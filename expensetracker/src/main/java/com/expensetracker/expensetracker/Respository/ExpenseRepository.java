package com.expensetracker.expensetracker.Respository;


import com.expensetracker.expensetracker.Entity.Category;
import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserOrderByExpenseDateDesc(User user);
    List<Expense> findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(User user, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserAndCategoryOrderByExpenseDateDesc(User user, Category category);
    List<Expense> findByUserAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(User user, Category category, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpenseForUserBetweenDates(@Param("user") User user,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.category = :category AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpenseForUserAndCategoryBetweenDates(@Param("user") User user,
                                                             @Param("category") Category category,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTopSpendingCategoriesForUser(@Param("user") User user,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT DATE(e.expenseDate), SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate GROUP BY DATE(e.expenseDate) ORDER BY DATE(e.expenseDate)")
    List<Object[]> getDailyExpensesForUser(@Param("user") User user,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    List<Expense> findByUser(User user);
}