import React, { useState, useEffect } from 'react';

function UserPreferences() {
  const [preferences, setPreferences] = useState({
    notificationFrequency: 'NONE',
    emailNotifications: false,
    overBudgetAlerts: false,
    currency: 'INR',
    timezone: 'Asia/Kolkata'
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchPreferences();
  }, []);

  const fetchPreferences = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch('http://localhost:8080/api/preferences', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setPreferences(data);
      } else if (response.status === 404) {
        // No preferences found, use defaults
        setPreferences({
          notificationFrequency: 'NONE',
          emailNotifications: false,
          overBudgetAlerts: false,
          currency: 'INR',
          timezone: 'Asia/Kolkata'
        });
      } else {
        setError('Failed to fetch preferences. Please try again.');
      }
    } catch (error) {
      setError('Failed to fetch preferences. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      // Only send the preference fields, not the entire object with user
      const payload = {
        notificationFrequency: preferences.notificationFrequency,
        overBudgetAlerts: preferences.overBudgetAlerts,
        emailNotifications: preferences.emailNotifications,
        currency: preferences.currency,
        timezone: preferences.timezone
      };

      const response = await fetch('http://localhost:8080/api/preferences', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        setSuccess('Preferences saved successfully!');
      } else {
        setError('Failed to save preferences. Please try again.');
      }
    } catch (error) {
      setError('Failed to save preferences. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (field, value) => {
    setPreferences(prev => ({
      ...prev,
      [field]: value
    }));
  };

  if (loading) {
    return <div className="text-center">Loading preferences...</div>;
  }

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">User Preferences</h5>
      </div>
      <div className="card-body">
        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Email Notification Frequency</label>
              <select
                className="form-select"
                value={preferences.notificationFrequency}
                onChange={(e) => handleChange('notificationFrequency', e.target.value)}
              >
                <option value="NONE">None</option>
                <option value="DAILY">Daily</option>
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="YEARLY">Yearly</option>
              </select>
              <small className="text-muted">
                Receive email summaries of your expenses
              </small>
            </div>
            
            <div className="col-md-6 mb-3">
              <label className="form-label">Currency</label>
              <select
                className="form-select"
                value={preferences.currency}
                onChange={(e) => handleChange('currency', e.target.value)}
              >
                <option value="INR">Indian Rupee (₹)</option>
                <option value="USD">US Dollar ($)</option>
                <option value="EUR">Euro (€)</option>
                <option value="GBP">British Pound (£)</option>
              </select>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Timezone</label>
              <select
                className="form-select"
                value={preferences.timezone}
                onChange={(e) => handleChange('timezone', e.target.value)}
              >
                <option value="Asia/Kolkata">India (IST)</option>
                <option value="America/New_York">Eastern Time (ET)</option>
                <option value="America/Los_Angeles">Pacific Time (PT)</option>
                <option value="Europe/London">London (GMT)</option>
                <option value="Asia/Tokyo">Tokyo (JST)</option>
              </select>
            </div>
            
            <div className="col-md-6 mb-3">
              <div className="form-check form-switch">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="emailNotifications"
                  checked={preferences.emailNotifications}
                  onChange={(e) => handleChange('emailNotifications', e.target.checked)}
                />
                <label className="form-check-label" htmlFor="emailNotifications">
                  Enable Email Notifications
                </label>
              </div>
              <small className="text-muted">
                Receive important updates via email
              </small>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <div className="form-check form-switch">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="overBudgetAlerts"
                  checked={preferences.overBudgetAlerts}
                  onChange={(e) => handleChange('overBudgetAlerts', e.target.checked)}
                />
                <label className="form-check-label" htmlFor="overBudgetAlerts">
                  Over-Budget Alerts
                </label>
              </div>
              <small className="text-muted">
                Get notified when you exceed your budget
              </small>
            </div>
          </div>

          <div className="d-flex gap-2">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
            >
              {saving ? 'Saving...' : 'Save Preferences'}
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={fetchPreferences}
            >
              Reset
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default UserPreferences; 