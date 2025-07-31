import React, { useState } from 'react';

function PDFExport() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleExport = async () => {
    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch('http://localhost:8080/api/pdf/export', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        // Create a blob from the PDF data
        const blob = await response.blob();
        
        // Create a download link
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `expense-report-${new Date().toISOString().split('T')[0]}.pdf`;
        
        // Trigger download
        document.body.appendChild(a);
        a.click();
        
        // Cleanup
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } else {
        setError('Failed to generate PDF. Please try again.');
      }
    } catch (error) {
      setError('Failed to generate PDF. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">Export Data</h5>
      </div>
      <div className="card-body">
        <p className="text-muted mb-3">
          Export your expenses and budgets as a PDF report. This will include all your financial data in a formatted document.
        </p>
        
        {error && <div className="alert alert-danger">{error}</div>}
        
        <button
          className="btn btn-primary"
          onClick={handleExport}
          disabled={loading}
        >
          {loading ? (
            <>
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Generating PDF...
            </>
          ) : (
            <>
              <i className="bi bi-file-pdf me-2"></i>
              Export to PDF
            </>
          )}
        </button>
        
        <div className="mt-3">
          <small className="text-muted">
            The PDF will include:
          </small>
          <ul className="small text-muted mt-1">
            <li>All your expenses with categories and dates</li>
            <li>Budget information and tracking</li>
            <li>Spending summaries and analytics</li>
            <li>Formatted tables and charts</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default PDFExport; 