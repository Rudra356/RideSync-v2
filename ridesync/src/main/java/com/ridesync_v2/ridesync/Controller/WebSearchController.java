
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Service.WebSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class WebSearchController {

    private final WebSearchService webSearchService;

    public WebSearchController(WebSearchService webSearchService) {
        this.webSearchService = webSearchService;
    }

    @GetMapping
    public ResponseEntity<List<WebSearchService.SearchResult>> search(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                webSearchService.search(query)
        );
    }
}
