
package com.ridesync_v2.ridesync.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class WebSearchService {

    private static final Logger log =
            LoggerFactory.getLogger(WebSearchService.class);

    private static final int MAX_QUERY_LENGTH = 300;
    private static final int MAX_RESULTS = 10;

    private final String searxngUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WebSearchService(
            ObjectMapper objectMapper,
            @Value("${ridesync.search.searxng-url:http://127.0.0.1:8888/search}")
            String searxngUrl
    ) {
        this.objectMapper = objectMapper;
        this.searxngUrl = validateSearchEndpoint(searxngUrl);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public record SearchResult(
            String title,
            String url,
            String snippet
    ) {}

    public List<SearchResult> search(String query) {

        validateQuery(query);

        String encodedQuery = URLEncoder.encode(
                query.trim(),
                StandardCharsets.UTF_8
        );

        URI uri = URI.create(
                searxngUrl
                        + "?q=" + encodedQuery
                        + "&format=json"
                        + "&language=en"
                        + "&safesearch=0"
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8
                    )
            );

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "SearXNG returned HTTP "
                                + response.statusCode()
                );
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode resultNodes = root.path("results");

            if (!resultNodes.isArray()) {
                throw new IllegalStateException(
                        "Invalid SearXNG response: results array missing"
                );
            }

            List<SearchResult> results = new ArrayList<>();
            Set<String> seenUrls = new HashSet<>();

            for (JsonNode result : resultNodes) {

                if (results.size() >= 50) {
                    break;
                }

                String title = result.path("title")
                        .asText("")
                        .trim();

                String rawUrl = result.path("url")
                        .asText("")
                        .trim();

                String snippet = result.path("content")
                        .asText("")
                        .trim();

                String validUrl = validateDestinationUrl(rawUrl);

                if (title.isBlank() || validUrl == null) {
                    continue;
                }

                String normalizedUrl = normalizeUrl(validUrl);

                if (!seenUrls.add(normalizedUrl)) {
                    continue;
                }

                results.add(
                        new SearchResult(
                                limit(title, 300),
                                validUrl,
                                limit(snippet, 1500)
                        )
                );
            }

            results.sort(
                    Comparator.comparingInt(
                            (SearchResult result) ->
                                    calculateSourcePriority(result)
                    ).reversed()
            );

            List<SearchResult> finalResults = results.stream()
                    .limit(MAX_RESULTS)
                    .toList();

            log.info(
                    "SearXNG search completed. Query length: {}, "
                            + "valid results: {}, selected results: {}",
                    query.length(),
                    results.size(),
                    finalResults.size()
            );

            return finalResults;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "SearXNG search was interrupted",
                    e
            );

        } catch (IOException e) {

            log.error("Unable to parse SearXNG response", e);

            throw new IllegalStateException(
                    "Unable to parse SearXNG JSON response",
                    e
            );

        } catch (RuntimeException e) {

            log.error("SearXNG search failed", e);

            throw e;
        }
    }

    /**
     * Gives preference to likely primary and technical sources.
     * This is a heuristic, not proof of source authority.
     */
    private int calculateSourcePriority(SearchResult result) {

        String host = URI.create(result.url())
                .getHost()
                .toLowerCase(Locale.ROOT);

        String title = result.title()
                .toLowerCase(Locale.ROOT);

        String snippet = result.snippet()
                .toLowerCase(Locale.ROOT);

        String combined = title + " " + snippet;

        int score = 0;

        if (isManufacturerDomain(host)) {
            score += 100;
        }

        if (containsAny(
                combined,
                "technical specifications",
                "technical data",
                "owner's manual",
                "owners manual",
                "service manual",
                "workshop manual",
                "brochure",
                "specification sheet"
        )) {
            score += 30;
        }

        if (containsAny(
                host,
                "autocar",
                "caranddriver",
                "motortrend",
                "evo.co.uk",
                "topgear",
                "automobile-catalog",
                "automobile-propre"
        )) {
            score += 15;
        }

        if (containsAny(
                host,
                "team-bhp",
                "reddit",
                "quora"
        )) {
            score -= 10;
        }

        return score;
    }

    private boolean isManufacturerDomain(String host) {

        return containsAny(
                host,
                "ferrari.com",
                "porsche.com",
                "bmw.com",
                "mercedes-benz.com",
                "audi.com",
                "volkswagen.com",
                "ford.com",
                "toyota.com",
                "honda.com",
                "hyundai.com",
                "kia.com",
                "mahindra.com",
                "tatamotors.com",
                "marutisuzuki.com",
                "suzukimotorcycle.co.in",
                "global.honda"
        );
    }

    private boolean containsAny(
            String value,
            String... candidates
    ) {
        for (String candidate : candidates) {
            if (value.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    private String normalizeUrl(String rawUrl) {

        try {
            URI uri = URI.create(rawUrl);

            String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
            String host = uri.getHost().toLowerCase(Locale.ROOT);

            int port = uri.getPort();

            if (("http".equals(scheme) && port == 80)
                    || ("https".equals(scheme) && port == 443)) {
                port = -1;
            }

            String path = uri.getRawPath();

            if (path == null || path.isBlank()) {
                path = "/";
            }

            return scheme + "://" + host
                    + (port == -1 ? "" : ":" + port)
                    + path
                    + (uri.getRawQuery() == null
                    ? "" : "?" + uri.getRawQuery());

        } catch (IllegalArgumentException e) {
            return rawUrl;
        }
    }

    private String validateDestinationUrl(String rawUrl) {

        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(rawUrl);

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null) {
                return null;
            }

            if (!"http".equalsIgnoreCase(scheme)
                    && !"https".equalsIgnoreCase(scheme)) {
                return null;
            }

            if (uri.getUserInfo() != null) {
                return null;
            }

            if (host.equalsIgnoreCase("localhost")
                    || host.equalsIgnoreCase("127.0.0.1")
                    || host.equalsIgnoreCase("0.0.0.0")
                    || host.equalsIgnoreCase("::1")) {
                return null;
            }

            return uri.toString();

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String validateSearchEndpoint(String endpoint) {

        URI uri = URI.create(endpoint);

        if (!"http".equalsIgnoreCase(uri.getScheme())
                && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "SearXNG endpoint must use HTTP or HTTPS"
            );
        }

        if (uri.getHost() == null
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new IllegalArgumentException(
                    "Invalid SearXNG endpoint"
            );
        }

        return endpoint.replaceAll("/+$", "");
    }

    private String limit(String value, int maxLength) {

        if (value == null) {
            return "";
        }

        String normalized = value.trim();

        if (normalized.length() <= maxLength) {
            return normalized;
        }

        return normalized.substring(0, maxLength) + "...";
    }

    private void validateQuery(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        if (query.length() > MAX_QUERY_LENGTH) {
            throw new IllegalArgumentException(
                    "Search query cannot exceed "
                            + MAX_QUERY_LENGTH + " characters"
            );
        }
    }
}
