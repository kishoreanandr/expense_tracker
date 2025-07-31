package com.expensetracker.expensetracker.Service;

import com.expensetracker.expensetracker.Entity.Budget;
import com.expensetracker.expensetracker.Entity.User;
import com.expensetracker.expensetracker.Respository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> getAllBudgetsForCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return budgetRepository.findByUser(user);
    }

    public Budget addBudget(Budget budget) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("Budget not found"));
        budget.setAmount(updatedBudget.getAmount());
        budget.setCategory(updatedBudget.getCategory());
        budget.setPeriodType(updatedBudget.getPeriodType());
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
} 