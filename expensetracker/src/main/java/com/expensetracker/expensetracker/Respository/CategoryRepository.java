package com.expensetracker.expensetracker.Respository;

import com.expensetracker.expensetracker.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsDefaultTrue();

    @Query("SELECT c FROM Category c WHERE c.isDefault = true OR c.id IN " +
            "(SELECT DISTINCT e.category.id FROM Expense e WHERE e.user.id = :userId)")
    List<Category> findAvailableCategoriesForUser(Long userId);
}