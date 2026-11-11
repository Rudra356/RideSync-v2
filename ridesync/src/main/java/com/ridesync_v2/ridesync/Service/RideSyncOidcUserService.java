package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.UserPrincipal;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class RideSyncOidcUserService extends OidcUserService {

    private final RideSyncOAuth2UserService rideSyncOAuth2UserService;

    public RideSyncOidcUserService(
            RideSyncOAuth2UserService rideSyncOAuth2UserService
    ) {
        this.rideSyncOAuth2UserService = rideSyncOAuth2UserService;
    }

    @Override
    public UserPrincipal loadUser(OidcUserRequest request) {

        OidcUser oidcUser = super.loadUser(request);

        return rideSyncOAuth2UserService.loadGoogleUser(oidcUser);
    }
}
