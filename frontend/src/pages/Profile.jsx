// ===== FILE: src/pages/Profile.jsx =====
import React, { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth.js'
import { useApi } from '../hooks/useApi.js'
import { apiService } from '../services/api'
import { formatCurrency } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import StatusBadge from '../components/ui/StatusBadge'

const Profile = () => {
    const { user, logout } = useAuth()
    const [customer, setCustomer] = useState(null)
    const [accounts, setAccounts] = useState([])

    // Fetch customer profile
    const { data: customerData, loading: customerLoading } = useApi(
        () => apiService.getCustomerProfile(),
        []
    )

    // Fetch accounts
    const { data: accountsData, loading: accountsLoading } = useApi(
        () => apiService.getAccounts(),
        []
    )

    useEffect(() => {
        if (customerData && customerData.resultCode === "00") {
            setCustomer(customerData.data)
        }
    }, [customerData])

    useEffect(() => {
        if (accountsData && accountsData.resultCode === "00") {
            const accountList = accountsData.data?.content || accountsData.data || []
            setAccounts(accountList)
        }
    }, [accountsData])

    const handleViewAccountDetails = (account) => {
        window.location.href = `/account/${account.accountNumber}/details`
    }

    if (customerLoading || accountsLoading) {
        return <LoadingSpinner text="Loading profile..." />
    }

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={logout} />

            <div className="container mt-4">
                {/* Profile Header */}
                <div className="row justify-content-center mb-5">
                    <div className="col-lg-8">
                        <div
                            className="card border-0 text-white position-relative overflow-hidden"
                            style={{
                                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                borderRadius: '16px'
                            }}
                        >
                            <div className="card-body text-center py-5">
                                <div className="mb-4">
                                    <div
                                        className="mx-auto d-flex align-items-center justify-content-center text-white fw-bold"
                                        style={{
                                            width: '80px',
                                            height: '80px',
                                            background: 'rgba(255, 255, 255, 0.2)',
                                            border: '3px solid rgba(255, 255, 255, 0.3)',
                                            borderRadius: '50%',
                                            fontSize: '2rem'
                                        }}
                                    >
                                        {customer?.firstName?.charAt(0) || user?.username?.charAt(0) || 'U'}
                                        {customer?.lastName?.charAt(0) || ''}
                                    </div>
                                </div>

                                <h2 className="fw-bold mb-2">
                                    {customer?.firstName && customer?.lastName
                                        ? `${customer.firstName} ${customer.lastName}`
                                        : user?.username
                                    }
                                </h2>
                                <p className="mb-3 opacity-90">{customer?.email || user?.username}</p>

                                <span className="badge bg-light text-primary px-3 py-2 rounded-pill">
                                    <i className="fas fa-user me-1" />
                                    {customer?.type || 'USER'}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Accounts Section */}
                <div className="row mb-5">
                    <div className="col-12">
                        <h4 className="fw-bold mb-4 d-flex align-items-center">
                            <i className="fas fa-credit-card me-2 text-primary" />
                            My Accounts
                        </h4>
                    </div>

                    {accounts.length > 0 ? (
                        accounts.map(account => (
                            <div key={account.accountNumber} className="col-lg-4 col-md-6 mb-4">
                                <div className="card h-100 shadow-sm" style={{ borderRadius: '12px' }}>
                                    <div className="card-body">
                                        <div className="d-flex justify-content-between align-items-start mb-3">
                                            <div>
                                                <h6 className="mb-1">Account</h6>
                                                <small className="text-muted">****{account.accountNumber?.slice(-4)}</small>
                                            </div>
                                            <StatusBadge status={account.status} />
                                        </div>

                                        <div className="text-center mb-3">
                                            <h4 className="fw-bold">{formatCurrency(account.balance)}</h4>
                                            <small className="text-muted">Current Balance</small>
                                        </div>

                                        <button
                                            className="btn btn-outline-primary btn-sm w-100"
                                            onClick={() => handleViewAccountDetails(account)}
                                        >
                                            <i className="fas fa-eye me-1" />
                                            View Details
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="col-12 text-center py-5">
                            <i className="fas fa-credit-card text-muted" style={{ fontSize: '3rem' }} />
                            <h5 className="text-muted mt-3">No accounts found</h5>
                        </div>
                    )}
                </div>

                {/* Profile Information */}
                <div className="row">
                    <div className="col-12">
                        <div className="card shadow-sm" style={{ borderRadius: '12px' }}>
                            <div className="card-header bg-light">
                                <h5 className="mb-0 d-flex align-items-center">
                                    <i className="fas fa-info-circle me-2 text-primary" />
                                    Profile Information
                                </h5>
                            </div>
                            <div className="card-body">
                                <div className="row">
                                    <div className="col-md-6">
                                        <div className="mb-3">
                                            <strong className="text-muted">Username:</strong>
                                            <div className="mt-1">{user?.username || 'Not provided'}</div>
                                        </div>
                                        <div className="mb-3">
                                            <strong className="text-muted">First Name:</strong>
                                            <div className="mt-1">{customer?.firstName || 'Not provided'}</div>
                                        </div>
                                        <div className="mb-3">
                                            <strong className="text-muted">Last Name:</strong>
                                            <div className="mt-1">{customer?.lastName || 'Not provided'}</div>
                                        </div>
                                        <div className="mb-3">
                                            <strong className="text-muted">Email:</strong>
                                            <div className="mt-1">{customer?.email || 'Not provided'}</div>
                                        </div>
                                    </div>
                                    <div className="col-md-6">
                                        <div className="mb-3">
                                            <strong className="text-muted">Phone Number:</strong>
                                            <div className="mt-1">{customer?.phoneNumber || 'Not provided'}</div>
                                        </div>
                                        <div className="mb-3">
                                            <strong className="text-muted">Customer Type:</strong>
                                            <div className="mt-1">
                                                <span className="badge bg-info text-dark">
                                                    {customer?.type || 'Not specified'}
                                                </span>
                                            </div>
                                        </div>
                                        <div className="mb-3">
                                            <strong className="text-muted">Customer Since:</strong>
                                            <div className="mt-1">
                                                {customer?.createDate ?
                                                    new Date(customer.createDate).toLocaleDateString() :
                                                    'Not available'
                                                }
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <footer className="text-center py-4 border-top mt-5">
                    <p className="text-muted mb-0">© 2025 Demo Banking</p>
                </footer>
            </div>
        </div>
    )
}

export default Profile