
package com.ridesync_v2.ridesync.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Repo.UserRepo;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RideSyncOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public RideSyncOAuth2UserService(
            UserRepo userRepo,
            RestClient.Builder restClientBuilder
    ) {
        this.userRepo = userRepo;
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    // GitHub OAuth2 authentication
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);

        String provider = request.getClientRegistration()
                .getRegistrationId()
                .toLowerCase(Locale.ROOT);

        Map<String, Object> attributes = oauthUser.getAttributes();

        User user = switch (provider) {
            case "github" -> processGitHub(request, attributes);
            default -> throw oauthError(
                    "Unsupported OAuth provider: " + provider
            );
        };

        return new UserPrincipal(user, attributes);
    }

    // Google OpenID Connect authentication
    @Transactional
    public UserPrincipal loadGoogleUser(OidcUser oidcUser) {

        User user = processGoogle(oidcUser.getAttributes());

        return new UserPrincipal(user, oidcUser);
    }

    // Google account processing and linking
    private User processGoogle(Map<String, Object> attributes) {

        String subject = getRequiredString(attributes, "sub");
        String email = getRequiredString(attributes, "email");

        Object verified = attributes.get("email_verified");

        if (!Boolean.TRUE.equals(verified)) {
            throw oauthError("Google email is not verified");
        }

        // Find an existing Google-linked account.
        User existing = userRepo.findByGoogleSubject(subject)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        // Find an existing account by verified email.
        existing = userRepo.findByEmailIgnoreCase(email)
                .orElse(null);

        if (existing != null) {

            if (existing.getGoogleSubject() != null) {
                throw oauthError(
                        "This email is already linked to another Google account"
                );
            }

            existing.setGoogleSubject(subject);

            return userRepo.save(existing);
        }

        // Create a new account only when no matching account exists.
        User user = new User();
        user.setEmail(email);
        user.setGoogleSubject(subject);
        user.setUserName(uniqueUsername(
                attributes.get("name"),
                email
        ));

        return userRepo.save(user);
    }

    // GitHub account processing and linking
    private User processGitHub(
            OAuth2UserRequest request,
            Map<String, Object> attributes
    ) {

        String subject = getRequiredString(attributes, "id");

        // Find an existing GitHub-linked account.
        User existing = userRepo.findByGithubSubject(subject)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        // Retrieve GitHub's primary verified email.
        String email = fetchGitHubVerifiedEmail(
                request.getAccessToken().getTokenValue()
        );

        // Find an existing account by verified email.
        existing = userRepo.findByEmailIgnoreCase(email)
                .orElse(null);

        if (existing != null) {

            if (existing.getGithubSubject() != null) {
                throw oauthError(
                        "This email is already linked to another GitHub account"
                );
            }

            existing.setGithubSubject(subject);

            return userRepo.save(existing);
        }

        // Create a new account only when no matching account exists.
        User user = new User();
        user.setEmail(email);
        user.setGithubSubject(subject);
        user.setUserName(uniqueUsername(
                attributes.get("name"),
                email
        ));

        return userRepo.save(user);
    }

    // Retrieve primary verified GitHub email
    private String fetchGitHubVerifiedEmail(String accessToken) {

        try {
            String response = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + accessToken
                    )
                    .header(
                            HttpHeaders.ACCEPT,
                            "application/vnd.github+json"
                    )
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(String.class);

            List<Map<String, Object>> emails =
                    objectMapper.readValue(
                            response,
                            new TypeReference<>() {}
                    );

            return emails.stream()
                    .filter(email -> Boolean.TRUE.equals(
                            email.get("primary")
                    ))
                    .filter(email -> Boolean.TRUE.equals(
                            email.get("verified")
                    ))
                    .map(email -> email.get("email"))
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .findFirst()
                    .orElseThrow(() ->
                            oauthError(
                                    "GitHub has no primary verified email"
                            )
                    );

        } catch (OAuth2AuthenticationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw oauthError(
                    "Unable to retrieve verified GitHub email"
            );
        }
    }

    // Generate a unique username
    private String uniqueUsername(Object displayName, String email) {

        String base = displayName instanceof String name
                && StringUtils.hasText(name)
                ? name.trim()
                : email.substring(0, email.indexOf('@'));

        base = base.replaceAll("[^a-zA-Z0-9_]", "");

        if (!StringUtils.hasText(base)) {
            base = "ridesync";
        }

        base = base.substring(0, Math.min(base.length(), 85));

        String candidate = base;
        int suffix = 1;

        while (userRepo.existsByUserName(candidate)) {
            String ending = String.valueOf(suffix++);

            candidate = base.substring(
                    0,
                    Math.min(base.length(), 100 - ending.length())
            ) + ending;
        }

        return candidate;
    }

    // Validate required OAuth attributes
    private String getRequiredString(
            Map<String, Object> attributes,
            String key
    ) {

        Object value = attributes.get(key);

        if (value == null || !StringUtils.hasText(value.toString())) {
            throw oauthError(
                    "OAuth provider did not return required field: " + key
            );
        }

        return value.toString().trim();
    }

    // Standard OAuth error
    private OAuth2AuthenticationException oauthError(String message) {

        return new OAuth2AuthenticationException(
                new OAuth2Error("oauth_account_error"),
                message
        );
    }
}
