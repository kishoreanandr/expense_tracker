import React, { useState, useEffect } from 'react';

function BudgetForm({ onBudgetAdded, editingBudget, onCancelEdit }) {
  const [amount, setAmount] = useState('');
  const [budgetType, setBudgetType] = useState('OVERALL');
  const [periodType, setPeriodType] = useState('MONTHLY');
  const [category, setCategory] = useState('');
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchCategories();
    if (editingBudget) {
      setAmount(editingBudget.amount.toString());
      setBudgetType(editingBudget.budgetType);
      setPeriodType(editingBudget.periodType);
      if (editingBudget.category) {
        setCategory(editingBudget.category.id.toString());
      }
    }
  }, [editingBudget]);

  const fetchCategories = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch('http://localhost:8080/api/categories', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setCategories(data);
      } else {
        setError('Failed to fetch categories. Please try again.');
      }
    } catch (error) {
      setError('Failed to fetch categories. Please try again.');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const payload = {
        amount: parseFloat(amount),
        budgetType: budgetType,
        periodType: periodType,
        ...(budgetType === 'CATEGORY' && category && { category: { id: parseInt(category) } })
      };

      const url = editingBudget 
        ? `http://localhost:8080/api/budgets/${editingBudget.id}`
        : 'http://localhost:8080/api/budgets';

      const response = await fetch(url, {
        method: editingBudget ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const data = await response.json();
        onBudgetAdded();
        if (!editingBudget) {
          setAmount('');
          setBudgetType('OVERALL');
          setPeriodType('MONTHLY');
          setCategory('');
        }
      } else {
        const errorData = await response.text();
        setError(errorData || 'Failed to save budget. Please try again.');
      }
    } catch (error) {
      setError('Failed to save budget. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card mb-4">
      <div className="card-header">
        <h5 className="mb-0">{editingBudget ? 'Edit Budget' : 'Add New Budget'}</h5>
      </div>
      <div className="card-body">
        {error && <div className="alert alert-danger">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Amount</label>
              <input
                type="number"
                className="form-control"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                step="0.01"
                min="0"
                required
              />
            </div>
            
            <div className="col-md-6 mb-3">
              <label className="form-label">Budget Type</label>
              <select
                className="form-select"
                value={budgetType}
                onChange={(e) => setBudgetType(e.target.value)}
                required
              >
                <option value="OVERALL">Overall Budget</option>
                <option value="CATEGORY">Category Budget</option>
              </select>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Period Type</label>
              <select
                className="form-select"
                value={periodType}
                onChange={(e) => setPeriodType(e.target.value)}
                required
              >
                <option value="MONTHLY">Monthly</option>
                <option value="WEEKLY">Weekly</option>
                <option value="YEARLY">Yearly</option>
              </select>
            </div>
            
            {budgetType === 'CATEGORY' && (
              <div className="col-md-6 mb-3">
                <label className="form-label">Category</label>
                <select
                  className="form-select"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  required
                >
                  <option value="">Select Category</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          <div className="d-flex gap-2">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Saving...' : (editingBudget ? 'Update Budget' : 'Add Budget')}
            </button>
            {editingBudget && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onCancelEdit}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}

export default BudgetForm; 