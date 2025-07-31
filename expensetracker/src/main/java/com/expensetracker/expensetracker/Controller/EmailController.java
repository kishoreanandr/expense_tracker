package com.expensetracker.expensetracker.Controller;

import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.expensetracker.expensetracker.Entity.UserPreferences;
import com.expensetracker.expensetracker.Respository.UserPreferencesRepository;
import com.expensetracker.expensetracker.Entity.Budget;
import com.expensetracker.expensetracker.Respository.BudgetRepository;
import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Respository.ExpenseRepository;
import com.expensetracker.expensetracker.Respository.UserRepository;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @PostMapping("/send-summary")
    public ResponseEntity<String> sendExpenseSummary() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // This would need to be implemented to send a summary email
            return ResponseEntity.ok("Summary email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send summary email: " + e.getMessage());
        }
    }

    @PostMapping("/test-budget-alert")
    public ResponseEntity<String> testBudgetAlert() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // Send a test budget alert email
            emailService.sendBudgetAlertEmail(user, "Test Category", new java.math.BigDecimal("1500"), 
                                           new java.math.BigDecimal("1000"), 1.5);
            return ResponseEntity.ok("Test budget alert email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test budget alert: " + e.getMessage());
        }
    }

    @PostMapping("/trigger-budget-check")
    public ResponseEntity<String> triggerBudgetCheck() {
        System.out.println("=== TRIGGER BUDGET CHECK ENDPOINT CALLED ===");
        try {
            // Manually trigger the budget check
            System.out.println("Calling emailService.checkBudgetAlerts()...");
            emailService.checkBudgetAlerts();
            System.out.println("Budget check completed successfully");
            return ResponseEntity.ok("Budget check triggered successfully");
        } catch (Exception e) {
            System.err.println("Error in triggerBudgetCheck: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to trigger budget check: " + e.getMessage());
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // Send a simple test email
            emailService.sendWelcomeEmail(user);
            return ResponseEntity.ok("Test email sent successfully to " + user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }

    @PostMapping("/test-email-simple")
    public ResponseEntity<String> testEmailSimple() {
        System.out.println("=== TEST EMAIL SIMPLE ===");
        try {
            // Get the first user from database
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.ok("No users found in database");
            }
            
            User user = users.get(0);
            System.out.println("Testing email for user: " + user.getEmail());
            
            emailService.sendTestEmail(user);
            return ResponseEntity.ok("Test email sent successfully to " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error in testEmailSimple: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }

    @PostMapping("/debug-budget-check")
    public ResponseEntity<String> debugBudgetCheck() {
        System.out.println("=== DEBUG BUDGET CHECK ENDPOINT CALLED ===");
        try {
            // Get all users with budget alerts enabled
            List<UserPreferences> prefs = userPreferencesRepository.findByOverBudgetAlertsWithUser(true);
            System.out.println("Found " + prefs.size() + " users with budget alerts enabled");
            
            if (prefs.isEmpty()) {
                return ResponseEntity.ok("No users found with budget alerts enabled");
            }
            
            for (UserPreferences pref : prefs) {
                System.out.println("Checking preferences ID: " + pref.getId());
                System.out.println("Over budget alerts: " + pref.getOverBudgetAlerts());
                System.out.println("Email notifications: " + pref.getEmailNotifications());
                
                User user = pref.getUser();
                if (user == null) {
                    System.out.println("User is null for preferences ID: " + pref.getId());
                    continue;
                }
                
                System.out.println("User found: " + user.getEmail());
                
                // Check budgets for this user
                List<Budget> budgets = budgetRepository.findByUser(user);
                System.out.println("Found " + budgets.size() + " budgets for user");
                
                for (Budget budget : budgets) {
                    System.out.println("Budget: " + budget.getAmount() + " (" + budget.getBudgetType() + ")");
                }
            }
            
            return ResponseEntity.ok("Debug check completed. Check console for details.");
        } catch (Exception e) {
            System.err.println("Error in debugBudgetCheck: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to debug budget check: " + e.getMessage());
        }
    }

    @PostMapping("/test-complete-flow")
    public ResponseEntity<String> testCompleteFlow() {
        System.out.println("=== COMPLETE FLOW TEST ===");
        try {
            // Step 1: Check if any users exist
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users in database: " + allUsers.size());
            
            if (allUsers.isEmpty()) {
                return ResponseEntity.ok("No users found in database");
            }
            
            // Step 2: Check user preferences
            List<UserPreferences> prefs = userPreferencesRepository.findByOverBudgetAlertsWithUser(true);
            System.out.println("Users with budget alerts enabled: " + prefs.size());
            
            if (prefs.isEmpty()) {
                return ResponseEntity.ok("No users have budget alerts enabled");
            }
            
            // Step 3: Check budgets for each user
            for (UserPreferences pref : prefs) {
                User user = pref.getUser();
                System.out.println("\n--- Testing User: " + user.getEmail() + " ---");
                
                // Check budgets
                List<Budget> allBudgets = budgetRepository.findByUser(user);
                System.out.println("Total budgets for user: " + allBudgets.size());
                
                for (Budget budget : allBudgets) {
                    System.out.println("Budget: " + budget.getAmount() + " (" + budget.getBudgetType() + ", " + budget.getPeriodType() + ")");
                    
                    // Check expenses for this budget period
                    LocalDate now = LocalDate.now();
                    LocalDate start;
                    
                    switch (budget.getPeriodType()) {
                        case WEEKLY -> start = now.minusDays(6);
                        case MONTHLY -> start = now.withDayOfMonth(1);
                        case YEARLY -> start = now.withDayOfYear(1);
                        default -> start = now;
                    }
                    
                    System.out.println("Date range: " + start + " to " + now);
                    
                    List<Expense> expenses;
                    if (budget.getBudgetType() == Budget.BudgetType.CATEGORY && budget.getCategory() != null) {
                        expenses = expenseRepository.findByUserAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(
                            user, budget.getCategory(), start, now);
                        System.out.println("Category expenses: " + expenses.size());
                    } else {
                        expenses = expenseRepository.findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(user, start, now);
                        System.out.println("Total expenses: " + expenses.size());
                    }
                    
                    // Calculate total spent
                    BigDecimal totalSpent = expenses.stream()
                        .map(Expense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    double percentage = totalSpent.doubleValue() / budget.getAmount().doubleValue();
                    System.out.println("Total spent: " + totalSpent + " / " + budget.getAmount() + " = " + (percentage * 100) + "%");
                    
                    if (percentage >= 1.0) {
                        System.out.println("*** OVER BUDGET - SHOULD SEND EMAIL ***");
                    } else {
                        System.out.println("Under budget - no email needed");
                    }
                }
            }
            
            return ResponseEntity.ok("Complete flow test finished. Check console for details.");
        } catch (Exception e) {
            System.err.println("Error in testCompleteFlow: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to test complete flow: " + e.getMessage());
        }
    }

    @PostMapping("/test-date-calculations")
    public ResponseEntity<String> testDateCalculations() {
        System.out.println("=== TEST DATE CALCULATIONS ===");
        try {
            LocalDate now = LocalDate.now();
            System.out.println("Current date: " + now);
            
            // Test different period calculations
            LocalDate weeklyStart = now.minusDays(6);
            LocalDate monthlyStart = now.withDayOfMonth(1);
            LocalDate yearlyStart = now.withDayOfYear(1);
            
            System.out.println("Weekly range: " + weeklyStart + " to " + now);
            System.out.println("Monthly range: " + monthlyStart + " to " + now);
            System.out.println("Yearly range: " + yearlyStart + " to " + now);
            
            // Get all expenses and show their dates
            List<Expense> allExpenses = expenseRepository.findAll();
            System.out.println("Total expenses in database: " + allExpenses.size());
            
            for (Expense expense : allExpenses) {
                System.out.println("Expense: " + expense.getAmount() + " on " + expense.getExpenseDate() + 
                                 " (User: " + expense.getUser().getEmail() + ", Category: " + 
                                 (expense.getCategory() != null ? expense.getCategory().getName() : "null") + ")");
            }
            
            return ResponseEntity.ok("Date calculations test completed. Check console for details.");
        } catch (Exception e) {
            System.err.println("Error in testDateCalculations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to test date calculations: " + e.getMessage());
        }
    }

    @PostMapping("/test-specific-user")
    public ResponseEntity<String> testSpecificUser() {
        System.out.println("=== TEST SPECIFIC USER WITH ₹100 BUDGET ===");
        try {
            // Find user with ₹100 budget
            List<Budget> budgets = budgetRepository.findAll();
            for (Budget budget : budgets) {
                if (budget.getAmount().equals(new java.math.BigDecimal("100.00"))) {
                    User user = budget.getUser();
                    System.out.println("Found user with ₹100 budget: " + user.getEmail());
                    
                    // Check expenses for this user
                    LocalDate now = LocalDate.now();
                    LocalDate start = now.withDayOfMonth(1); // Monthly
                    
                    List<Expense> expenses = expenseRepository.findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(user, start, now);
                    BigDecimal totalSpent = expenses.stream()
                        .map(Expense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    double percentage = totalSpent.doubleValue() / budget.getAmount().doubleValue();
                    
                    System.out.println("Budget: " + budget.getAmount());
                    System.out.println("Total Spent: " + totalSpent);
                    System.out.println("Percentage: " + (percentage * 100) + "%");
                    System.out.println("Number of expenses: " + expenses.size());
                    
                    if (percentage >= 1.0) {
                        System.out.println("*** SHOULD SEND EMAIL - OVER BUDGET ***");
                        // Try to send email
                        try {
                            emailService.sendBudgetAlertEmail(user, "Overall Budget", totalSpent, budget.getAmount(), percentage);
                            System.out.println("*** EMAIL SENT SUCCESSFULLY ***");
                        } catch (Exception e) {
                            System.err.println("Failed to send email: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Under budget - no email needed");
                    }
                }
            }
            
            return ResponseEntity.ok("Specific user test completed. Check console for details.");
        } catch (Exception e) {
            System.err.println("Error in testSpecificUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to test specific user: " + e.getMessage());
        }
    }

    @PostMapping("/test-user-preferences")
    public ResponseEntity<String> testUserPreferences() {
        System.out.println("=== TEST USER PREFERENCES ===");
        try {
            // Get all users and their preferences
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users: " + allUsers.size());
            
            for (User user : allUsers) {
                System.out.println("\n--- User: " + user.getEmail() + " ---");
                
                // Get user preferences
                Optional<UserPreferences> prefsOpt = userPreferencesRepository.findByUser(user);
                if (prefsOpt.isPresent()) {
                    UserPreferences prefs = prefsOpt.get();
                    System.out.println("  Email notifications: " + prefs.getEmailNotifications());
                    System.out.println("  Over budget alerts: " + prefs.getOverBudgetAlerts());
                    System.out.println("  Notification frequency: " + prefs.getNotificationFrequency());
                } else {
                    System.out.println("  No preferences found for this user");
                }
                
                // Get user budgets
                List<Budget> budgets = budgetRepository.findByUser(user);
                System.out.println("  Budgets: " + budgets.size());
                for (Budget budget : budgets) {
                    System.out.println("    Budget: " + budget.getAmount() + " (" + budget.getBudgetType() + ")");
                }
            }
            
            return ResponseEntity.ok("User preferences test completed. Check console for details.");
        } catch (Exception e) {
            System.err.println("Error in testUserPreferences: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to test user preferences: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        System.out.println("=== HEALTH CHECK ENDPOINT CALLED ===");
        return ResponseEntity.ok("Backend is running!");
    }
} 