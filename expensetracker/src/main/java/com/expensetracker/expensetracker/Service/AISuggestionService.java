package com.expensetracker.expensetracker.Service;

import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Entity.Budget;
import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Respository.ExpenseRepository;
import com.expensetracker.expensetracker.Respository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AISuggestionService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    public List<String> getSuggestionsForCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Expense> expenses = expenseRepository.findByUser(user);
        List<Budget> budgets = budgetRepository.findByUser(user);
        List<String> suggestions = new ArrayList<>();
        // Simple rule-based logic
        for (Budget budget : budgets) {
            BigDecimal spent = expenses.stream()
                .filter(e -> e.getCategory().equals(budget.getCategory()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (spent.compareTo(budget.getAmount()) > 0) {
                suggestions.add("You’re spending too much on " + budget.getCategory().getName() + " this period.");
            } else if (spent.compareTo(budget.getAmount().multiply(new BigDecimal("0.8"))) > 0) {
                suggestions.add("Warning: You are close to exceeding your budget for " + budget.getCategory().getName() + ".");
            }
        }
        // Example static advice
        if (suggestions.isEmpty()) {
            suggestions.add("Great job! You are within your budgets.");
        }
        return suggestions;
    }
} 