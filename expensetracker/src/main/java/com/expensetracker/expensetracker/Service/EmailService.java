package com.expensetracker.expensetracker.Service;


import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Entity.UserPreferences;
import com.expensetracker.expensetracker.Respository.UserPreferencesRepository;
import com.expensetracker.expensetracker.Respository.UserRepository;
import com.expensetracker.expensetracker.Respository.ExpenseRepository;
import com.expensetracker.expensetracker.Respository.BudgetRepository;
import com.expensetracker.expensetracker.Entity.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.expensetracker.expensetracker.Entity.Category;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    public void sendWelcomeEmail(User user) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Welcome to Expense Tracker!");

        String htmlContent = buildWelcomeEmailContent(user);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    public void sendExpenseSummaryEmail(User user, List<Expense> expenses, BigDecimal totalAmount,
                                        String period) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Your " + period + " Expense Summary");

        String htmlContent = buildExpenseSummaryEmailContent(user, expenses, totalAmount, period);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    public void sendBudgetAlertEmail(User user, String categoryName, BigDecimal spentAmount,
                                     BigDecimal budgetAmount, double percentage) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Budget Alert: " + categoryName);

        String htmlContent = buildBudgetAlertEmailContent(user, categoryName, spentAmount, budgetAmount, percentage);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    public void sendTestEmail(User user) throws MessagingException {
        System.out.println("Attempting to send test email to: " + user.getEmail());
        
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Test Email - Budget Alert System");

        String htmlContent = "<!DOCTYPE html><html><head><style>body { font-family: Arial, sans-serif; }</style></head><body><h2>Test Email</h2><p>This is a test email to verify the email system is working.</p><p>Time: " + java.time.LocalDateTime.now() + "</p></body></html>";
        helper.setText(htmlContent, true);

        emailSender.send(message);
        System.out.println("Test email sent successfully to: " + user.getEmail());
    }

        // Add this after buildWelcomeEmailContent and before the final closing brace of the class

    private String buildWelcomeEmailContent(User user) {
        return "<!DOCTYPE html><html><head><style>body { font-family: Arial, sans-serif; color: #333; } .container { max-width: 600px; margin: 0 auto; padding: 20px; } .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; } .content { padding: 20px; background-color: #f9f9f9; } .footer { background-color: #333; color: white; padding: 10px; text-align: center; }</style></head><body><div class='container'><div class='header'><h2>Welcome to Expense Tracker!</h2></div><div class='content'><p>Hello, <b>" + (user.getFirstName() != null ? user.getFirstName() : user.getUsername()) + "</b>!</p><p>Thank you for registering. Start tracking your expenses and managing your budget today!</p></div><div class='footer'>Expense Tracker &copy; " + java.time.LocalDate.now().getYear() + "</div></div></body></html>";
    }

    private String buildExpenseSummaryEmailContent(User user, List<Expense> expenses, BigDecimal totalAmount, String period) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><style>");
        sb.append("body { font-family: Arial, sans-serif; color: #333; } .container { max-width: 600px; margin: 0 auto; padding: 20px; } .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; } .content { padding: 20px; background-color: #f9f9f9; } .footer { background-color: #333; color: white; padding: 10px; text-align: center; } table { width: 100%; border-collapse: collapse; margin-top: 20px; } th, td { border: 1px solid #ddd; padding: 8px; text-align: left; } th { background-color: #4CAF50; color: white; } ");
        sb.append("</style></head><body><div class='container'><div class='header'><h2>Expense Summary (" + period.substring(0, 1).toUpperCase() + period.substring(1) + ")</h2></div><div class='content'>");
        sb.append("<p>Hello, <b>").append(user.getFirstName() != null ? user.getFirstName() : user.getUsername()).append("</b>!</p>");
        sb.append("<p>Here is your expense summary for this ").append(period).append(":</p>");
        sb.append("<table><tr><th>Date</th><th>Category</th><th>Amount</th><th>Note</th></tr>");
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Expense e : expenses) {
            sb.append("<tr><td>").append(e.getExpenseDate().format(fmt)).append("</td><td>")
              .append(e.getCategory() != null ? e.getCategory().getName() : "").append("</td><td>")
              .append(e.getAmount()).append("</td><td>")
              .append(e.getNote() != null ? e.getNote() : "").append("</td></tr>");
        }
        sb.append("</table>");
        sb.append("<p><b>Total Spent: </b> ").append(totalAmount).append("</p>");
        sb.append("</div><div class='footer'>Expense Tracker &copy; ").append(java.time.LocalDate.now().getYear()).append("</div></div></body></html>");
        return sb.toString();
    }

    private String buildBudgetAlertEmailContent(User user, String categoryName, BigDecimal spentAmount, BigDecimal budgetAmount, double percentage) {
        return "<!DOCTYPE html><html><head><style>body { font-family: Arial, sans-serif; color: #333; } .container { max-width: 600px; margin: 0 auto; padding: 20px; } .header { background-color: #e53935; color: white; padding: 20px; text-align: center; } .content { padding: 20px; background-color: #f9f9f9; } .footer { background-color: #333; color: white; padding: 10px; text-align: center; }</style></head><body><div class='container'><div class='header'><h2>Budget Alert!</h2></div><div class='content'><p>Hello, <b>" + (user.getFirstName() != null ? user.getFirstName() : user.getUsername()) + "</b>!</p><p>You have exceeded your budget for <b>" + categoryName + "</b>.</p><p><b>Budgeted: </b>" + budgetAmount + "<br/><b>Spent: </b>" + spentAmount + "<br/><b>Percent: </b>" + String.format("%.2f", percentage * 100) + "%</p></div><div class='footer'>Expense Tracker &copy; " + java.time.LocalDate.now().getYear() + "</div></div></body></html>";
    }

    // Scheduled summary emails
    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
    public void sendDailySummaryEmails() {
        sendSummaryEmailsByFrequency(UserPreferences.NotificationFrequency.DAILY, "daily");
    }

    @Scheduled(cron = "0 0 8 * * MON") // Every Monday at 8 AM
    public void sendWeeklySummaryEmails() {
        sendSummaryEmailsByFrequency(UserPreferences.NotificationFrequency.WEEKLY, "weekly");
    }

    @Scheduled(cron = "0 0 8 1 * *") // First day of month at 8 AM
    public void sendMonthlySummaryEmails() {
        sendSummaryEmailsByFrequency(UserPreferences.NotificationFrequency.MONTHLY, "monthly");
    }

    @Scheduled(cron = "0 0 8 1 1 *") // First day of year at 8 AM
    public void sendYearlySummaryEmails() {
        sendSummaryEmailsByFrequency(UserPreferences.NotificationFrequency.YEARLY, "yearly");
    }

    // Check for over-budget situations every minute (for testing)
    @Scheduled(cron = "0 * * * * *") // Every minute
    public void checkBudgetAlerts() {
        System.out.println("=== AUTOMATIC SCHEDULED BUDGET ALERT CHECK - " + java.time.LocalDateTime.now() + " ===");
        List<UserPreferences> prefs = userPreferencesRepository.findByOverBudgetAlertsWithUser(true);
        System.out.println("Found " + prefs.size() + " users with budget alerts enabled");
        
        if (prefs.isEmpty()) {
            System.out.println("No users have budget alerts enabled!");
            return;
        }
        
        for (UserPreferences pref : prefs) {
            User user = pref.getUser();
            if (user == null) {
                System.out.println("User is null for preferences ID: " + pref.getId());
                continue;
            }
            
            System.out.println("Checking budget alerts for user: " + user.getEmail());
            System.out.println("User preferences - Email notifications: " + pref.getEmailNotifications() + ", Over budget alerts: " + pref.getOverBudgetAlerts());
            
            try {
                // Check overall budgets
                List<Budget> overallBudgets = budgetRepository.findByUserAndBudgetType(user, Budget.BudgetType.OVERALL);
                System.out.println("Found " + overallBudgets.size() + " overall budgets");
                for (Budget budget : overallBudgets) {
                    checkAndSendBudgetAlert(user, budget, null);
                }
                
                // Check category budgets
                List<Budget> categoryBudgets = budgetRepository.findByUserAndBudgetTypeWithCategory(user, Budget.BudgetType.CATEGORY);
                System.out.println("Found " + categoryBudgets.size() + " category budgets");
                for (Budget budget : categoryBudgets) {
                    checkAndSendBudgetAlert(user, budget, budget.getCategory());
                }
            } catch (Exception e) {
                System.err.println("Failed to check budget alerts for " + user.getEmail() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("=== AUTOMATIC BUDGET ALERT CHECK COMPLETE ===");
    }

    private void checkAndSendBudgetAlert(User user, Budget budget, Category category) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate start;
            
            // Determine the start date based on budget period
            switch (budget.getPeriodType()) {
                case WEEKLY -> start = now.minusDays(6);
                case MONTHLY -> start = now.withDayOfMonth(1);
                case YEARLY -> start = now.withDayOfYear(1);
                default -> start = now;
            }
            
            System.out.println("  Date Range: " + start + " to " + now);
            System.out.println("  Budget Period: " + budget.getPeriodType());
            
            // Get expenses for the period
            List<Expense> expenses;
            if (category != null) {
                expenses = expenseRepository.findByUserAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(
                    user, category, start, now);
                System.out.println("  Found " + expenses.size() + " expenses for category: " + category.getName());
            } else {
                expenses = expenseRepository.findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(user, start, now);
                System.out.println("  Found " + expenses.size() + " total expenses");
            }
            
            // Calculate total spent
            BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Check if over budget (100% threshold for alert)
            double percentage = totalSpent.doubleValue() / budget.getAmount().doubleValue();
            
            System.out.println("Budget Check for " + user.getEmail() + ":");
            System.out.println("  Budget Amount: " + budget.getAmount());
            System.out.println("  Total Spent: " + totalSpent);
            System.out.println("  Percentage: " + (percentage * 100) + "%");
            System.out.println("  Category: " + (category != null ? category.getName() : "Overall"));
            
            if (percentage >= 1.0) { // Changed from 0.8 to 1.0 (100%)
                String categoryName = category != null ? category.getName() : "Overall Budget";
                System.out.println("  SENDING BUDGET ALERT EMAIL!");
                sendBudgetAlertEmail(user, categoryName, totalSpent, budget.getAmount(), percentage);
            } else {
                System.out.println("  No alert needed (under 100%)");
            }
        } catch (Exception e) {
            System.err.println("Failed to send budget alert for user " + user.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendSummaryEmailsByFrequency(UserPreferences.NotificationFrequency frequency, String period) {
        List<UserPreferences> prefs = userPreferencesRepository.findByEmailNotificationsAndFrequency(frequency);
        for (UserPreferences pref : prefs) {
            User user = pref.getUser();
            if (user == null) continue;
            try {
                // Determine date range for the period
                LocalDate now = LocalDate.now();
                LocalDate start;
                switch (frequency) {
                    case DAILY -> start = now;
                    case WEEKLY -> start = now.minusDays(6);
                    case MONTHLY -> start = now.withDayOfMonth(1);
                    case YEARLY -> start = now.withDayOfYear(1);
                    default -> start = now;
                }
                List<Expense> expenses = expenseRepository.findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(user, start, now);
                BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                sendExpenseSummaryEmail(user, expenses, total, period);
            } catch (Exception e) {
                // Log error (could use a logger)
                System.err.println("Failed to send summary email to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    // Test scheduled task - runs every minute
    @Scheduled(cron = "0 * * * * *") // Every minute
    public void testScheduledTask() {
        System.out.println("=== SCHEDULED TASK TEST - " + java.time.LocalDateTime.now() + " ===");
    }
}