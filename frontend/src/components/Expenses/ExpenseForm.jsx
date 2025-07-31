import React, { useState, useEffect } from 'react';

function ExpenseForm({ onExpenseAdded, editingExpense, onCancelEdit }) {
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState('');
  const [categories, setCategories] = useState([]);
  const [date, setDate] = useState('');
  const [note, setNote] = useState('');
  const [merchant, setMerchant] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setError('You are not logged in. Please log in again.');
      window.location.href = '/login';
      return;
    }
    const fetchCategories = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/categories', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Failed to fetch categories');
        const data = await response.json();
        setCategories(data);
      } catch (err) {
        setCategories([]);
        setError('Failed to fetch categories. Please try again.');
      }
    };
    fetchCategories();
  }, []);

  useEffect(() => {
    if (editingExpense) {
      setAmount(editingExpense.amount || '');
      setCategory(editingExpense.category?.id || '');
      setDate(editingExpense.expenseDate || editingExpense.date || '');
      setNote(editingExpense.note || '');
      setMerchant(editingExpense.merchant || '');
      setPaymentMethod(editingExpense.paymentMethod || '');
      setError('');
      setSuccess('');
    } else {
      setAmount('');
      setCategory('');
      setDate('');
      setNote('');
      setMerchant('');
      setPaymentMethod('');
      setError('');
      setSuccess('');
    }
  }, [editingExpense]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    const token = localStorage.getItem('token');
    if (!token) {
      setError('You are not logged in. Please log in again.');
      window.location.href = '/login';
      setLoading(false);
      return;
    }
    if (!category || Number(category) === 0) {
      setError('Please select a category.');
      setLoading(false);
      return;
    }
    try {
      const method = editingExpense ? 'PUT' : 'POST';
      const url = editingExpense ? `http://localhost:8080/api/expenses/${editingExpense.id}` : 'http://localhost:8080/api/expenses';
      const payload = {
        amount,
        category: { id: Number(category) },
        expenseDate: date,
        note,
        merchant,
        paymentMethod
      };
      console.log('Selected category:', category);
      console.log('Expense payload:', payload);
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || (editingExpense ? 'Failed to update expense' : 'Failed to add expense'));
      }
      setSuccess(editingExpense ? 'Expense updated successfully!' : 'Expense added successfully!');
      if (!editingExpense) {
        setAmount('');
        setCategory('');
        setDate('');
        setNote('');
        setMerchant('');
        setPaymentMethod('');
      }
      if (onExpenseAdded) onExpenseAdded();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card mt-4">
      <div className="card-body">
        <h5 className="card-title">{editingExpense ? 'Edit Expense' : 'Add Expense'}</h5>
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-4 mb-3">
              <label className="form-label">Amount</label>
              <input type="number" className="form-control" value={amount} onChange={e => setAmount(e.target.value)} required min="0.01" step="0.01" />
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Category</label>
              <select className="form-select" value={category} onChange={e => setCategory(e.target.value)} required>
                <option value="">Select</option>
                {categories.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Date</label>
              <input type="date" className="form-control" value={date} onChange={e => setDate(e.target.value)} required />
            </div>
          </div>
          <div className="row">
            <div className="col-md-4 mb-3">
              <label className="form-label">Merchant</label>
              <input type="text" className="form-control" value={merchant} onChange={e => setMerchant(e.target.value)} />
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Payment Method</label>
              <input type="text" className="form-control" value={paymentMethod} onChange={e => setPaymentMethod(e.target.value)} />
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Note</label>
              <input type="text" className="form-control" value={note} onChange={e => setNote(e.target.value)} />
            </div>
          </div>
          {error && <div className="alert alert-danger">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}
          <button type="submit" className="btn btn-primary me-2" disabled={loading}>
            {loading ? (editingExpense ? 'Updating...' : 'Adding...') : (editingExpense ? 'Update Expense' : 'Add Expense')}
          </button>
          {editingExpense && (
            <button type="button" className="btn btn-secondary" onClick={onCancelEdit} disabled={loading}>
              Cancel
            </button>
          )}
        </form>
      </div>
    </div>
  );
}

export default ExpenseForm; 