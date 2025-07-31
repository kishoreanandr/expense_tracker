import React, { useState } from 'react';
import ExpenseForm from '../Expenses/ExpenseForm';
import ExpenseList from '../Expenses/ExpenseList';
import BudgetForm from '../Budget/BudgetForm';
import BudgetList from '../Budget/BudgetList';
import ExpenseSummary from '../Analytics/ExpenseSummary';
import AISuggestions from '../AI/AISuggestions';
import UserPreferences from '../Settings/UserPreferences';

function Dashboard() {
  const [refresh, setRefresh] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [selectedBudget, setSelectedBudget] = useState(null);
  const [activeTab, setActiveTab] = useState('expenses');

  const handleExpenseAddedOrEdited = () => {
    setRefresh(r => !r);
    setSelectedExpense(null);
  };

  const handleBudgetAddedOrEdited = () => {
    setRefresh(r => !r);
    setSelectedBudget(null);
  };

  const handleEditExpense = (expense) => {
    setSelectedExpense(expense);
    setActiveTab('expenses');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleEditBudget = (budget) => {
    setSelectedBudget(budget);
    setActiveTab('budgets');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setSelectedExpense(null);
    setSelectedBudget(null);
  };

  return (
    <div className="container mt-5">
      <h2>Dashboard</h2>
      <p>Welcome to your expense and budget tracker!</p>
      
      {/* Navigation Tabs */}
      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'expenses' ? 'active' : ''}`}
            onClick={() => setActiveTab('expenses')}
          >
            Expenses
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'budgets' ? 'active' : ''}`}
            onClick={() => setActiveTab('budgets')}
          >
            Budgets
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'analytics' ? 'active' : ''}`}
            onClick={() => setActiveTab('analytics')}
          >
            Analytics
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'ai' ? 'active' : ''}`}
            onClick={() => setActiveTab('ai')}
          >
            AI Insights
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'settings' ? 'active' : ''}`}
            onClick={() => setActiveTab('settings')}
          >
            Settings
          </button>
        </li>
      </ul>

      {/* Expenses Tab */}
      {activeTab === 'expenses' && (
        <>
          <ExpenseForm
            onExpenseAdded={handleExpenseAddedOrEdited}
            editingExpense={selectedExpense}
            onCancelEdit={handleCancelEdit}
          />
          <ExpenseList
            refresh={refresh}
            onExpenseChanged={handleExpenseAddedOrEdited}
            onEditExpense={handleEditExpense}
          />
        </>
      )}

      {/* Budgets Tab */}
      {activeTab === 'budgets' && (
        <>
          <BudgetForm
            onBudgetAdded={handleBudgetAddedOrEdited}
            editingBudget={selectedBudget}
            onCancelEdit={handleCancelEdit}
          />
          <BudgetList
            refresh={refresh}
            onBudgetChanged={handleBudgetAddedOrEdited}
            onEditBudget={handleEditBudget}
          />
        </>
      )}

      {/* Analytics Tab */}
      {activeTab === 'analytics' && (
        <ExpenseSummary refresh={refresh} />
      )}

      {/* AI Insights Tab */}
      {activeTab === 'ai' && (
        <AISuggestions refresh={refresh} />
      )}

      {/* Settings Tab */}
      {activeTab === 'settings' && (
        <UserPreferences />
      )}
    </div>
  );
}

export default Dashboard; 