import React, { useState, useEffect } from 'react';
import PDFExport from '../Export/PDFExport';

function ExpenseSummary({ refresh }) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchExpenses();
  }, [refresh]);

  const fetchExpenses = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch('http://localhost:8080/api/expenses', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        setError('Failed to fetch expenses. Please try again.');
      }
    } catch (error) {
      setError('Failed to fetch expenses. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalSpent = () => {
    return expenses.reduce((total, expense) => total + expense.amount, 0);
  };

  const getSpendingByCategory = () => {
    const categorySpending = {};
    expenses.forEach(expense => {
      const categoryName = expense.category ? expense.category.name : 'Unknown';
      categorySpending[categoryName] = (categorySpending[categoryName] || 0) + expense.amount;
    });
    return categorySpending;
  };

  const getCurrentMonthExpenses = () => {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();
    
    return expenses.filter(expense => {
      const expenseDate = new Date(expense.expenseDate);
      return expenseDate.getMonth() === currentMonth && expenseDate.getFullYear() === currentYear;
    });
  };

  const getTopSpendingCategories = () => {
    const categorySpending = getSpendingByCategory();
    return Object.entries(categorySpending)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 5);
  };

  if (loading) {
    return <div className="text-center">Loading summary...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  const totalSpent = calculateTotalSpent();
  const currentMonthExpenses = getCurrentMonthExpenses();
  const currentMonthTotal = currentMonthExpenses.reduce((total, expense) => total + expense.amount, 0);
  const topCategories = getTopSpendingCategories();

  return (
    <div>
      {/* Export Section */}
      <div className="row mb-4">
        <div className="col-12">
          <PDFExport />
        </div>
      </div>

      <div className="row">
        {/* Summary Cards */}
        <div className="col-md-4 mb-4">
          <div className="card bg-primary text-white">
            <div className="card-body">
              <h5 className="card-title">Total Spent</h5>
              <h3 className="card-text">₹{totalSpent.toFixed(2)}</h3>
              <small>All time</small>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-4">
          <div className="card bg-success text-white">
            <div className="card-body">
              <h5 className="card-title">This Month</h5>
              <h3 className="card-text">₹{currentMonthTotal.toFixed(2)}</h3>
              <small>{currentMonthExpenses.length} expenses</small>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-4">
          <div className="card bg-info text-white">
            <div className="card-body">
              <h5 className="card-title">Total Expenses</h5>
              <h3 className="card-text">{expenses.length}</h3>
              <small>All time</small>
            </div>
          </div>
        </div>

        {/* Top Spending Categories */}
        <div className="col-12">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">Top Spending Categories</h5>
            </div>
            <div className="card-body">
              {topCategories.length === 0 ? (
                <p className="text-muted">No expenses found.</p>
              ) : (
                <div className="row">
                  {topCategories.map(([category, amount]) => (
                    <div key={category} className="col-md-6 col-lg-4 mb-3">
                      <div className="d-flex justify-content-between align-items-center p-3 border rounded">
                        <div>
                          <h6 className="mb-0">{category}</h6>
                          <small className="text-muted">
                            {((amount / totalSpent) * 100).toFixed(1)}% of total
                          </small>
                        </div>
                        <div className="text-end">
                          <strong>₹{amount.toFixed(2)}</strong>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Recent Expenses */}
        <div className="col-12">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">Recent Expenses</h5>
            </div>
            <div className="card-body">
              {expenses.length === 0 ? (
                <p className="text-muted">No expenses found.</p>
              ) : (
                <div className="table-responsive">
                  <table className="table table-sm">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Category</th>
                        <th>Amount</th>
                        <th>Note</th>
                      </tr>
                    </thead>
                    <tbody>
                      {expenses.slice(0, 5).map(expense => (
                        <tr key={expense.id}>
                          <td>{new Date(expense.expenseDate).toLocaleDateString()}</td>
                          <td>
                            <span className="badge bg-secondary">
                              {expense.category ? expense.category.name : 'Unknown'}
                            </span>
                          </td>
                          <td>₹{expense.amount.toFixed(2)}</td>
                          <td>{expense.note || '-'}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ExpenseSummary; 