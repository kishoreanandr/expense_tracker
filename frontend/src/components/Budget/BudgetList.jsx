import React, { useState, useEffect } from 'react';

function BudgetList({ refresh, onBudgetChanged, onEditBudget }) {
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBudgets();
  }, [refresh]);

  const fetchBudgets = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch('http://localhost:8080/api/budgets', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setBudgets(data);
      } else {
        setError('Failed to fetch budgets. Please try again.');
      }
    } catch (error) {
      setError('Failed to fetch budgets. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this budget?')) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch(`http://localhost:8080/api/budgets/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        onBudgetChanged();
      } else {
        setError('Failed to delete budget. Please try again.');
      }
    } catch (error) {
      setError('Failed to delete budget. Please try again.');
    }
  };

  const getBudgetTypeLabel = (type) => {
    return type === 'OVERALL' ? 'Overall Budget' : 'Category Budget';
  };

  const getPeriodTypeLabel = (period) => {
    switch (period) {
      case 'MONTHLY': return 'Monthly';
      case 'WEEKLY': return 'Weekly';
      case 'YEARLY': return 'Yearly';
      default: return period;
    }
  };

  if (loading) {
    return <div className="text-center">Loading budgets...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">Your Budgets</h5>
      </div>
      <div className="card-body">
        {budgets.length === 0 ? (
          <p className="text-muted">No budgets found. Create your first budget above.</p>
        ) : (
          <div className="table-responsive">
            <table className="table table-hover">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Period</th>
                  <th>Category</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {budgets.map(budget => (
                  <tr key={budget.id}>
                    <td>
                      <span className={`badge ${budget.budgetType === 'OVERALL' ? 'bg-primary' : 'bg-success'}`}>
                        {getBudgetTypeLabel(budget.budgetType)}
                      </span>
                    </td>
                    <td>₹{budget.amount.toFixed(2)}</td>
                    <td>{getPeriodTypeLabel(budget.periodType)}</td>
                    <td>
                      {budget.category ? budget.category.name : '-'}
                    </td>
                    <td>
                      <div className="btn-group btn-group-sm">
                        <button
                          className="btn btn-outline-primary"
                          onClick={() => onEditBudget(budget)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-outline-danger"
                          onClick={() => handleDelete(budget.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default BudgetList; 