
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Service.OllamaChatService;
import com.ridesync_v2.ridesync.Service.WebPageFetchService;
import com.ridesync_v2.ridesync.Service.WebSearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger logger =
            LoggerFactory.getLogger(AiController.class);

    private final OllamaChatService ollamaChatService;
    private final WebSearchService webSearchService;
    private final WebPageFetchService webPageFetchService;

    public AiController(
            OllamaChatService ollamaChatService,
            WebSearchService webSearchService,
            WebPageFetchService webPageFetchService
    ) {
        this.ollamaChatService = ollamaChatService;
        this.webSearchService = webSearchService;
        this.webPageFetchService = webPageFetchService;
    }

    public record ChatRequest(
            @NotBlank(message = "Prompt cannot be empty")
            @Size(
                    max = 2000,
                    message = "Prompt cannot exceed 2000 characters"
            )
            String prompt
    ) {}

    public record ChatResponse(String response) {}

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request
    ) {
        String prompt = request.prompt().trim();

        List<WebPageFetchService.PageContent> pages = List.of();

        try {
            List<WebSearchService.SearchResult> searchResults =
                    webSearchService.search(prompt);

            if (!searchResults.isEmpty()) {
                pages = webPageFetchService.fetchPages(searchResults);
            }

            long fetchedCount = pages.stream()
                    .filter(WebPageFetchService.PageContent::fetched)
                    .count();

            logger.info(
                    "Vehicle research completed: {} search results, "
                            + "{} pages fetched successfully",
                    searchResults.size(),
                    fetchedCount
            );

        } catch (RuntimeException exception) {
            logger.warn(
                    "Vehicle web research failed. Continuing without "
                            + "retrieved page content: {}",
                    exception.getMessage()
            );
        }

        String answer = ollamaChatService.askWithFetchedPages(
                prompt,
                pages
        );

        return ResponseEntity.ok(new ChatResponse(answer));
    }
}
