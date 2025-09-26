// ===== src/pages/Dashboard.jsx =====
import React, { useState, useEffect } from 'react'
import { formatCurrency } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import StatusBadge from '../components/ui/StatusBadge'

// Simple API service for this component
const fetchWithAuth = async (url) => {
    const response = await fetch(url, {
        credentials: "include",
        headers: { "Content-Type": "application/json" }
    })
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
    }
    return response.json()
}

const Dashboard = () => {
    const [user, setUser] = useState(null)
    const [customer, setCustomer] = useState(null)
    const [accounts, setAccounts] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        console.log('Dashboard mounted - fetching data...')
        fetchAllData()
    }, [])

    const fetchAllData = async () => {
        try {
            setLoading(true)
            setError('')
            console.log('Starting to fetch all data...')

            // Fetch user data
            console.log('Fetching user...')
            const userResult = await fetchWithAuth('/api/auth/me')
            console.log('User result:', userResult)
            if (userResult.resultCode === "00") {
                setUser(userResult.data)
            }

            // Fetch customer data
            console.log('Fetching customer...')
            const customerResult = await fetchWithAuth('/api/customers/profile')
            console.log('Customer result:', customerResult)
            if (customerResult.resultCode === "00") {
                setCustomer(customerResult.data)
            }

            // Fetch accounts data
            console.log('Fetching accounts...')
            const accountsResult = await fetchWithAuth('/api/accounts/me')
            console.log('Accounts result:', accountsResult)
            if (accountsResult.resultCode === "00") {
                // Extract accounts from the paginated response
                const accountList = accountsResult.data?.content || []
                setAccounts(accountList)
                console.log('Set accounts:', accountList)
            }

            console.log('All data fetched successfully')
        } catch (err) {
            console.error('Error fetching data:', err)
            setError(err.message || 'Failed to load dashboard data')
        } finally {
            setLoading(false)
            console.log('Loading set to false')
        }
    }

    const handleLogout = async () => {
        try {
            await fetchWithAuth('/api/auth/logout')
            window.location.href = '/login'
        } catch (err) {
            console.error('Logout error:', err)
            window.location.href = '/login'
        }
    }

    const handleViewAccountDetails = (account) => {
        window.location.href = `/account/${account.accountNumber}/details`
    }

    console.log('Render - loading:', loading, 'user:', user, 'customer:', customer, 'accounts:', accounts)

    if (loading) {
        return (
            <div className="min-h-screen bg-light">
                <LoadingSpinner text="Loading your banking dashboard..." />
            </div>
        )
    }

    if (error) {
        return (
            <div className="min-h-screen bg-light d-flex align-items-center justify-content-center">
                <div className="text-center">
                    <div className="alert alert-danger">
                        <h4>Error Loading Dashboard</h4>
                        <p>{error}</p>
                        <button className="btn btn-primary" onClick={fetchAllData}>
                            <i className="fas fa-refresh me-2"></i>
                            Retry
                        </button>
                    </div>
                </div>
            </div>
        )
    }

    const totalBalance = accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0)
    const activeAccounts = accounts.filter(acc => acc.status === 'ACTIVE').length

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={handleLogout} />

            <div className="container mt-4">
                {/* Debug Info - Remove this in production */}
                <div className="alert alert-info mb-4">
                    <h6>Debug Info:</h6>
                    <p><strong>User:</strong> {user ? user.username : 'None'}</p>
                    <p><strong>Customer:</strong> {customer ? `${customer.firstName} ${customer.lastName}` : 'None'}</p>
                    <p><strong>Accounts:</strong> {accounts.length} found</p>
                </div>

                {/* Welcome Header */}
                <div className="row mb-5">
                    <div className="col-12">
                        <div className="card border-0" style={{
                            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                            borderRadius: '16px',
                            color: 'white'
                        }}>
                            <div className="card-body py-5 text-center">
                                <h1 className="display-5 fw-bold mb-3">
                                    Welcome back, {customer?.firstName || user?.username || 'User'}!
                                </h1>
                                <p className="lead opacity-90">
                                    Manage your finances with ease and security
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Stats Overview */}
                <div className="row mb-5">
                    <div className="col-md-4 mb-4">
                        <div className="card border-0 shadow-sm text-center" style={{ borderRadius: '12px' }}>
                            <div className="card-body py-4">
                                <i className="fas fa-dollar-sign text-primary mb-3" style={{ fontSize: '2.5rem' }} />
                                <h3 className="fw-bold text-dark">{formatCurrency(totalBalance)}</h3>
                                <p className="text-muted mb-0">Total Balance</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-4 mb-4">
                        <div className="card border-0 shadow-sm text-center" style={{ borderRadius: '12px' }}>
                            <div className="card-body py-4">
                                <i className="fas fa-credit-card text-success mb-3" style={{ fontSize: '2.5rem' }} />
                                <h3 className="fw-bold text-dark">{activeAccounts}</h3>
                                <p className="text-muted mb-0">Active Accounts</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-4 mb-4">
                        <div className="card border-0 shadow-sm text-center" style={{ borderRadius: '12px' }}>
                            <div className="card-body py-4">
                                <i className="fas fa-shield-alt text-info mb-3" style={{ fontSize: '2.5rem' }} />
                                <h3 className="fw-bold text-dark">Secure</h3>
                                <p className="text-muted mb-0">Bank-Grade Security</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Quick Actions */}
                <div className="row mb-5">
                    <div className="col-12">
                        <h4 className="fw-bold mb-4">Quick Actions</h4>
                    </div>
                    <div className="col-md-3 mb-4">
                        <div className="card border-0 shadow-sm text-center h-100" style={{ borderRadius: '12px', cursor: 'pointer' }}>
                            <div className="card-body py-4">
                                <i className="fas fa-sync-alt text-primary mb-3" style={{ fontSize: '2rem' }} />
                                <h6 className="fw-bold">Refresh Data</h6>
                                <p className="text-muted small mb-0">Update all information</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3 mb-4">
                        <div
                            className="card border-0 shadow-sm text-center h-100"
                            style={{ borderRadius: '12px', cursor: 'pointer', transition: 'transform 0.2s' }}
                            onClick={() => window.location.href = '/profile'}
                            onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
                            onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                        >
                            <div className="card-body py-4">
                                <i className="fas fa-user text-primary mb-3" style={{ fontSize: '2rem' }} />
                                <h6 className="fw-bold">My Profile</h6>
                                <p className="text-muted small mb-0">View and edit profile</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* My Accounts */}
                <div className="row mb-5">
                    <div className="col-12">
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <h4 className="fw-bold mb-0">
                                <i className="fas fa-credit-card me-2 text-primary" />
                                My Accounts ({accounts.length})
                            </h4>
                            <button className="btn btn-outline-primary rounded-pill" onClick={fetchAllData}>
                                <i className="fas fa-sync-alt me-2" />
                                Refresh
                            </button>
                        </div>
                    </div>

                    {accounts.length > 0 ? (
                        accounts.map((account, index) => (
                            <div key={account.accountNumber || index} className="col-lg-4 col-md-6 mb-4">
                                <div className="card h-100 shadow-sm" style={{
                                    borderRadius: '12px',
                                    transition: 'transform 0.2s, box-shadow 0.2s',
                                    cursor: 'pointer'
                                }}
                                     onMouseEnter={(e) => {
                                         e.currentTarget.style.transform = 'translateY(-4px)'
                                         e.currentTarget.style.boxShadow = '0 8px 25px rgba(0,0,0,0.15)'
                                     }}
                                     onMouseLeave={(e) => {
                                         e.currentTarget.style.transform = 'translateY(0)'
                                         e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.12)'
                                     }}>
                                    <div className="card-body p-4">
                                        <div className="d-flex justify-content-between align-items-start mb-3">
                                            <div>
                                                <h6 className="card-title mb-1">
                                                    <i className="fas fa-credit-card me-2 text-primary" />
                                                    Account
                                                </h6>
                                                <small className="text-muted">****{account.accountNumber?.slice(-4)}</small>
                                            </div>
                                            <StatusBadge status={account.status} />
                                        </div>

                                        <div className="text-center mb-4">
                                            <h3 className="fw-bold text-dark mb-1">
                                                {formatCurrency(account.balance || 0)}
                                            </h3>
                                            <small className="text-muted">Current Balance</small>
                                        </div>

                                        <div className="row text-center mb-3">
                                            <div className="col-6">
                                                <div className="border-end">
                                                    <div className="fw-bold text-dark">
                                                        {formatCurrency(account.accountLimit || 0)}
                                                    </div>
                                                    <small className="text-muted">Limit</small>
                                                </div>
                                            </div>
                                            <div className="col-6">
                                                <div className="fw-bold text-dark">
                                                    {account.openingDate ?
                                                        new Date(account.openingDate).getFullYear() : 'N/A'
                                                    }
                                                </div>
                                                <small className="text-muted">Since</small>
                                            </div>
                                        </div>

                                        <button
                                            className="btn btn-primary w-100 rounded-pill"
                                            onClick={() => handleViewAccountDetails(account)}
                                        >
                                            <i className="fas fa-eye me-2" />
                                            View Details
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="col-12">
                            <div className="text-center py-5">
                                <i className="fas fa-credit-card text-muted" style={{ fontSize: '4rem' }} />
                                <h5 className="text-muted mt-3">No accounts found</h5>
                                <p className="text-muted mb-4">No accounts are currently available</p>
                            </div>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <footer className="text-center py-4 border-top">
                    <p className="text-muted mb-0">
                        © 2025 Demo Banking | Server Time: {new Date().toLocaleString()}
                    </p>
                </footer>
            </div>
        </div>
    )
}

export default Dashboard