
package com.ridesync_v2.ridesync.Service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class WebPageFetchService {

    private static final Logger log =
            LoggerFactory.getLogger(WebPageFetchService.class);

    private static final int MAX_URL_LENGTH = 2048;
    private static final int MAX_PAGE_TEXT_LENGTH = 6000;
    private static final int MAX_PAGES_PER_REQUEST = 5;
    private static final int MAX_RESPONSE_BYTES = 1_000_000;

    private static final Duration FETCH_TIMEOUT =
            Duration.ofSeconds(10);

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost",
            "metadata.google.internal",
            "metadata.google",
            "instance-data"
    );

    private static final String USER_AGENT =
            "RideSyncResearchBot/1.0";

    public record PageContent(
            String title,
            String url,
            String text,
            boolean fetched
    ) {}

    /**
     * Fetches readable HTML pages from search results.
     * Each page is handled independently so one failure does not
     * interrupt the remaining requests.
     */
    public List<PageContent> fetchPages(
            List<WebSearchService.SearchResult> searchResults
    ) {

        if (searchResults == null || searchResults.isEmpty()) {
            return List.of();
        }

        List<PageContent> pages = new ArrayList<>();

        int limit = Math.min(
                searchResults.size(),
                MAX_PAGES_PER_REQUEST
        );

        for (int i = 0; i < limit; i++) {

            WebSearchService.SearchResult result = searchResults.get(i);

            if (result == null || result.url() == null) {
                continue;
            }

            try {
                URI uri = validatePublicHttpUrl(result.url());

                Connection.Response response = Jsoup.connect(
                                uri.toString()
                        )
                        .userAgent(USER_AGENT)
                        .timeout((int) FETCH_TIMEOUT.toMillis())
                        .maxBodySize(MAX_RESPONSE_BYTES)
                        .followRedirects(false)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(false)
                        .header("Accept", "text/html")
                        .header("Accept-Encoding", "gzip")
                        .method(Connection.Method.GET)
                        .execute();

                int status = response.statusCode();

                if (status >= 300 && status < 400) {
                    pages.add(failedPage(
                            result,
                            "Redirect response rejected. "
                                    + "Redirect destinations require "
                                    + "independent validation."
                    ));
                    continue;
                }

                if (status < 200 || status >= 300) {
                    pages.add(failedPage(
                            result,
                            "Page returned HTTP " + status
                    ));
                    continue;
                }

                String contentType = response.contentType();

                if (contentType == null
                        || !contentType.toLowerCase(Locale.ROOT)
                        .contains("text/html")) {

                    pages.add(failedPage(
                            result,
                            "Page did not return HTML content"
                    ));
                    continue;
                }

                Document document = response.parse();

                String title = extractTitle(
                        document,
                        result.title()
                );

                String text = extractReadableText(document);

                if (text.isBlank()) {
                    pages.add(failedPage(
                            result,
                            "No readable page text was extracted"
                    ));
                    continue;
                }

                pages.add(new PageContent(
                        title,
                        uri.toString(),
                        limitText(text, MAX_PAGE_TEXT_LENGTH),
                        true
                ));

                log.info(
                        "Research page fetched successfully: host={}, "
                                + "characters={}",
                        uri.getHost(),
                        text.length()
                );

            } catch (IOException | IllegalArgumentException
                     | SecurityException exception) {

                log.warn(
                        "Research page fetch failed: url={}, reason={}",
                        result.url(),
                        exception.getMessage()
                );

                pages.add(failedPage(
                        result,
                        "Page could not be fetched or validated"
                ));
            }
        }

        log.info(
                "Research page fetching completed: requested={}, returned={}, fetched={}",
                limit,
                pages.size(),
                pages.stream().filter(PageContent::fetched).count()
        );

        return List.copyOf(pages);
    }

    /**
     * Extracts the main readable content while removing common
     * navigation, advertising, scripts and unrelated page elements.
     */
    private String extractReadableText(Document document) {

        document.select(
                "script, style, noscript, iframe, svg, "
                        + "nav, footer, header, form, button, "
                        + "input, textarea, select, "
                        + "aside, dialog, canvas, "
                        + "[hidden], [aria-hidden=true], "
                        + ".advertisement, .ads, .sidebar, "
                        + ".cookie-banner, .newsletter, "
                        + ".social-share, .related-articles"
        ).remove();

        Element contentRoot = document.selectFirst(
                "article, main, [role=main], "
                        + ".article-content, .post-content, "
                        + ".entry-content, .story-content"
        );

        if (contentRoot == null) {
            contentRoot = document.body();
        }

        if (contentRoot == null) {
            return "";
        }

        return contentRoot.text().trim();
    }

    private String extractTitle(
            Document document,
            String fallback
    ) {

        String title = document.title();

        if (title == null || title.isBlank()) {
            return fallback == null || fallback.isBlank()
                    ? "Untitled page"
                    : fallback.trim();
        }

        return title.trim();
    }

    /**
     * Performs preliminary URL and DNS validation.
     *
     * Important: DNS validation followed by a separate HTTP connection
     * does not completely prevent DNS rebinding. Network-level egress
     * restrictions are also required for production deployment.
     */
    private URI validatePublicHttpUrl(String rawUrl) {

        if (rawUrl == null || rawUrl.isBlank()
                || rawUrl.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid URL or URL exceeds permitted length"
            );
        }

        URI uri = URI.create(rawUrl).normalize();

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (scheme == null
                || (!scheme.equalsIgnoreCase("http")
                && !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException(
                    "Only HTTP and HTTPS URLs are permitted"
            );
        }

        if (host == null
                || host.isBlank()
                || uri.getUserInfo() != null) {
            throw new IllegalArgumentException(
                    "Invalid destination URL"
            );
        }

        if (uri.getPort() != -1
                && uri.getPort() != 80
                && uri.getPort() != 443) {
            throw new SecurityException(
                    "Non-standard destination ports are not permitted"
            );
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);

        if (BLOCKED_HOSTS.contains(normalizedHost)
                || normalizedHost.endsWith(".localhost")
                || normalizedHost.endsWith(".local")
                || normalizedHost.endsWith(".internal")) {
            throw new SecurityException(
                    "Local or internal hostnames are not permitted"
            );
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);

            if (addresses.length == 0) {
                throw new SecurityException(
                        "Hostname did not resolve to an IP address"
                );
            }

            for (InetAddress address : addresses) {
                if (!isPublicAddress(address)) {
                    throw new SecurityException(
                            "Private, reserved or local IP addresses "
                                    + "are not permitted"
                    );
                }
            }

        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(
                    "Unable to resolve destination hostname",
                    exception
            );
        }

        return uri;
    }

    private boolean isPublicAddress(InetAddress address) {

        return !address.isAnyLocalAddress()
                && !address.isLoopbackAddress()
                && !address.isLinkLocalAddress()
                && !address.isSiteLocalAddress()
                && !address.isMulticastAddress();
    }

    private String limitText(String text, int maxLength) {

        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }

    private PageContent failedPage(
            WebSearchService.SearchResult result,
            String reason
    ) {

        return new PageContent(
                result.title() == null
                        ? "Unavailable page"
                        : result.title(),
                result.url(),
                reason,
                false
        );
    }
}
