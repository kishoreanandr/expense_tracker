package com.expensetracker.expensetracker.Respository;


import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUser(User user);

    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotifications = true AND up.notificationFrequency = :frequency")
    List<UserPreferences> findByEmailNotificationsAndFrequency(@Param("frequency") UserPreferences.NotificationFrequency frequency);

    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotifications = true AND up.overBudgetAlerts = true")
    List<UserPreferences> findUsersWithBudgetAlertsEnabled();

    @Query("SELECT up FROM UserPreferences up WHERE up.overBudgetAlerts = :enabled")
    List<UserPreferences> findByOverBudgetAlerts(@Param("enabled") Boolean enabled);

    @Query("SELECT up FROM UserPreferences up JOIN FETCH up.user WHERE up.overBudgetAlerts = :enabled")
    List<UserPreferences> findByOverBudgetAlertsWithUser(@Param("enabled") Boolean enabled);
}