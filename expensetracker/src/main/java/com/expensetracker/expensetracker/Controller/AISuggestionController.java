package com.expensetracker.expensetracker.Controller;

import com.expensetracker.expensetracker.Service.AISuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AISuggestionController {
    @Autowired
    private AISuggestionService aiSuggestionService;

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions() {
        return ResponseEntity.ok(aiSuggestionService.getSuggestionsForCurrentUser());
    }
} 