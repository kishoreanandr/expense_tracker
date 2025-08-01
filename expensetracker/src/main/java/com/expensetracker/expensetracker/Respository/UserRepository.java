package com.expensetracker.expensetracker.Respository;

import com.expensetracker.expensetracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.preferences p WHERE p.emailNotifications = true")
    List<User> findUsersWithEmailNotificationsEnabled();
}
