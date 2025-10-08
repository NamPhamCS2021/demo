// ===== src/pages/Dashboard.jsx =====
import React, { useState, useEffect } from 'react'
import { formatCurrency } from '../utils/formatters'

import Navbar from '../components/layout/Navbar'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import StatusBadge from '../components/ui/StatusBadge'

// Simple API service
const fetchWithAuth = async (url, options = {}) => {
    const response = await fetch(url, {
        ...options,
        credentials: "include",
        headers: { "Content-Type": "application/json", ...(options.headers || {}) }
    })
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
    }
    return response.json()
}

// Account Creation Modal Component
const AccountCreationModal = ({ show, onHide, onAccountCreated, customerPublicId }) => {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)
    const [newAccount, setNewAccount] = useState(null)

    const handleCreateAccount = async () => {
        if (!customerPublicId) {
            setError('Customer ID is required')
            return
        }

        setLoading(true)
        setError('')

        try {
            const result = await fetchWithAuth('/api/accounts', {
                method: 'POST',
                body: JSON.stringify({ customerPublicId })
            })
            if (result.resultCode === '00') {
                setNewAccount(result.data)
                setSuccess(true)
                setTimeout(() => {
                    onAccountCreated(result.data)
                    onHide()
                }, 2000)
            } else {
                setError(result.resultMessage || 'Failed to create account')
            }
        } catch (error) {
            setError(`Network error: ${error.message}`)
        } finally {
            setLoading(false)
        }
    }

    const handleClose = () => {
        setError('')
        setSuccess(false)
        setNewAccount(null)
        onHide()
    }

    if (!show) return null

    return (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">
                            <i className="fas fa-plus-circle me-2" />
                            {success ? 'Account Created!' : 'Create New Account'}
                        </h5>
                        <button type="button" className="btn-close" onClick={handleClose} />
                    </div>
                    <div className="modal-body">
                        {loading ? (
                            <LoadingSpinner text="Creating your account..." />
                        ) : success ? (
                            <div className="text-center">
                                <i className="fas fa-check-circle text-success" style={{ fontSize: '4rem' }} />
                                <h4 className="text-success mt-3">Success!</h4>
                                {newAccount && (
                                    <div className="mt-3">
                                        <p><strong>Account Number:</strong> {newAccount.accountNumber}</p>
                                        <p><strong>Balance:</strong> {formatCurrency(newAccount.balance)}</p>
                                        <p><strong>Limit:</strong> {formatCurrency(newAccount.accountLimit)}</p>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div>
                                <div className="text-center mb-4">
                                    <i className="fas fa-credit-card text-primary" style={{ fontSize: '3rem' }} />
                                    <h5 className="mt-3">Ready to create your new account?</h5>
                                </div>
                                <div className="alert alert-info">
                                    <h6><i className="fas fa-info-circle me-2" />Account Features:</h6>
                                    <ul className="mb-0">
                                        <li>Instant account activation</li>
                                        <li>Automatic account limit based on customer type</li>
                                        <li>Zero initial balance</li>
                                        <li>Full transaction capabilities</li>
                                    </ul>
                                </div>
                                {error && (
                                    <div className="alert alert-danger">
                                        <i className="fas fa-exclamation-triangle me-2" />
                                        {error}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    <div className="modal-footer">
                        {!success && (
                            <>
                                <button type="button" className="btn btn-secondary" onClick={handleClose}>
                                    Cancel
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={handleCreateAccount}
                                    disabled={loading || !customerPublicId}
                                >
                                    {loading ? 'Creating...' : 'Create Account'}
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

// Money Transfer Modal Component
const MoneyTransferModal = ({ show, onHide, onTransferCompleted, userAccounts }) => {
    const [currentStep, setCurrentStep] = useState(1)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)

    const [selectedAccount, setSelectedAccount] = useState(null)
    const [recipientAccountNumber, setRecipientAccountNumber] = useState('')
    const [amount, setAmount] = useState('')
    const [location, setLocation] = useState('Online Banking')
    const [recipientAccount, setRecipientAccount] = useState(null)
    const [recipientLoading, setRecipientLoading] = useState(false)
    const [recipientError, setRecipientError] = useState('')

    const resetForm = () => {
        setCurrentStep(1)
        setError('')
        setSuccess(false)
        setSelectedAccount(null)
        setRecipientAccountNumber('')
        setAmount('')
        setLocation('Online Banking')
        setRecipientAccount(null)
        setRecipientError('')
    }

    const handleClose = () => {
        resetForm()
        onHide()
    }

    const validateRecipientAccount = async (accountNumber) => {
        if (!accountNumber || accountNumber.length < 10) {
            setRecipientError('Account number must be at least 10 digits')
            setRecipientAccount(null)
            return false
        }

        if (accountNumber === selectedAccount?.accountNumber) {
            setRecipientError('Cannot transfer to the same account')
            setRecipientAccount(null)
            return false
        }

        setRecipientLoading(true)
        setRecipientError('')

        try {
            const result = await fetchWithAuth(`/api/accounts/byAccountNumber/receiver/${accountNumber}`)
            if (result.resultCode === '00') {
                setRecipientAccount(result.data)
                return true
            } else {
                setRecipientError('Account not found or invalid')
                setRecipientAccount(null)
                return false
            }
        } catch (error) {
            setRecipientError('Error validating account')
            setRecipientAccount(null)
            return false
        } finally {
            setRecipientLoading(false)
        }
    }

    const handleRecipientAccountChange = (e) => {
        const value = e.target.value.replace(/\D/g, '')
        setRecipientAccountNumber(value)

        if (value.length >= 10) {
            validateRecipientAccount(value)
        } else {
            setRecipientAccount(null)
            setRecipientError('')
        }
    }

    const handleAmountChange = (e) => {
        const value = e.target.value
        if (/^\d*\.?\d{0,2}$/.test(value)) {
            setAmount(value)
        }
    }

    const handleTransfer = async () => {
        setLoading(true)
        setError('')

        try {
            const transferData = {
                accountNumber: selectedAccount.accountNumber,
                receiverAccountNumber: recipientAccount.accountNumber,
                amount: parseFloat(amount),
                location: location
            }

            const result = await fetchWithAuth('/api/transactions/transferByAccountNumber', {
                method: 'POST',
                body: JSON.stringify(transferData)
            })

            if (result.resultCode === '00') {
                setSuccess(true)
                setCurrentStep(4)
                setTimeout(() => {
                    if (onTransferCompleted) {
                        onTransferCompleted(result.data)
                    }
                }, 2000)
            } else {
                setError(result.resultMessage || 'Transfer failed')
            }
        } catch (error) {
            setError(`Network error: ${error.message}`)
        } finally {
            setLoading(false)
        }
    }

    const validateStep1 = () => selectedAccount !== null
    const validateStep2 = () => recipientAccount !== null && !recipientError
    const validateStep3 = () => {
        const amountNum = parseFloat(amount)
        return amountNum > 0 &&
            amountNum <= selectedAccount?.balance &&
            amountNum <= selectedAccount?.accountLimit &&
            location.trim().length > 0
    }

    const nextStep = () => {
        if (currentStep === 1 && validateStep1()) {
            setCurrentStep(2)
        } else if (currentStep === 2 && validateStep2()) {
            setCurrentStep(3)
        } else if (currentStep === 3 && validateStep3()) {
            handleTransfer()
        }
    }

    const prevStep = () => {
        if (currentStep > 1 && !loading) {
            setCurrentStep(currentStep - 1)
        }
    }

    if (!show) return null

    const activeAccounts = userAccounts?.filter(acc => acc.status === 'ACTIVE') || []

    return (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog modal-dialog-centered modal-lg">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">
                            <i className="fas fa-exchange-alt me-2" />
                            {success ? 'Transfer Complete!' : 'Transfer Money'}
                        </h5>
                        <button type="button" className="btn-close" onClick={handleClose} />
                    </div>

                    <div className="modal-body">
                        {loading && (
                            <div className="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-white bg-opacity-75" style={{ zIndex: 1000 }}>
                                <LoadingSpinner text="Processing transfer..." />
                            </div>
                        )}

                        {error && (
                            <div className="alert alert-danger">
                                <i className="fas fa-exclamation-triangle me-2" />
                                {error}
                            </div>
                        )}

                        {/* Step 1: Select Account */}
                        {currentStep === 1 && !success && (
                            <div>
                                <h6 className="mb-3">Select source account:</h6>
                                {activeAccounts.length > 0 ? (
                                    <div className="row">
                                        {activeAccounts.map(account => (
                                            <div key={account.accountNumber} className="col-md-6 mb-3">
                                                <div
                                                    className={`card p-3 border-2 ${selectedAccount?.accountNumber === account.accountNumber ? 'border-primary bg-light' : 'border-light'}`}
                                                    style={{ cursor: 'pointer' }}
                                                    onClick={() => setSelectedAccount(account)}
                                                >
                                                    <div className="d-flex justify-content-between align-items-center">
                                                        <div>
                                                            <h6 className="mb-1">****{account.accountNumber?.slice(-4)}</h6>
                                                            <p className="text-muted mb-0 small">
                                                                Available: {formatCurrency(account.balance)}
                                                            </p>
                                                        </div>
                                                        {selectedAccount?.accountNumber === account.accountNumber && (
                                                            <i className="fas fa-check-circle text-primary" />
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="text-center py-4">
                                        <i className="fas fa-exclamation-circle text-warning" style={{ fontSize: '2rem' }} />
                                        <p className="mt-2">No active accounts available</p>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Step 2: Recipient */}
                        {currentStep === 2 && !success && (
                            <div>
                                <h6 className="mb-3">Enter recipient's account:</h6>
                                <div className="mb-3">
                                    <input
                                        type="text"
                                        className={`form-control ${recipientError ? 'is-invalid' : recipientAccount ? 'is-valid' : ''}`}
                                        value={recipientAccountNumber}
                                        onChange={handleRecipientAccountChange}
                                        placeholder="Enter recipient account number"
                                        maxLength="20"
                                    />
                                    {recipientError && <div className="invalid-feedback">{recipientError}</div>}
                                </div>
                                {recipientAccount && (
                                    <div className="alert alert-success">
                                        <h6>Account Verified</h6>
                                        <p className="mb-0">
                                            <strong>Account:</strong> ****{recipientAccount.accountNumber?.slice(-4)}<br />
                                            <strong>Name:</strong> {recipientAccount.firstName} {recipientAccount.lastName}
                                        </p>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Step 3: Amount */}
                        {currentStep === 3 && !success && (
                            <div>
                                <h6 className="mb-3">Transfer details:</h6>
                                <div className="mb-3">
                                    <label className="form-label">Amount</label>
                                    <div className="input-group">
                                        <span className="input-group-text">$</span>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={amount}
                                            onChange={handleAmountChange}
                                            placeholder="0.00"
                                        />
                                    </div>
                                    <small className="text-muted">
                                        Maximum: {formatCurrency(Math.min(selectedAccount?.balance || 0, selectedAccount?.accountLimit || 0))}
                                    </small>
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">Location</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={location}
                                        onChange={(e) => setLocation(e.target.value)}
                                    />
                                </div>
                            </div>
                        )}

                        {/* Success */}
                        {success && (
                            <div className="text-center">
                                <i className="fas fa-check-circle text-success" style={{ fontSize: '4rem' }} />
                                <h4 className="text-success mt-3">Transfer Successful!</h4>
                                <p className="text-muted">Amount: {formatCurrency(parseFloat(amount))}</p>
                            </div>
                        )}
                    </div>

                    <div className="modal-footer">
                        {!success ? (
                            <>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={currentStep === 1 ? handleClose : prevStep}
                                    disabled={loading}
                                >
                                    {currentStep === 1 ? 'Cancel' : 'Back'}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={nextStep}
                                    disabled={loading ||
                                        (currentStep === 1 && !validateStep1()) ||
                                        (currentStep === 2 && !validateStep2()) ||
                                        (currentStep === 3 && !validateStep3())
                                    }
                                >
                                    {currentStep === 3 ? 'Complete Transfer' : 'Next'}
                                </button>
                            </>
                        ) : (
                            <button className="btn btn-primary" onClick={handleClose}>
                                Close
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}




// Main Dashboard Component
const Dashboard = () => {
    const [user, setUser] = useState(null)
    const [customer, setCustomer] = useState(null)
    const [accounts, setAccounts] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Modal states
    const [showAccountModal, setShowAccountModal] = useState(false)
    const [showTransferModal, setShowTransferModal] = useState(false)

    useEffect(() => {
        fetchAllData()
    }, [])

    const fetchAllData = async () => {
        try {
            setLoading(true)
            setError('')

            // Fetch user
            const userResult = await fetchWithAuth('/api/auth/me')
            if (userResult.resultCode === "00") {
                setUser(userResult.data)
            }

            // Fetch customer
            const customerResult = await fetchWithAuth('/api/customers/profile')
            if (customerResult.resultCode === "00") {
                setCustomer(customerResult.data)
            }

            // Fetch accounts
            const accountsResult = await fetchWithAuth('/api/accounts/me')
            if (accountsResult.resultCode === "00") {
                setAccounts(accountsResult.data?.content || [])
            }

        } catch (err) {
            setError(err.message || 'Failed to load dashboard data')
        } finally {
            setLoading(false)
        }
    }

    const handleLogout = async () => {
        try {
            await fetchWithAuth('/api/auth/logout', { method: 'POST' })
            window.location.href = '/login'
        } catch (err) {
            window.location.href = '/login'
        }
    }

    const handleViewAccountDetails = (account) => {
        window.location.href = `/account/${account.accountNumber}/details`
    }

    const handleAccountCreated = () => {
        fetchAllData()
        setShowAccountModal(false)
    }

    const handleTransferCompleted = () => {
        fetchAllData()
        setShowTransferModal(false)
    }

    const handleViewStatements = () => {
        if (customer?.publicId) {
            const year = new Date().getFullYear()
            const month = new Date().getMonth() + 1
            window.location.href = `/statement?customerId=${customer.publicId}&year=${year}&month=${month}`
        } else {
            alert('Customer information not available')
        }
    }

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
                <div className="alert alert-danger">
                    <h4>Error Loading Dashboard</h4>
                    <p>{error}</p>
                    <button className="btn btn-primary" onClick={fetchAllData}>
                        Retry
                    </button>
                </div>
            </div>
        )
    }

    const totalBalance = accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0)
    const activeAccounts = accounts.filter(acc => acc.status === 'ACTIVE').length
    const customerId = customer.publicId
    console.log('Render - -User IDL ', user?.id);
    console.log('Render - Accounts:', accounts);
    console.log('Render - Final customerId:', customer.publicId);


    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={handleLogout} />

            <div className="container mt-4">
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

                    {/* Create Account */}
                    <div className="col-md-3 mb-4">
                        <div
                            className="card border-0 shadow-sm text-center h-100"
                            style={{ borderRadius: '12px', cursor: 'pointer', transition: 'transform 0.2s' }}
                            onClick={() => setShowAccountModal(true)}
                            onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
                            onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                        >
                            <div className="card-body py-4">
                                <i className="fas fa-plus text-primary mb-3" style={{ fontSize: '2rem' }} />
                                <h6 className="fw-bold">New Account</h6>
                                <p className="text-muted small mb-0">Open a new account</p>
                            </div>
                        </div>
                    </div>

                    {/* Transfer Money */}
                    <div className="col-md-3 mb-4">
                        <div
                            className="card border-0 shadow-sm text-center h-100"
                            style={{ borderRadius: '12px', cursor: 'pointer', transition: 'transform 0.2s' }}
                            onClick={() => setShowTransferModal(true)}
                            onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
                            onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                        >
                            <div className="card-body py-4">
                                <i className="fas fa-exchange-alt text-primary mb-3" style={{ fontSize: '2rem' }} />
                                <h6 className="fw-bold">Transfer Money</h6>
                                <p className="text-muted small mb-0">Send money to another account</p>
                            </div>
                        </div>
                    </div>

                    {/* View Profile */}
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

                    {/* View Statements */}
                    <div className="col-md-3 mb-4">
                        <div
                            className="card border-0 shadow-sm text-center h-100"
                            style={{ borderRadius: '12px', cursor: 'pointer', transition: 'transform 0.2s' }}
                            onClick={handleViewStatements}
                            onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
                            onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                        >
                            <div className="card-body py-4">
                                <i className="fas fa-file-invoice-dollar text-primary mb-3" style={{ fontSize: '2rem' }} />
                                <h6 className="fw-bold">Statements</h6>
                                <p className="text-muted small mb-0">Monthly statements</p>
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
                                <p className="text-muted mb-4">Get started by opening your first account</p>
                                <button
                                    className="btn btn-primary rounded-pill px-4"
                                    onClick={() => setShowAccountModal(true)}
                                >
                                    <i className="fas fa-plus me-2" />
                                    Open New Account
                                </button>
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

            {/* Modals */}
            <AccountCreationModal
                show={showAccountModal}
                onHide={() => setShowAccountModal(false)}
                onAccountCreated={handleAccountCreated}
                customerPublicId={customer?.publicId}
            />

            <MoneyTransferModal
                show={showTransferModal}
                onHide={() => setShowTransferModal(false)}
                onTransferCompleted={handleTransferCompleted}
                userAccounts={accounts}
            />
        </div>
    )
}

export default Dashboard