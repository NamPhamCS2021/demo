// ===== src/pages/Statement.jsx =====
import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { formatCurrency, formatDateTime } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'

const fetchWithAuth = async (url, options = {}) => {
    const finalOptions = {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        },
        ...options
    }

    const response = await fetch(url, finalOptions)
    if (!response.ok) {
        const text = await response.text()
        throw new Error(`HTTP error! status: ${response.status}, body: ${text}`)
    }

    // Avoid parsing empty response body
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
        return response.json()
    } else {
        return {}
    }
}


const Statement = () => {
    const [searchParams] = useSearchParams()
    const [user, setUser] = useState(null)
    const [customer, setCustomer] = useState(null)
    const [accounts, setAccounts] = useState([])
    const [accountSummaries, setAccountSummaries] = useState({})
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const customerPublicId = searchParams.get('customerId')
    const [currentYear, setCurrentYear] = useState(parseInt(searchParams.get('year')) || new Date().getFullYear())
    const [currentMonth, setCurrentMonth] = useState(parseInt(searchParams.get('month')) || new Date().getMonth() + 1)

    useEffect(() => {
        if (customerPublicId) {
            fetchStatementData()
        }
    }, [customerPublicId, currentYear, currentMonth])

    const fetchStatementData = async () => {
        setLoading(true)
        setError('')

        try {
            // Fetch user
            const userResult = await fetchWithAuth('/api/auth/me')
            if (userResult.resultCode === '00') {
                setUser(userResult.data)
            }

            // Fetch customer profile
            const customerResult = await fetchWithAuth('/api/customers/profile')
            if (customerResult.resultCode === '00') {
                setCustomer(customerResult.data)
            }

            // Fetch all customer accounts
            const accountsResult = await fetchWithAuth('/api/accounts/me')
            if (accountsResult.resultCode === '00') {
                const accountList = accountsResult.data?.content || []
                setAccounts(accountList)

                // Fetch transactions for each account
                await fetchAccountTransactions(accountList)
            }

        } catch (err) {
            console.error('Error fetching statement:', err)
            setError('Failed to load statement: ' + err.message)
        } finally {
            setLoading(false)
        }
    }

    const fetchAccountTransactions = async (accountList) => {
        const summaries = {}

        // Get start and end dates for the month
        const startDate = new Date(currentYear, currentMonth - 1, 1)
        const endDate = new Date(currentYear, currentMonth, 0, 23, 59, 59)

        for (const account of accountList) {
            try {
                const startDate = new Date(currentYear, currentMonth - 1, 1)
                const endDate = new Date(currentYear, currentMonth, 0, 23, 59, 59)
                // Fetch transactions for this account for the month
                const searchPayload = {
                    from: startDate.toISOString(),
                    to: endDate.toISOString()
                }

                const result = await fetchWithAuth(
                    `/api/transactions/account/accountnumber/${account.accountNumber}/search?page=0&size=1000&sort=createdAt&direction=ASC`,
                    {
                        method: 'PUT',
                        body: JSON.stringify(searchPayload || {}),
                        headers: { 'Content-Type': 'application/json' }
                    }
                )

                if (result.resultCode === '00') {
                    const transactions = result.data?.content || []

                    // Calculate opening balance (balance before first transaction)
                    let openingBalance = account.balance
                    transactions.forEach(txn => {
                        // Reverse calculate - subtract all transactions from current balance
                        if (txn.type === 'DEPOSIT' || (txn.type === 'TRANSFER' && txn.receiverNumber === account.accountNumber)) {
                            openingBalance -= txn.amount
                        } else {
                            openingBalance += txn.amount
                        }
                    })

                    // Calculate totals
                    let totalCredits = 0
                    let totalDebits = 0

                    transactions.forEach(txn => {
                        if (txn.type === 'DEPOSIT' || (txn.type === 'TRANSFER' && txn.receiverNumber === account.accountNumber)) {
                            totalCredits += txn.amount
                        } else {
                            totalDebits += txn.amount
                        }
                    })

                    const closingBalance = openingBalance + totalCredits - totalDebits

                    summaries[account.accountNumber] = {
                        account,
                        transactions,
                        openingBalance,
                        closingBalance,
                        totalCredits,
                        totalDebits
                    }
                }
            } catch (err) {
                console.error(`Error fetching transactions for account ${account.accountNumber}:`, err)
            }
        }

        setAccountSummaries(summaries)
    }

    const handleLogout = async () => {
        try {
            await fetchWithAuth('/api/auth/logout')
            window.location.href = '/login'
        } catch (err) {
            window.location.href = '/login'
        }
    }

    const handlePrevMonth = () => {
        if (currentMonth === 1) {
            setCurrentMonth(12)
            setCurrentYear(currentYear - 1)
        } else {
            setCurrentMonth(currentMonth - 1)
        }
    }

    const handleNextMonth = () => {
        if (currentMonth === 12) {
            setCurrentMonth(1)
            setCurrentYear(currentYear + 1)
        } else {
            setCurrentMonth(currentMonth + 1)
        }
    }

    const getMonthName = (month) => {
        const months = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ]
        return months[month - 1]
    }

    const handlePrint = () => {
        window.print()
    }

    if (loading) {
        return <LoadingSpinner text="Loading statement..." />
    }

    if (!customerPublicId) {
        return (
            <div className="min-h-screen bg-light">
                <Navbar user={user} onLogout={handleLogout} />
                <div className="container mt-4">
                    <div className="alert alert-warning">
                        <h4>Missing Customer ID</h4>
                        <p>Please provide a valid customer ID to view the statement.</p>
                        <button className="btn btn-primary" onClick={() => window.location.href = '/dashboard'}>
                            Back to Dashboard
                        </button>
                    </div>
                </div>
            </div>
        )
    }

    if (error) {
        return (
            <div className="min-h-screen bg-light">
                <Navbar user={user} onLogout={handleLogout} />
                <div className="container mt-4">
                    <div className="alert alert-danger">
                        <h4>Error</h4>
                        <p>{error}</p>
                        <button className="btn btn-primary" onClick={() => window.location.href = '/dashboard'}>
                            Back to Dashboard
                        </button>
                    </div>
                </div>
            </div>
        )
    }

    const totalOpeningBalance = Object.values(accountSummaries).reduce((sum, s) => sum + s.openingBalance, 0)
    const totalClosingBalance = Object.values(accountSummaries).reduce((sum, s) => sum + s.closingBalance, 0)
    const totalCredits = Object.values(accountSummaries).reduce((sum, s) => sum + s.totalCredits, 0)
    const totalDebits = Object.values(accountSummaries).reduce((sum, s) => sum + s.totalDebits, 0)

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={handleLogout} />

            <div className="container mt-4">
                {/* Header */}
                <div className="row mb-4">
                    <div className="col-12">
                        <div className="d-flex justify-content-between align-items-center">
                            <div>
                                <h2 className="fw-bold mb-1">Monthly Statement</h2>
                                <p className="text-muted mb-0">
                                    {customer?.firstName} {customer?.lastName}
                                </p>
                            </div>
                            <div className="d-print-none">
                                <button className="btn btn-outline-primary me-2" onClick={handlePrint}>
                                    <i className="fas fa-print me-2" />
                                    Print
                                </button>
                                <button className="btn btn-primary" onClick={() => window.location.href = '/dashboard'}>
                                    <i className="fas fa-arrow-left me-2" />
                                    Dashboard
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Statement Card */}
                <div className="card shadow-sm mb-4" style={{ borderRadius: '12px' }}>
                    <div className="card-header bg-light d-flex justify-content-between align-items-center">
                        <h3 className="mb-0">
                            <i className="fas fa-file-invoice-dollar me-2 text-primary" />
                            Statement Period
                        </h3>
                        <div className="d-flex align-items-center d-print-none">
                            <button className="btn btn-outline-secondary btn-sm me-2" onClick={handlePrevMonth}>
                                <i className="fas fa-chevron-left" />
                            </button>
                            <span className="fw-bold mx-3" style={{ minWidth: '150px', textAlign: 'center' }}>
                                {getMonthName(currentMonth)} {currentYear}
                            </span>
                            <button className="btn btn-outline-secondary btn-sm ms-2" onClick={handleNextMonth}>
                                <i className="fas fa-chevron-right" />
                            </button>
                        </div>
                    </div>

                    <div className="card-body">
                        {/* Overall Summary */}
                        <div className="row mb-4">
                            <div className="col-md-3 text-center">
                                <div className="border-end">
                                    <h6 className="text-muted mb-2">Total Opening Balance</h6>
                                    <h4 className="fw-bold text-primary">{formatCurrency(totalOpeningBalance)}</h4>
                                </div>
                            </div>
                            <div className="col-md-3 text-center">
                                <div className="border-end">
                                    <h6 className="text-muted mb-2">Total Closing Balance</h6>
                                    <h4 className="fw-bold text-dark">{formatCurrency(totalClosingBalance)}</h4>
                                </div>
                            </div>
                            <div className="col-md-3 text-center">
                                <div className="border-end">
                                    <h6 className="text-success mb-2">Total Credits</h6>
                                    <h4 className="fw-bold text-success">{formatCurrency(totalCredits)}</h4>
                                </div>
                            </div>
                            <div className="col-md-3 text-center">
                                <h6 className="text-danger mb-2">Total Debits</h6>
                                <h4 className="fw-bold text-danger">{formatCurrency(totalDebits)}</h4>
                            </div>
                        </div>

                        <hr />

                        {/* Per-Account Breakdown */}
                        {Object.entries(accountSummaries).map(([accountNumber, summary]) => (
                            <div key={accountNumber} className="mb-5">
                                <div className="card border">
                                    <div className="card-header bg-light">
                                        <h5 className="mb-0">
                                            <i className="fas fa-credit-card me-2" />
                                            Account ****{accountNumber.slice(-4)}
                                            <span className="badge bg-secondary ms-2">{summary.transactions.length} transactions</span>
                                        </h5>
                                    </div>
                                    <div className="card-body">
                                        {/* Account Summary */}
                                        <div className="row mb-3">
                                            <div className="col-md-3">
                                                <small className="text-muted">Opening Balance</small>
                                                <div className="fw-bold">{formatCurrency(summary.openingBalance)}</div>
                                            </div>
                                            <div className="col-md-3">
                                                <small className="text-muted">Closing Balance</small>
                                                <div className="fw-bold">{formatCurrency(summary.closingBalance)}</div>
                                            </div>
                                            <div className="col-md-3">
                                                <small className="text-success">Total Credits</small>
                                                <div className="fw-bold text-success">+{formatCurrency(summary.totalCredits)}</div>
                                            </div>
                                            <div className="col-md-3">
                                                <small className="text-danger">Total Debits</small>
                                                <div className="fw-bold text-danger">-{formatCurrency(summary.totalDebits)}</div>
                                            </div>
                                        </div>

                                        {/* Transactions Table */}
                                        {summary.transactions.length > 0 ? (
                                            <div className="table-responsive">
                                                <table className="table table-hover table-sm">
                                                    <thead className="table-light">
                                                    <tr>
                                                        <th>Date</th>
                                                        <th>Type</th>
                                                        <th>Description</th>
                                                        <th className="text-end">Debit</th>
                                                        <th className="text-end">Credit</th>
                                                        <th className="text-end">Balance</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {/* Opening Balance Row */}
                                                    <tr className="table-info">
                                                        <td colSpan="3"><strong>Opening Balance</strong></td>
                                                        <td></td>
                                                        <td></td>
                                                        <td className="text-end"><strong>{formatCurrency(summary.openingBalance)}</strong></td>
                                                    </tr>

                                                    {/* Transaction Rows */}
                                                    {summary.transactions.map((txn, idx) => {
                                                        const isCredit = txn.type === 'DEPOSIT' || (txn.type === 'TRANSFER' && txn.receiverNumber === accountNumber)
                                                        let runningBalance = summary.openingBalance

                                                        // Calculate running balance
                                                        for (let i = 0; i <= idx; i++) {
                                                            const t = summary.transactions[i]
                                                            if (t.type === 'DEPOSIT' || (t.type === 'TRANSFER' && t.receiverNumber === accountNumber)) {
                                                                runningBalance += t.amount
                                                            } else {
                                                                runningBalance -= t.amount
                                                            }
                                                        }

                                                        return (
                                                            <tr key={idx}>
                                                                <td><small>{formatDateTime(txn.timestamp)}</small></td>
                                                                <td>
                                                                        <span className={`badge ${isCredit ? 'bg-success' : 'bg-danger'}`}>
                                                                            {txn.type}
                                                                        </span>
                                                                </td>
                                                                <td>
                                                                    <small>
                                                                        {txn.location || 'N/A'}
                                                                        {txn.type === 'TRANSFER' && (
                                                                            <span className="text-muted d-block">
                                                                                    {isCredit ? 'From' : 'To'}: ****{(isCredit ? txn.accountNumber : txn.receiverNumber)?.slice(-4)}
                                                                                </span>
                                                                        )}
                                                                    </small>
                                                                </td>
                                                                <td className="text-end text-danger">
                                                                    {!isCredit && formatCurrency(txn.amount)}
                                                                </td>
                                                                <td className="text-end text-success">
                                                                    {isCredit && formatCurrency(txn.amount)}
                                                                </td>
                                                                <td className="text-end fw-bold">
                                                                    {formatCurrency(runningBalance)}
                                                                </td>
                                                            </tr>
                                                        )
                                                    })}

                                                    {/* Closing Balance Row */}
                                                    <tr className="table-info">
                                                        <td colSpan="3"><strong>Closing Balance</strong></td>
                                                        <td className="text-end text-danger"><strong>{formatCurrency(summary.totalDebits)}</strong></td>
                                                        <td className="text-end text-success"><strong>{formatCurrency(summary.totalCredits)}</strong></td>
                                                        <td className="text-end"><strong>{formatCurrency(summary.closingBalance)}</strong></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        ) : (
                                            <div className="text-center py-4 text-muted">
                                                <i className="fas fa-inbox fa-2x mb-2" />
                                                <p>No transactions for this period</p>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}

                        {Object.keys(accountSummaries).length === 0 && (
                            <div className="text-center py-5">
                                <i className="fas fa-file-invoice fa-3x text-muted mb-3" />
                                <h5 className="text-muted">No Statement Data</h5>
                                <p className="text-muted">No accounts or transactions available for this period</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Statement