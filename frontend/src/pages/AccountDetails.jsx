// ===== FILE: src/pages/AccountDetails.jsx =====
import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import { apiService } from '../services/api'
import { formatCurrency, formatDateTime } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import StatusBadge from '../components/ui/StatusBadge'

const AccountDetails = () => {
    const { accountNumber } = useParams()
    const { user, logout } = useAuth()

    const [account, setAccount] = useState(null)
    const [transactions, setTransactions] = useState([])
    const [loading, setLoading] = useState(true)
    const [transactionsLoading, setTransactionsLoading] = useState(false)
    const [error, setError] = useState('')

    const [searchFilters, setSearchFilters] = useState({
        minAmount: '',
        maxAmount: '',
        location: '',
        from: '',
        to: '',
        checked: ''
    })

    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)

    useEffect(() => {
        if (accountNumber) {
            fetchAccountDetails()
            fetchTransactions()
        }
    }, [accountNumber])

    const fetchAccountDetails = async () => {
        try {
            const result = await apiService.getAccountByAccountNumber(accountNumber)
            if (result.resultCode === '00') {
                setAccount(result.data)
            } else {
                setError(result.resultMessage || 'Account not found')
            }
        } catch (err) {
            setError('Failed to load account details')
        } finally {
            setLoading(false)
        }
    }

    const fetchTransactions = async (page = 0) => {
        setTransactionsLoading(true)
        try {
            const searchPayload = {
                minAmount: searchFilters.minAmount ? parseFloat(searchFilters.minAmount) : null,
                maxAmount: searchFilters.maxAmount ? parseFloat(searchFilters.maxAmount) : null,
                location: searchFilters.location || null,
                from: searchFilters.from || null,
                to: searchFilters.to || null,
                checked: searchFilters.checked !== '' ? searchFilters.checked === 'true' : null
            }

            // Remove null values
            Object.keys(searchPayload).forEach(key => {
                if (searchPayload[key] === null || searchPayload[key] === '') {
                    delete searchPayload[key]
                }
            })

            const result = await apiService.searchTransactionsByAccountNumber(
                accountNumber,
                searchPayload,
                { page, size: 20, sort: 'createdAt', direction: 'DESC' }
            )

            if (result.resultCode === '00') {
                const pageData = result.data
                setTransactions(pageData.content || [])
                setCurrentPage(pageData.number || 0)
                setTotalPages(pageData.totalPages || 0)
            } else {
                setTransactions([])
            }
        } catch (err) {
            setTransactions([])
        } finally {
            setTransactionsLoading(false)
        }
    }

    const handleSearchFilterChange = (e) => {
        const { name, value } = e.target
        setSearchFilters(prev => ({
            ...prev,
            [name]: value
        }))
    }

    const handleSearch = (e) => {
        e.preventDefault()
        setCurrentPage(0)
        fetchTransactions(0)
    }

    const TransactionCard = ({ transaction }) => {
        const isPositive = transaction.type === 'DEPOSIT'
        const amountClass = isPositive ? 'text-success' : 'text-danger'
        const amountPrefix = isPositive ? '+' : '-'

        return (
            <div className="col-md-6 col-lg-4 mb-3">
                <div className="card h-100 shadow-sm" style={{ borderRadius: '8px' }}>
                    <div className="card-body">
                        <div className="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <h6 className="mb-1">
                                    <i className={`fas ${transaction.type === 'DEPOSIT' ? 'fa-arrow-down' : transaction.type === 'WITHDRAWAL' ? 'fa-arrow-up' : 'fa-exchange-alt'} me-2`} />
                                    {transaction.type}
                                </h6>
                                <small className="text-muted">
                                    {formatDateTime(transaction.timestamp)}
                                </small>
                            </div>
                            <span className={`badge ${transaction.checked ? 'bg-success' : 'bg-warning'}`}>
                                {transaction.checked ? 'Cleared' : 'Pending'}
                            </span>
                        </div>
                        <div className={`h5 ${amountClass} text-center mb-2`}>
                            {amountPrefix}{formatCurrency(Math.abs(transaction.amount))}
                        </div>
                        {transaction.location && (
                            <div className="text-center">
                                <small className="text-muted">
                                    <i className="fas fa-map-marker-alt me-1" />
                                    {transaction.location}
                                </small>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        )
    }

    if (loading) return <LoadingSpinner text="Loading account details..." />
    if (error) return <div className="alert alert-danger">{error}</div>
    if (!account) return <div className="alert alert-warning">Account not found</div>

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={logout} />

            <div className="container mt-4">
                {/* Account Header */}
                <div className="row mb-4">
                    <div className="col-12">
                        <div
                            className="card border-0 text-white"
                            style={{
                                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                borderRadius: '16px'
                            }}
                        >
                            <div className="card-body py-5">
                                <div className="row align-items-center">
                                    <div className="col-md-8">
                                        <h1 className="display-6 fw-bold mb-3">Account Details</h1>
                                        <p className="mb-2 opacity-90">Account: {account.accountNumber}</p>
                                        <p className="mb-1 opacity-90">Customer: {account.customerName}</p>
                                        <StatusBadge status={account.status} />
                                    </div>
                                    <div className="col-md-4 text-md-end">
                                        <div style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>
                                            {formatCurrency(account.balance)}
                                        </div>
                                        <p className="opacity-90 mb-2">Current Balance</p>
                                        <p className="opacity-75">
                                            Limit: {formatCurrency(account.accountLimit)}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Transactions */}
                <div className="row">
                    <div className="col-12">
                        <div className="card shadow-sm" style={{ borderRadius: '12px' }}>
                            <div className="card-header bg-light d-flex justify-content-between align-items-center">
                                <h5 className="mb-0">
                                    <i className="fas fa-exchange-alt me-2" />
                                    Transaction History
                                </h5>
                                <button
                                    className="btn btn-sm btn-outline-primary"
                                    onClick={() => fetchTransactions(currentPage)}
                                    disabled={transactionsLoading}
                                >
                                    <i className="fas fa-sync-alt me-1" />
                                    Refresh
                                </button>
                            </div>
                            <div className="card-body">
                                {transactionsLoading ? (
                                    <LoadingSpinner text="Loading transactions..." />
                                ) : transactions.length > 0 ? (
                                    <div className="row">
                                        {transactions.map((transaction, index) => (
                                            <TransactionCard
                                                key={transaction.id || `transaction-${index}`}
                                                transaction={transaction}
                                            />
                                        ))}
                                    </div>
                                ) : (
                                    <div className="text-center py-5">
                                        <i className="fas fa-exchange-alt text-muted" style={{ fontSize: '3rem' }} />
                                        <h5 className="text-muted mt-3">No transactions found</h5>
                                        <p className="text-muted">No transactions available for this account</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AccountDetails