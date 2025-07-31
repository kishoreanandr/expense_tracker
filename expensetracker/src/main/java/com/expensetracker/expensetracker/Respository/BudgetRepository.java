package com.expensetracker.expensetracker.Respository;


import com.expensetracker.expensetracker.Entity.Budget;
import com.expensetracker.expensetracker.Entity.Category;
import com.expensetracker.expensetracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserAndBudgetType(User user, Budget.BudgetType budgetType);
    Optional<Budget> findByUserAndCategoryAndPeriodType(User user, Category category, Budget.PeriodType periodType);
    Optional<Budget> findByUserAndBudgetTypeAndPeriodType(User user, Budget.BudgetType budgetType, Budget.PeriodType periodType);

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.budgetType = 'OVERALL' AND b.periodType = :periodType")
    Optional<Budget> findOverallBudgetByUserAndPeriod(@Param("user") User user, @Param("periodType") Budget.PeriodType periodType);

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.budgetType = 'CATEGORY' AND b.periodType = :periodType")
    List<Budget> findCategoryBudgetsByUserAndPeriod(@Param("user") User user, @Param("periodType") Budget.PeriodType periodType);

    @Query("SELECT b FROM Budget b JOIN FETCH b.category WHERE b.user = :user AND b.budgetType = :budgetType")
    List<Budget> findByUserAndBudgetTypeWithCategory(@Param("user") User user, @Param("budgetType") Budget.BudgetType budgetType);
}