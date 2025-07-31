package com.expensetracker.expensetracker.Service;

import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Entity.UserPreferences;
import com.expensetracker.expensetracker.Respository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserPreferencesService {
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    public UserPreferences getPreferencesForCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPreferencesRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreferences(user));
    }

    public UserPreferences updatePreferencesForCurrentUser(UserPreferences updatedPreferences) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreferences(user));
        
        preferences.setNotificationFrequency(updatedPreferences.getNotificationFrequency());
        preferences.setOverBudgetAlerts(updatedPreferences.getOverBudgetAlerts());
        preferences.setEmailNotifications(updatedPreferences.getEmailNotifications());
        preferences.setCurrency(updatedPreferences.getCurrency());
        preferences.setTimezone(updatedPreferences.getTimezone());
        
        return userPreferencesRepository.save(preferences);
    }

    private UserPreferences createDefaultPreferences(User user) {
        UserPreferences defaultPreferences = new UserPreferences();
        defaultPreferences.setUser(user);
        defaultPreferences.setNotificationFrequency(UserPreferences.NotificationFrequency.NONE);
        defaultPreferences.setOverBudgetAlerts(false);
        defaultPreferences.setEmailNotifications(false);
        defaultPreferences.setCurrency("INR");
        defaultPreferences.setTimezone("Asia/Kolkata");
        return userPreferencesRepository.save(defaultPreferences);
    }
} 