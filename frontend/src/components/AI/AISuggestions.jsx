import React, { useState, useEffect } from 'react';

function AISuggestions({ refresh }) {
  const [expenses, setExpenses] = useState([]);
  const [budgets, setBudgets] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, [refresh]);

  const fetchData = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      // Fetch expenses and budgets in parallel
      const [expensesResponse, budgetsResponse] = await Promise.all([
        fetch('http://localhost:8080/api/expenses', {
          headers: { 'Authorization': `Bearer ${token}` }
        }),
        fetch('http://localhost:8080/api/budgets', {
          headers: { 'Authorization': `Bearer ${token}` }
        })
      ]);

      if (expensesResponse.ok && budgetsResponse.ok) {
        const expensesData = await expensesResponse.json();
        const budgetsData = await budgetsResponse.json();
        
        setExpenses(expensesData);
        setBudgets(budgetsData);
        generateSuggestions(expensesData, budgetsData);
      } else {
        setError('Failed to fetch data. Please try again.');
      }
    } catch (error) {
      setError('Failed to fetch data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const generateSuggestions = (expenses, budgets) => {
    const suggestions = [];

    // Calculate current month spending
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();
    
    const currentMonthExpenses = expenses.filter(expense => {
      const expenseDate = new Date(expense.expenseDate);
      return expenseDate.getMonth() === currentMonth && expenseDate.getFullYear() === currentYear;
    });

    const currentMonthTotal = currentMonthExpenses.reduce((total, expense) => total + expense.amount, 0);

    // Check overall budget
    const overallBudget = budgets.find(b => b.budgetType === 'OVERALL' && b.periodType === 'MONTHLY');
    if (overallBudget) {
      const budgetPercentage = (currentMonthTotal / overallBudget.amount) * 100;
      
      if (budgetPercentage > 80) {
        suggestions.push({
          type: 'warning',
          title: 'Budget Alert',
          message: `You've spent ${budgetPercentage.toFixed(1)}% of your monthly budget. Consider reducing expenses.`,
          icon: '⚠️'
        });
      } else if (budgetPercentage > 60) {
        suggestions.push({
          type: 'info',
          title: 'Budget Status',
          message: `You've spent ${budgetPercentage.toFixed(1)}% of your monthly budget. You're on track.`,
          icon: 'ℹ️'
        });
      }
    }

    // Analyze spending by category
    const categorySpending = {};
    currentMonthExpenses.forEach(expense => {
      const categoryName = expense.category ? expense.category.name : 'Unknown';
      categorySpending[categoryName] = (categorySpending[categoryName] || 0) + expense.amount;
    });

    // Find top spending category
    const topCategory = Object.entries(categorySpending)
      .sort(([,a], [,b]) => b - a)[0];

    if (topCategory) {
      const [categoryName, amount] = topCategory;
      const categoryPercentage = (amount / currentMonthTotal) * 100;
      
      if (categoryPercentage > 50) {
        suggestions.push({
          type: 'warning',
          title: 'High Category Spending',
          message: `${categoryName} accounts for ${categoryPercentage.toFixed(1)}% of your spending this month. Consider diversifying.`,
          icon: '💰'
        });
      }
    }

    // Check for frequent small expenses
    const smallExpenses = currentMonthExpenses.filter(expense => expense.amount < 100);
    if (smallExpenses.length > 10) {
      suggestions.push({
        type: 'info',
        title: 'Small Expenses Add Up',
        message: `You have ${smallExpenses.length} small expenses this month. Small purchases can add up quickly.`,
        icon: '📊'
      });
    }

    // Check for recent spending patterns
    const lastWeekExpenses = expenses.filter(expense => {
      const expenseDate = new Date(expense.expenseDate);
      const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      return expenseDate >= weekAgo;
    });

    if (lastWeekExpenses.length > 5) {
      suggestions.push({
        type: 'info',
        title: 'Recent Activity',
        message: `You've made ${lastWeekExpenses.length} expenses in the last week. Review your recent spending.`,
        icon: '📅'
      });
    }

    // If no specific suggestions, provide general advice
    if (suggestions.length === 0) {
      suggestions.push({
        type: 'success',
        title: 'Great Job!',
        message: 'Your spending looks healthy this month. Keep up the good work!',
        icon: '✅'
      });
    }

    setSuggestions(suggestions);
  };

  const getSuggestionColor = (type) => {
    switch (type) {
      case 'warning': return 'border-warning';
      case 'info': return 'border-info';
      case 'success': return 'border-success';
      default: return 'border-secondary';
    }
  };

  if (loading) {
    return <div className="text-center">Loading suggestions...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">AI Spending Insights</h5>
        <small className="text-muted">Personalized suggestions based on your spending patterns</small>
      </div>
      <div className="card-body">
        {suggestions.length === 0 ? (
          <p className="text-muted">No suggestions available. Add some expenses to get personalized insights.</p>
        ) : (
          <div className="row">
            {suggestions.map((suggestion, index) => (
              <div key={index} className="col-md-6 mb-3">
                <div className={`card ${getSuggestionColor(suggestion.type)}`}>
                  <div className="card-body">
                    <div className="d-flex align-items-start">
                      <div className="me-3 fs-4">{suggestion.icon}</div>
                      <div>
                        <h6 className="card-title mb-1">{suggestion.title}</h6>
                        <p className="card-text mb-0">{suggestion.message}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default AISuggestions; 