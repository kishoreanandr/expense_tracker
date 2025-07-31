package com.expensetracker.expensetracker.Controller;

import com.expensetracker.expensetracker.Entity.UserPreferences;
import com.expensetracker.expensetracker.Service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {
    @Autowired
    private UserPreferencesService userPreferencesService;

    @GetMapping
    public ResponseEntity<UserPreferences> getPreferences() {
        return ResponseEntity.ok(userPreferencesService.getPreferencesForCurrentUser());
    }

    @PutMapping
    public ResponseEntity<UserPreferences> updatePreferences(@RequestBody UserPreferences preferences) {
        return ResponseEntity.ok(userPreferencesService.updatePreferencesForCurrentUser(preferences));
    }
} 