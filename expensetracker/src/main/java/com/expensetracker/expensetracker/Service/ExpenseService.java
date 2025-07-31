package com.expensetracker.expensetracker.Service;

import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Entity.Category;
import com.expensetracker.expensetracker.Respository.ExpenseRepository;
import com.expensetracker.expensetracker.Respository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Expense> getAllExpensesForCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return expenseRepository.findByUser(user);
    }

    public Expense addExpense(Expense expense) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        expense.setUser(user);
        // Fix: fetch the full Category entity by id
        if (expense.getCategory() != null && expense.getCategory().getId() != null) {
            Category category = categoryRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(category);
        } else {
            throw new RuntimeException("Category is required");
        }
        System.out.println("Received category: " + (expense.getCategory() != null ? expense.getCategory().getId() : "null"));
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setExpenseDate(updatedExpense.getExpenseDate());
        expense.setNote(updatedExpense.getNote());
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
} 