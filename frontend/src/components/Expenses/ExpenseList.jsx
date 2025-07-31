import React, { useEffect, useState } from 'react';

function ExpenseList({ refresh, onExpenseChanged, onEditExpense }) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [localRefresh, setLocalRefresh] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setError('You are not logged in. Please log in again.');
      window.location.href = '/login';
      setLoading(false);
      return;
    }
    const fetchExpenses = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await fetch('http://localhost:8080/api/expenses', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch expenses');
        }
        const data = await response.json();
        setExpenses(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchExpenses();
  }, [refresh, localRefresh]);

  const handleDelete = async (id) => {
    const token = localStorage.getItem('token');
    if (!token) {
      setError('You are not logged in. Please log in again.');
      window.location.href = '/login';
      return;
    }
    if (!window.confirm('Are you sure you want to delete this expense?')) return;
    try {
      const response = await fetch(`http://localhost:8080/api/expenses/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error('Failed to delete expense');
      setLocalRefresh(r => !r);
      if (onExpenseChanged) onExpenseChanged();
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="mt-4">
      <h4>Your Expenses</h4>
      {loading && <div>Loading...</div>}
      {error && <div className="alert alert-danger">{error}</div>}
      {!loading && !error && (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Date</th>
              <th>Category</th>
              <th>Amount</th>
              <th>Note</th>
              <th>Merchant</th>
              <th>Payment Method</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {expenses.length === 0 && (
              <tr><td colSpan="7" className="text-center">No expenses found.</td></tr>
            )}
            {expenses.map(exp => (
              <tr key={exp.id}>
                <td>{exp.expenseDate || exp.date}</td>
                <td>{exp.category?.name || ''}</td>
                <td>{exp.amount}</td>
                <td>{exp.note}</td>
                <td>{exp.merchant}</td>
                <td>{exp.paymentMethod}</td>
                <td>
                  <button className="btn btn-sm btn-secondary me-2" onClick={() => onEditExpense && onEditExpense(exp)}>Edit</button>
                  <button className="btn btn-sm btn-danger" onClick={() => handleDelete(exp.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ExpenseList; 