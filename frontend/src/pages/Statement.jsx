// ===== FILE: src/pages/Statement.jsx =====
import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import { apiService } from '../services/api'
import { formatCurrency } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'

const Statement = () => {
    const [searchParams] = useSearchParams()
    const { user, logout } = useAuth()

    const [statement, setStatement] = useState(null)
    const [transactions, setTransactions] = useState([])
    const [customer, setCustomer] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const customerId = searchParams.get('customerId')
    const [currentYear, setCurrentYear] = useState(parseInt(searchParams.get('year')) || new Date().getFullYear())
    const [currentMonth, setCurrentMonth] = useState(parseInt(searchParams.get('month')) || new Date().getMonth() + 1)

    useEffect(() => {
        if (customerId) {
            fetchStatement()
            fetchCustomerData()
        }
    }, [customerId, currentYear, currentMonth])

    const fetchStatement = async () => {
        setLoading(true)
        setError('')
        try {
            const result = await apiService.getMonthlyStatement(customerId, currentYear, currentMonth)
            if (result.resultCode === '00') {
                setStatement(result.data)
                setTransactions(result.data.transactions || [])
            } else {
                setError(result.resultMessage || 'Failed to load statement')
                setStatement(null)
                setTransactions([])
            }
        } catch (err) {
            setError('Network error: ' + err.message)
            setStatement(null)
            setTransactions([])
        } finally {
            setLoading(false)
        }
    }

    const fetchCustomerData = async () => {
        try {
            const result = await apiService.getCustomer(customerId)
            if (result.resultCode === '00') {
                setCustomer(result.data)
            }
        } catch (err) {
            console.error('Error fetching customer data:', err)
        }
    }

    const handlePrevMonth = () => {
        if (currentMonth === 1) {
            setCurrentMonth(12)
            setCurrentYear(currentYear - 1)
        } else {
            setCurrentMonth(currentMonth - 1)
        }

        // Update URL params
        const newParams = new URLSearchParams(searchParams)
        newParams.set('year', currentYear.toString())
        newParams.set('month', (currentMonth === 1 ? 12 : currentMonth - 1).toString())
        window.history.replaceState(null, '', `${window.location.pathname}?${newParams}`)
    }

    const handleNextMonth = () => {
        if (currentMonth === 12) {
            setCurrentMonth(1)
            setCurrentYear(currentYear + 1)
        } else {
            setCurrentMonth(currentMonth + 1)
        }

        // Update URL params
        const newParams = new URLSearchParams(searchParams)
        newParams.set('year', (currentMonth === 12 ? currentYear + 1 : currentYear).toString())
        newParams.set('month', (currentMonth === 12 ? 1 : currentMonth + 1).toString())
        window.history.replaceState(null, '', `${window.location.pathname}?${newParams}`)
    }

    const formatDate = (dateString) => {
        if (!dateString) return '-'
        const date = new Date(dateString)
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: '2-digit'
        })
    }

    const getMonthName = (month) => {
        const months = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ]
        return months[month - 1]
    }

    const handlePrintStatement = () => {
        window.print()
    }

    const handleDownloadPDF = async () => {
        try {
            const response = await apiService.downloadStatementPDF(customerId, currentYear, currentMonth)
            // Handle PDF download logic here
            const blob = new Blob([response], { type: 'application/pdf' })
            const url = window.URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = url
            a.download = `statement-${currentYear}-${currentMonth.toString().padStart(2, '0')}.pdf`
            document.body.appendChild(a)
            a.click()
            window.URL.revokeObjectURL(url)
            document.body.removeChild(a)
        } catch (err) {
            console.error('Error downloading PDF:', err)
        }
    }

    if (loading) {
        return <LoadingSpinner text="Loading statement..." />
    }

    if (!customerId) {
        return (
            <div className="min-h-screen bg-light">
                <Navbar user={user} onLogout={logout} />
                <div className="container mt-4">
                    <div className="alert alert-warning">
                        <h4>Missing Customer ID</h4>
                        <p>Please provide a valid customer ID to view the statement.</p>
                        <button
                            className="btn btn-primary"
                            onClick={() => window.location.href = '/dashboard'}
                        >
                            Back to Dashboard
                        </button>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={logout} />

            <div className="container mt-4">
                {/* Header */}
                <div className="row mb-4">
                    <div className="col-12">
                        <div className="d-flex justify-content-between align-items-center">
                            <div>
                                <h2 className="fw-bold mb-1">Account Statement</h2>
                                <p className="text-muted mb-0">
                                    {customer?.firstName} {customer?.lastName} • Customer ID: {customerId}
                                </p>
                            </div>
                            <div>
                                <button
                                    className="btn btn-outline-primary me-2"
                                    onClick={handlePrintStatement}
                                >
                                    <i className="fas fa-print me-2" />
                                    Print
                                </button>
                                <button
                                    className="btn btn-primary"
                                    onClick={handleDownloadPDF}
                                >
                                    <i className="fas fa-download me-2" />
                                    Download PDF
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Statement Card */}
                <div className="card shadow-sm" style={{ borderRadius: '12px' }}>
                    <div className="card-header bg-light d-flex justify-content-between align-items-center">
                        <h3 className="mb-0">
                            <i className="fas fa-file-invoice-dollar me-2 text-primary" />
                            Monthly Statement
                        </h3>
                        <div className="d-flex align-items-center">
                            <button
                                className="btn btn-outline-secondary btn-sm me-2"
                                onClick={handlePrevMonth}
                            >
                                <i className="fas fa-chevron-left" />
                            </button>
                            <span className="fw-bold mx-3" style={{ minWidth: '150px', textAlign: 'center' }}>
                                {getMonthName(currentMonth)} {currentYear}
                            </span>
                            <button
                                className="btn btn-outline-secondary btn-sm ms-2"
                                onClick={handleNextMonth}
                            >
                                <i className="fas fa-chevron-right" />
                            </button>
                        </div>
                    </div>

                    <div className="card-body">
                        {error ? (
                            <div className="alert alert-danger">
                                <i className="fas fa-exclamation-triangle me-2" />
                                {error}
                            </div>
                        ) : statement ? (
                            <>
                                {/* Balance Summary */}
                                <div className="row mb-4">
                                    <div className="col-md-3 text-center">
                                        <div className="border-end">
                                            <h6 className="text-muted mb-2">Opening Balance</h6>
                                            <h4 className="fw-bold text-primary">
                                                {formatCurrency(statement.openingBalance)}
                                            </h4>
                                        </div>
                                    </div>
                                    <div className="col-md-3 text-center">
                                        <div className="border-end">
                                            <h6 className="text-muted mb-2">Closing Balance</h6>
                                            <h4 className="fw-bold text-dark">
                                                {formatCurrency(statement.closingBalance)}
                                            </h4>
                                        </div>
                                    </div>
                                    <div className="col-md-3 text-center">
                                        <div className="border-end">
                                            <h6 className="text-success mb-2">Total Credits</h6>
                                            <h4 className="fw-bold text-success">
                                                {formatCurrency(statement.totalCredits)}
                                            </h4>
                                        </div>
                                    </div>
                                    <div className="col-md-3 text-center">
                                        <h6 className="text-danger mb-2">Total Debits</h6>
                                        <h4 className="fw-bold text-danger">
                                            {formatCurrency(statement.totalDebits)}
                                        </h4>
                                    </div>
                                </div>

                                <hr />

                                {/* Transactions */}
                                <div className="mt-4">
                                    <h5 className="fw-bold mb-3">
                                        <i className="fas fa-list me-2" />
                                        Transaction History
                                        <span className="badge bg-secondary ms-2">{transactions.length}</span>
                                    </h5>

                                    {transactions.length > 0 ? (
                                        <div className="table-responsive">
                                            <table className="table table-hover">
                                                <thead className="table-light">
                                                <tr>
                                                    <th>Date</th>
                                                    <th>Description</th>
                                                    <th>Reference</th>
                                                    <th className="text-center">Type</th>
                                                    <th className="text-end">Amount</th>
                                                    <th className="text-end">Balance</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                {transactions.map((transaction, index) => (
                                                    <tr key={index}>
                                                        <td>{formatDate(transaction.transactionDate)}</td>
                                                        <td>
                                                            <div className="fw-medium">
                                                                {transaction.description || transaction.narration}
                                                            </div>
                                                            {transaction.beneficiaryName && (
                                                                <small className="text-muted">
                                                                    To: {transaction.beneficiaryName}
                                                                </small>
                                                            )}
                                                        </td>
                                                        <td>
                                                            <small className="text-muted font-monospace">
                                                                {transaction.referenceNumber || transaction.transactionId}
                                                            </small>
                                                        </td>
                                                        <td className="text-center">
                                                                <span className={`badge ${
                                                                    transaction.transactionType === 'CREDIT' || transaction.amount > 0
                                                                        ? 'bg-success' : 'bg-danger'
                                                                }`}>
                                                                    {transaction.transactionType || (transaction.amount > 0 ? 'CREDIT' : 'DEBIT')}
                                                                </span>
                                                        </td>
                                                        <td className={`text-end fw-medium ${
                                                            transaction.transactionType === 'CREDIT' || transaction.amount > 0
                                                                ? 'text-success' : 'text-danger'
                                                        }`}>
                                                            {transaction.transactionType === 'CREDIT' || transaction.amount > 0 ? '+' : ''}
                                                            {formatCurrency(Math.abs(transaction.amount))}
                                                        </td>
                                                        <td className="text-end fw-medium">
                                                            {formatCurrency(transaction.runningBalance || transaction.balance)}
                                                        </td>
                                                    </tr>
                                                ))}
                                                </tbody>
                                            </table>
                                        </div>
                                    ) : (
                                        <div className="text-center py-5">
                                            <i className="fas fa-inbox fa-3x text-muted mb-3" />
                                            <h5 className="text-muted">No Transactions Found</h5>
                                            <p className="text-muted">
                                                There are no transactions for {getMonthName(currentMonth)} {currentYear}
                                            </p>
                                        </div>
                                    )}
                                </div>
                            </>
                        ) : (
                            <div className="text-center py-5">
                                <i className="fas fa-file-invoice fa-3x text-muted mb-3" />
                                <h5 className="text-muted">No Statement Available</h5>
                                <p className="text-muted">
                                    No statement data available for {getMonthName(currentMonth)} {currentYear}
                                </p>
                            </div>
                        )}
                    </div>
                </div>

                {/* Back to Dashboard */}
                <div className="text-center mt-4 mb-5">
                    <button
                        className="btn btn-outline-primary"
                        onClick={() => window.location.href = '/dashboard'}
                    >
                        <i className="fas fa-arrow-left me-2" />
                        Back to Dashboard
                    </button>
                </div>
            </div>
        </div>
    )
}

export default Statement