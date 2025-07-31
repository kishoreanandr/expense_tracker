package com.expensetracker.expensetracker.Controller;

import com.expensetracker.expensetracker.Entity.Category;
import com.expensetracker.expensetracker.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategoriesForCurrentUser() {
        return ResponseEntity.ok(categoryService.getAllCategoriesForCurrentUser());
    }
} 