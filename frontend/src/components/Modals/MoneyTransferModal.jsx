// ===== src/components/modals/MoneyTransferModal.jsx =====
import React, { useState } from 'react'
import { apiService } from '../../services/api'
import { formatCurrency } from '../../utils/formatters'
import LoadingSpinner from '../ui/LoadingSpinner'

const MoneyTransferModal = ({ show, onHide, onTransferCompleted, userAccounts }) => {
    const [currentStep, setCurrentStep] = useState(1)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)

    // Form data
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
            const result = await apiService.getReceiver(accountNumber)
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
        const value = e.target.value.replace(/\D/g, '') // Only digits
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

    const validateStep1 = () => selectedAccount !== null
    const validateStep2 = () => recipientAccount !== null && !recipientError && recipientAccountNumber.length >= 10
    const validateStep3 = () => {
        const amountNum = parseFloat(amount)
        return amountNum > 0 &&
            amountNum <= selectedAccount?.balance &&
            amountNum <= selectedAccount?.accountLimit &&
            location.trim().length > 0
    }

    const handleTransfer = async () => {
        if (!validateStep3()) {
            setError('Please check all required fields')
            return
        }

        setLoading(true)
        setError('')

        try {
            const transferData = {
                accountNumber: selectedAccount.accountNumber,
                receiverAccountNumber: recipientAccount.accountNumber,
                amount: parseFloat(amount),
                location: location
            }

            const result = await apiService.transferByAccountNumber(transferData)
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
                <div className="modal-content position-relative">
                    {loading && (
                        <div className="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-white bg-opacity-75" style={{ zIndex: 1000 }}>
                            <LoadingSpinner text="Processing your transfer..." />
                        </div>
                    )}

                    <div className="modal-header">
                        <h5 className="modal-title">
                            <i className="fas fa-exchange-alt me-2" />
                            {success ? 'Transfer Complete!' : 'Transfer Money'}
                        </h5>
                        <button type="button" className="btn-close" onClick={handleClose} disabled={loading} />
                    </div>

                    <div className="modal-body">
                        {!success && (
                            <div className="row mb-4">
                                <div className="col-12">
                                    <div className="d-flex justify-content-between align-items-center">
                                        {[1, 2, 3].map(step => (
                                            <React.Fragment key={step}>
                                                <div className="d-flex align-items-center">
                                                    <div
                                                        className={`rounded-circle d-flex align-items-center justify-content-center fw-bold ${
                                                            currentStep >= step ? 'bg-primary text-white' : 'bg-secondary text-white'
                                                        }`}
                                                        style={{ width: '30px', height: '30px' }}
                                                    >
                                                        {currentStep > step ? '✓' : step}
                                                    </div>
                                                    <small className="ms-2">
                                                        {step === 1 ? 'From Account' : step === 2 ? 'To Account' : 'Amount & Details'}
                                                    </small>
                                                </div>
                                                {step < 3 && (
                                                    <div className="flex-grow-1 mx-3">
                                                        <hr className={currentStep > step ? 'text-primary' : 'text-muted'} />
                                                    </div>
                                                )}
                                            </React.Fragment>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        )}

                        {error && (
                            <div className="alert alert-danger">
                                <i className="fas fa-exclamation-triangle me-2" />
                                {error}
                            </div>
                        )}

                        {/* Step 1: Select Source Account */}
                        {currentStep === 1 && !success && (
                            <div>
                                <h6 className="mb-3">
                                    <i className="fas fa-credit-card me-2" />
                                    Select source account:
                                </h6>
                                {activeAccounts.length > 0 ? (
                                    <div className="row">
                                        {activeAccounts.map(account => (
                                            <div key={account.accountNumber} className="col-md-6 mb-3">
                                                <div
                                                    className={`card p-3 border-2 ${
                                                        selectedAccount?.accountNumber === account.accountNumber
                                                            ? 'border-primary bg-light'
                                                            : 'border-light'
                                                    }`}
                                                    style={{ cursor: 'pointer', transition: 'all 0.2s' }}
                                                    onClick={() => setSelectedAccount(account)}
                                                >
                                                    <div className="d-flex justify-content-between align-items-center">
                                                        <div>
                                                            <h6 className="mb-1">****{account.accountNumber?.slice(-4)}</h6>
                                                            <p className="text-muted mb-0 small">
                                                                Available: {formatCurrency(account.balance)}
                                                            </p>
                                                            <p className="text-muted mb-0 small">
                                                                Limit: {formatCurrency(account.accountLimit)}
                                                            </p>
                                                        </div>
                                                        <div>
                                                            {selectedAccount?.accountNumber === account.accountNumber && (
                                                                <i className="fas fa-check-circle text-primary" style={{ fontSize: '1.5rem' }} />
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="text-center py-4">
                                        <i className="fas fa-exclamation-circle text-warning" style={{ fontSize: '2rem' }} />
                                        <p className="mt-2 text-muted">No active accounts available for transfers</p>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Step 2: Enter Recipient */}
                        {currentStep === 2 && !success && (
                            <div>
                                <h6 className="mb-3">
                                    <i className="fas fa-user me-2" />
                                    Enter recipient's account:
                                </h6>
                                <div className="mb-3">
                                    <label className="form-label">Recipient Account Number</label>
                                    <div className="position-relative">
                                        <input
                                            type="text"
                                            className={`form-control ${recipientError ? 'is-invalid' : recipientAccount ? 'is-valid' : ''}`}
                                            value={recipientAccountNumber}
                                            onChange={handleRecipientAccountChange}
                                            placeholder="Enter account number"
                                            maxLength="20"
                                        />
                                        {recipientLoading && (
                                            <div className="position-absolute top-50 end-0 translate-middle-y me-3">
                                                <div className="spinner-border spinner-border-sm" role="status" />
                                            </div>
                                        )}
                                        {recipientError && (
                                            <div className="invalid-feedback">{recipientError}</div>
                                        )}
                                    </div>
                                </div>
                                {recipientAccount && (
                                    <div className="alert alert-success">
                                        <h6 className="alert-heading">
                                            <i className="fas fa-check-circle me-2" />
                                            Account Verified
                                        </h6>
                                        <p className="mb-0">
                                            <strong>Account:</strong> ****{recipientAccount.accountNumber?.slice(-4)}<br />
                                            <strong>Name:</strong> {recipientAccount.firstName} {recipientAccount.lastName}<br />
                                            <strong>Status:</strong> <span className="badge bg-success">{recipientAccount.status}</span>
                                        </p>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Step 3: Amount and Details */}
                        {currentStep === 3 && !success && (
                            <div>
                                <h6 className="mb-3">
                                    <i className="fas fa-dollar-sign me-2" />
                                    Transfer details:
                                </h6>
                                <div className="row mb-3">
                                    <div className="col-md-6">
                                        <div className="card bg-light">
                                            <div className="card-body p-3">
                                                <h6 className="card-title mb-2">From</h6>
                                                <p className="mb-1">****{selectedAccount?.accountNumber?.slice(-4)}</p>
                                                <small className="text-muted">Balance: {formatCurrency(selectedAccount?.balance)}</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-6">
                                        <div className="card bg-light">
                                            <div className="card-body p-3">
                                                <h6 className="card-title mb-2">To</h6>
                                                <p className="mb-1">****{recipientAccount?.accountNumber?.slice(-4)}</p>
                                                <small className="text-muted">
                                                    {recipientAccount?.firstName} {recipientAccount?.lastName}
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label className="form-label">Amount to Transfer</label>
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
                                    <div className="form-text">
                                        Maximum: {formatCurrency(Math.min(selectedAccount?.balance || 0, selectedAccount?.accountLimit || 0))}
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label className="form-label">Transfer Location</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={location}
                                        onChange={(e) => setLocation(e.target.value)}
                                        placeholder="Enter location"
                                    />
                                </div>
                            </div>
                        )}

                        {/* Success Step */}
                        {success && (
                            <div className="text-center">
                                <div style={{ animation: 'bounceIn 0.5s ease-in-out' }}>
                                    <i className="fas fa-check-circle text-success" style={{ fontSize: '4rem' }} />
                                </div>
                                <h4 className="text-success mt-3">Transfer Successful!</h4>
                                <div className="mt-4">
                                    <div className="card bg-light">
                                        <div className="card-body">
                                            <h6 className="card-title">Transaction Details</h6>
                                            <div className="row text-start">
                                                <div className="col-6">
                                                    <p className="mb-2">
                                                        <strong>From:</strong><br />
                                                        ****{selectedAccount?.accountNumber?.slice(-4)}
                                                    </p>
                                                    <p className="mb-2">
                                                        <strong>To:</strong><br />
                                                        ****{recipientAccount?.accountNumber?.slice(-4)}
                                                    </p>
                                                </div>
                                                <div className="col-6">
                                                    <p className="mb-2">
                                                        <strong>Amount:</strong><br />
                                                        {formatCurrency(parseFloat(amount))}
                                                    </p>
                                                    <p className="mb-2">
                                                        <strong>Location:</strong><br />
                                                        {location}
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <p className="text-muted mt-3">
                                    Your transfer has been processed successfully!
                                </p>
                            </div>
                        )}
                    </div>

                    <div className="modal-footer">
                        {!success ? (
                            <div className="d-flex justify-content-between w-100">
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
                                    {loading ? (
                                        <span>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" />
                                            Processing...
                                        </span>
                                    ) : currentStep === 3 ? 'Complete Transfer' : 'Next'}
                                </button>
                            </div>
                        ) : (
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
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default MoneyTransferModal