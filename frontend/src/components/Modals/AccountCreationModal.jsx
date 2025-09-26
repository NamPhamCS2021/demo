// ===== src/components/modals/AccountCreationModal.jsx =====
import React, { useState } from 'react'
import { apiService } from '../../services/api'
import { formatCurrency } from '../../utils/formatters'
import LoadingSpinner from '../ui/LoadingSpinner'

const AccountCreationModal = ({ show, onHide, onAccountCreated, customerId }) => {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)
    const [newAccount, setNewAccount] = useState(null)

    const handleCreateAccount = async () => {
        if (!customerId) {
            setError('Customer ID is required to create an account')
            return
        }

        setLoading(true)
        setError('')
        setSuccess(false)

        try {
            const result = await apiService.createAccount(customerId)
            if (result.resultCode === '00') {
                setNewAccount(result.data)
                setSuccess(true)
                setTimeout(() => {
                    if (onAccountCreated) {
                        onAccountCreated(result.data)
                    }
                }, 2000)
            } else {
                setError(result.resultMessage || 'Failed to create account')
            }
        } catch (error) {
            console.error('Account creation error:', error)
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
                <div className="modal-content position-relative">
                    {loading && (
                        <div className="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-white bg-opacity-75" style={{ zIndex: 1000 }}>
                            <LoadingSpinner text="Creating your account..." />
                        </div>
                    )}

                    <div className="modal-header">
                        <h5 className="modal-title">
                            <i className="fas fa-plus-circle me-2" />
                            {success ? 'Account Created Successfully!' : 'Create New Account'}
                        </h5>
                        <button
                            type="button"
                            className="btn-close"
                            onClick={handleClose}
                            disabled={loading}
                        />
                    </div>

                    <div className="modal-body">
                        {success ? (
                            <div className="text-center" style={{ animation: 'bounceIn 0.5s ease-in-out' }}>
                                <i className="fas fa-check-circle text-success" style={{ fontSize: '4rem' }} />
                                <h4 className="text-success mt-3">Account Created!</h4>
                                {newAccount && (
                                    <div className="mt-4">
                                        <div className="card bg-light">
                                            <div className="card-body">
                                                <h6 className="card-title">Account Details</h6>
                                                <p className="mb-2">
                                                    <strong>Account Number: </strong>
                                                    <code>{newAccount.accountNumber}</code>
                                                </p>
                                                <p className="mb-2">
                                                    <strong>Balance: </strong>
                                                    {formatCurrency(newAccount.balance)}
                                                </p>
                                                <p className="mb-2">
                                                    <strong>Account Limit: </strong>
                                                    {formatCurrency(newAccount.accountLimit)}
                                                </p>
                                                <p className="mb-0">
                                                    <strong>Status: </strong>
                                                    <span className="badge bg-success">{newAccount.status}</span>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                )}
                                <p className="text-muted mt-3">
                                    You can now start using your new account!
                                </p>
                            </div>
                        ) : (
                            <div>
                                <div className="text-center mb-4">
                                    <i className="fas fa-credit-card text-primary" style={{ fontSize: '3rem' }} />
                                    <h5 className="mt-3">Ready to create your new account?</h5>
                                    <p className="text-muted">
                                        A new account will be created instantly with default settings based on your customer type.
                                    </p>
                                </div>

                                <div className="alert alert-info">
                                    <h6>
                                        <i className="fas fa-info-circle me-2" />
                                        Account Features:
                                    </h6>
                                    <ul className="mb-0">
                                        <li>Instant account activation</li>
                                        <li>Automatic account limit based on customer type</li>
                                        <li>Zero initial balance</li>
                                        <li>Secure account number generation</li>
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
                        {success ? (
                            <div className="d-flex justify-content-between w-100">
                                <button
                                    type="button"
                                    className="btn btn-outline-primary"
                                    onClick={() => window.location.reload()}
                                >
                                    <i className="fas fa-tachometer-alt me-2" />
                                    Refresh Dashboard
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={handleClose}
                                >
                                    <i className="fas fa-times me-2" />
                                    Close
                                </button>
                            </div>
                        ) : (
                            <div className="d-flex justify-content-end">
                                <button
                                    type="button"
                                    className="btn btn-secondary me-2"
                                    onClick={handleClose}
                                    disabled={loading}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={handleCreateAccount}
                                    disabled={loading || !customerId}
                                >
                                    {loading ? (
                                        <span>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" />
                                            Creating...
                                        </span>
                                    ) : (
                                        <span>
                                            <i className="fas fa-plus me-2" />
                                            Create Account
                                        </span>
                                    )}
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AccountCreationModal