// MoneyTransferModal.js
(()=>{
    const { useState } = React;

    const MoneyTransferModal = ({ show, onHide, onTransferCompleted, userAccounts }) => {
        const [currentStep, setCurrentStep] = useState(1);
        const [loading, setLoading] = useState(false);
        const [error, setError] = useState('');
        const [success, setSuccess] = useState(false);
        const [transactionData, setTransactionData] = useState(null);

        // Form data
        const [selectedAccount, setSelectedAccount] = useState(null);
        const [recipientAccountNumber, setRecipientAccountNumber] = useState('');
        const [amount, setAmount] = useState('');
        const [location, setLocation] = useState('Online Banking');
        const [recipientAccount, setRecipientAccount] = useState(null);

        // Validation states
        const [recipientLoading, setRecipientLoading] = useState(false);
        const [recipientError, setRecipientError] = useState('');

        const resetForm = () => {
            setCurrentStep(1);
            setError('');
            setSuccess(false);
            setTransactionData(null);
            setSelectedAccount(null);
            setRecipientAccountNumber('');
            setAmount('');
            setLocation('Online Banking');
            setRecipientAccount(null);
            setRecipientError('');
        };

        const handleClose = () => {
            resetForm();
            onHide();
        };

        const formatCurrency = (amount) => {
            return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD'
            }).format(amount || 0);
        };

        const validateRecipientAccount = async (accountNumber) => {
            if (!accountNumber || accountNumber.length < 10) {
                setRecipientError('Account number must be at least 10 digits');
                setRecipientAccount(null);
                return false;
            }

            if (accountNumber === selectedAccount?.accountNumber) {
                setRecipientError('Cannot transfer to the same account');
                setRecipientAccount(null);
                return false;
            }

            setRecipientLoading(true);
            setRecipientError('');

            try {
                const response = await fetch(`/api/accounts/byAccountNumber/receiver/${accountNumber}`, {
                    credentials: 'include'
                });

                if (response.ok) {
                    const result = await response.json();
                    if (result.resultCode === '00') {
                        setRecipientAccount(result.data);
                        return true;
                    } else {
                        setRecipientError('Account not found or invalid');
                        setRecipientAccount(null);
                        return false;
                    }
                } else {
                    setRecipientError('Account not found');
                    setRecipientAccount(null);
                    return false;
                }
            } catch (error) {
                setRecipientError('Error validating account');
                setRecipientAccount(null);
                return false;
            } finally {
                setRecipientLoading(false);
            }
        };

        const handleRecipientAccountChange = (e) => {
            const value = e.target.value.replace(/\D/g, ''); // Only digits
            setRecipientAccountNumber(value);

            if (value.length >= 10) {
                validateRecipientAccount(value);
            } else {
                setRecipientAccount(null);
                setRecipientError('');
            }
        };

        const handleAmountChange = (e) => {
            const value = e.target.value;
            // Allow only numbers with up to 2 decimal places
            if (/^\d*\.?\d{0,2}$/.test(value)) {
                setAmount(value);
            }
        };

        const validateStep1 = () => {
            return selectedAccount !== null;
        };

        const validateStep2 = () => {
            return recipientAccount !== null && !recipientError && recipientAccountNumber.length >= 10;
        };

        const validateStep3 = () => {
            const amountNum = parseFloat(amount);
            return amountNum > 0 &&
                amountNum <= selectedAccount?.balance &&
                amountNum <= selectedAccount?.accountLimit &&
                location.trim().length > 0;
        };

        const handleTransfer = async () => {
            if (!validateStep3()) {
                setError('Please check all required fields');
                return;
            }

            setLoading(true);
            setError('');

            try {
                const transferData = {
                    accountNumber: selectedAccount.accountNumber,
                    receiverAccountNumber: recipientAccount.accountNumber,
                    amount: parseFloat(amount),
                    location: location
                };

                const response = await fetch('/api/transactions/transferByAccountNumber', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: JSON.stringify(transferData)
                });

                const result = await response.json();

                if (response.ok && result.resultCode === '00') {
                    setTransactionData(result.data);
                    setSuccess(true);
                    setCurrentStep(4);

                    setTimeout(() => {
                        if (onTransferCompleted) {
                            onTransferCompleted(result.data);
                        }
                    }, 2000);
                } else {
                    setError(result.resultMessage || 'Transfer failed');
                }
            } catch (error) {
                console.error('Transfer error:', error);
                setError(`Network error: ${error.message}`);
            } finally {
                setLoading(false);
            }
        };

        const nextStep = () => {
            if (currentStep === 1 && validateStep1()) {
                setCurrentStep(2);
                } else if (currentStep === 2 && validateStep2()) {
                setCurrentStep(3);
            } else if (currentStep === 3 && validateStep3()) {
                handleTransfer();
            }
        };

        const prevStep = () => {
            if (currentStep > 1 && !loading) {
                setCurrentStep(currentStep - 1);
            }
        };

        if (!show) return null;

        return React.createElement('div', {
                className: 'modal fade show d-block',
                style: { backgroundColor: 'rgba(0,0,0,0.5)' }
            },
            React.createElement('div', { className: 'modal-dialog modal-dialog-centered modal-lg' },
                React.createElement('div', { className: 'modal-content position-relative' },
                    loading ? React.createElement('div', { className: 'loading-overlay' },
                        React.createElement('div', { className: 'text-center' },
                            React.createElement('div', { className: 'spinner' }),
                            React.createElement('p', { className: 'mt-3' }, 'Processing your transfer...')
                        )
                    ) : null,

                    React.createElement('div', { className: 'modal-header' },
                        React.createElement('h5', { className: 'modal-title' },
                            React.createElement('i', { className: 'fas fa-exchange-alt me-2' }),
                            success ? 'Transfer Completed!' : 'Transfer Money'
                        ),
                        React.createElement('button', {
                            type: 'button',
                            className: 'btn-close',
                            onClick: handleClose,
                            disabled: loading
                        })
                    ),

                    React.createElement('div', { className: 'modal-body' },
                        !success ? React.createElement('div', { className: 'row mb-4' },
                            React.createElement('div', { className: 'col-12' },
                                React.createElement('div', { className: 'd-flex justify-content-between align-items-center' },
                                    React.createElement('div', { className: 'd-flex align-items-center' },
                                        React.createElement('div', {
                                            className: `step-indicator ${currentStep >= 1 ? 'active' : ''} ${currentStep > 1 ? 'completed' : ''}`
                                        }, currentStep > 1 ? React.createElement('i', { className: 'fas fa-check' }) : '1'),
                                        React.createElement('small', { className: 'ms-2' }, 'From Account')
                                    ),
                                    React.createElement('div', { className: 'flex-grow-1 mx-3' },
                                        React.createElement('hr', { className: currentStep >= 2 ? 'text-primary' : 'text-muted' })
                                    ),
                                    React.createElement('div', { className: 'd-flex align-items-center' },
                                        React.createElement('div', {
                                            className: `step-indicator ${currentStep >= 2 ? 'active' : ''} ${currentStep > 2 ? 'completed' : ''}`
                                        }, currentStep > 2 ? React.createElement('i', { className: 'fas fa-check' }) : '2'),
                                        React.createElement('small', { className: 'ms-2' }, 'To Account')
                                    ),
                                    React.createElement('div', { className: 'flex-grow-1 mx-3' },
                                        React.createElement('hr', { className: currentStep >= 3 ? 'text-primary' : 'text-muted' })
                                    ),
                                    React.createElement('div', { className: 'd-flex align-items-center' },
                                        React.createElement('div', {
                                            className: `step-indicator ${currentStep >= 3 ? 'active' : ''}`
                                        }, '3'),
                                        React.createElement('small', { className: 'ms-2' }, 'Amount & Details')
                                    )
                                )
                            )
                        ) : null,

                        error ? React.createElement('div', { className: 'alert alert-danger' },
                            React.createElement('i', { className: 'fas fa-exclamation-triangle me-2' }),
                            error
                        ) : null,

                        // Step 1: Select Source Account
                        currentStep === 1 && !success ? React.createElement('div', { className: 'transfer-step active' },
                            React.createElement('h6', { className: 'mb-3' },
                                React.createElement('i', { className: 'fas fa-credit-card me-2' }),
                                'Select source account:'
                            ),
                            React.createElement('div', { className: 'row' },
                                userAccounts?.filter(acc => acc.status === 'ACTIVE').map(account =>
                                    React.createElement('div', { key: account.accountNumber, className: 'col-md-6 mb-3' },
                                        React.createElement('div', {
                                                className: `account-select-card card p-3 ${selectedAccount?.accountNumber === account.accountNumber ? 'selected' : ''}`,
                                                onClick: () => setSelectedAccount(account)
                                            },
                                            React.createElement('div', { className: 'd-flex justify-content-between align-items-center' },
                                                React.createElement('div', null,
                                                    React.createElement('h6', { className: 'mb-1' }, `****${account.accountNumber?.slice(-4)}`),
                                                    React.createElement('p', { className: 'text-muted mb-0 small' }, `Available: ${formatCurrency(account.balance)}`),
                                                    React.createElement('p', { className: 'text-muted mb-0 small' }, `Limit: ${formatCurrency(account.accountLimit)}`)
                                                ),
                                                React.createElement('div', null,
                                                    selectedAccount?.accountNumber === account.accountNumber ? React.createElement('i', { className: 'fas fa-check-circle text-primary' }) : null
                                                )
                                            )
                                        )
                                    )
                                )
                            ),
                            (!userAccounts || userAccounts.filter(acc => acc.status === 'ACTIVE').length === 0) ? React.createElement('div', { className: 'text-center py-4' },
                                React.createElement('i', { className: 'fas fa-exclamation-circle text-warning', style: {fontSize: '2rem'} }),
                                React.createElement('p', { className: 'mt-2 text-muted' }, 'No active accounts available for transfers')
                            ) : null
                        ) : null,

                        // Step 2: Enter Recipient
                        currentStep === 2 && !success ? React.createElement('div', { className: 'transfer-step active' },
                            React.createElement('h6', { className: 'mb-3' },
                                React.createElement('i', { className: 'fas fa-user me-2' }),
                                'Enter recipient\'s account:'
                            ),
                            React.createElement('div', { className: 'mb-3' },
                                React.createElement('label', { className: 'form-label' }, 'Recipient Account Number'),
                                React.createElement('div', { className: 'position-relative' },
                                    React.createElement('input', {
                                        type: 'text',
                                        className: `form-control ${recipientError ? 'is-invalid' : recipientAccount ? 'is-valid' : ''}`,
                                        value: recipientAccountNumber,
                                        onChange: handleRecipientAccountChange,
                                        placeholder: 'Enter account number',
                                        maxLength: '20'
                                    }),
                                    recipientLoading ? React.createElement('div', { className: 'position-absolute top-50 end-0 translate-middle-y me-3' },
                                        React.createElement('div', { className: 'spinner-border spinner-border-sm', role: 'status' })
                                    ) : null,
                                    recipientError ? React.createElement('div', { className: 'invalid-feedback' }, recipientError) : null
                                )
                            ),
                            recipientAccount ? React.createElement('div', { className: 'alert alert-success' },
                                React.createElement('h6', { className: 'alert-heading' },
                                    React.createElement('i', { className: 'fas fa-check-circle me-2' }),
                                    'Account Verified'
                                ),
                                React.createElement('p', { className: 'mb-0' },
                                    React.createElement('strong', null, 'Account: '),
                                    `****${recipientAccount.accountNumber?.slice(-4)} `,
                                    React.createElement('br'),
                                    React.createElement('strong', null, 'Status: '),
                                    React.createElement('span', { className: 'badge bg-success' }, recipientAccount.status)
                                )
                            ) : null
                        ) : null,

                        // Step 3: Amount and Details
                        currentStep === 3 && !success ? React.createElement('div', { className: 'transfer-step active' },
                            React.createElement('h6', { className: 'mb-3' },
                                React.createElement('i', { className: 'fas fa-dollar-sign me-2' }),
                                'Transfer details:'
                            ),
                            React.createElement('div', { className: 'row mb-3' },
                                React.createElement('div', { className: 'col-md-6' },
                                    React.createElement('div', { className: 'card bg-light' },
                                        React.createElement('div', { className: 'card-body p-3' },
                                            React.createElement('h6', { className: 'card-title mb-2' }, 'From'),
                                            React.createElement('p', { className: 'mb-1' }, `****${selectedAccount?.accountNumber?.slice(-4)}`),
                                            React.createElement('small', { className: 'text-muted' }, `Balance: ${formatCurrency(selectedAccount?.balance)}`)
                                        )
                                    )
                                ),
                                React.createElement('div', { className: 'col-md-6' },
                                    React.createElement('div', { className: 'card bg-light' },
                                        React.createElement('div', { className: 'card-body p-3' },
                                            React.createElement('h6', { className: 'card-title mb-2' }, 'To'),
                                            React.createElement('p', { className: 'mb-1' }, `****${recipientAccount?.accountNumber?.slice(-4)}`),
                                            React.createElement('small', { className: 'text-muted' }, `Status: ${recipientAccount?.status}`)
                                        )
                                    )
                                )
                            ),
                            React.createElement('div', { className: 'mb-3' },
                                React.createElement('label', { className: 'form-label' }, 'Amount to Transfer'),
                                React.createElement('div', { className: 'input-group' },
                                    React.createElement('span', { className: 'input-group-text' }, '$'),
                                    React.createElement('input', {
                                        type: 'text',
                                        className: 'form-control',
                                        value: amount,
                                        onChange: handleAmountChange,
                                        placeholder: '0.00'
                                    })
                                ),
                                React.createElement('div', { className: 'form-text' },
                                    `Maximum: ${formatCurrency(Math.min(selectedAccount?.balance || 0, selectedAccount?.accountLimit || 0))}`
                                )
                            ),
                            React.createElement('div', { className: 'mb-3' },
                                React.createElement('label', { className: 'form-label' }, 'Transfer Location'),
                                React.createElement('input', {
                                    type: 'text',
                                    className: 'form-control',
                                    value: location,
                                    onChange: (e) => setLocation(e.target.value),
                                    placeholder: 'Enter location'
                                })
                            )
                        ) : null,

                        // Success Step
                        success ? React.createElement('div', { className: 'success-animation text-center' },
                            React.createElement('i', { className: 'fas fa-check-circle text-success', style: {fontSize: '4rem'} }),
                            React.createElement('h4', { className: 'text-success mt-3' }, 'Transfer Successful!'),
                            transactionData ? React.createElement('div', { className: 'mt-4' },
                                React.createElement('div', { className: 'card bg-light' },
                                    React.createElement('div', { className: 'card-body' },
                                        React.createElement('h6', { className: 'card-title' }, 'Transaction Details'),
                                        React.createElement('div', { className: 'row text-start' },
                                            React.createElement('div', { className: 'col-6' },
                                                React.createElement('p', { className: 'mb-2' },
                                                    React.createElement('strong', null, 'From:'),
                                                    React.createElement('br'),
                                                    `****${selectedAccount?.accountNumber?.slice(-4)}`
                                                ),
                                                React.createElement('p', { className: 'mb-2' },
                                                    React.createElement('strong', null, 'To:'),
                                                    React.createElement('br'),
                                                    `****${recipientAccount?.accountNumber?.slice(-4)}`
                                                )
                                            ),
                                            React.createElement('div', { className: 'col-6' },
                                                React.createElement('p', { className: 'mb-2' },
                                                    React.createElement('strong', null, 'Amount:'),
                                                    React.createElement('br'),
                                                    formatCurrency(parseFloat(amount))
                                                ),
                                                React.createElement('p', { className: 'mb-2' },
                                                    React.createElement('strong', null, 'Location:'),
                                                    React.createElement('br'),
                                                    location
                                                )
                                            )
                                        )
                                    )
                                )
                            ) : null,
                            React.createElement('p', { className: 'text-muted mt-3' }, 'Your transfer has been processed successfully!')
                        ) : null
                    ),

                    React.createElement('div', { className: 'modal-footer' },
                        !success ? React.createElement('div', { className: 'd-flex justify-content-between w-100' },
                            React.createElement('button', {
                                type: 'button',
                                className: 'btn btn-secondary',
                                onClick: currentStep === 1 ? handleClose : prevStep,
                                disabled: loading
                            }, currentStep === 1 ? 'Cancel' : 'Back'),
                            React.createElement('button', {
                                type: 'button',
                                className: 'btn btn-primary',
                                onClick: nextStep,
                                disabled: loading ||
                                    (currentStep === 1 && !validateStep1()) ||
                                    (currentStep === 2 && !validateStep2()) ||
                                    (currentStep === 3 && !validateStep3())
                            }, loading ? React.createElement('span', null,
                                React.createElement('span', { className: 'spinner-border spinner-border-sm me-2', role: 'status' }),
                                'Processing...'
                            ) : currentStep === 3 ? 'Complete Transfer' : 'Next')
                        ) : React.createElement('div', { className: 'd-flex justify-content-between w-100' },
                            React.createElement('button', {
                                    type: 'button',
                                    className: 'btn btn-outline-primary',
                                    onClick: () => window.location.reload()
                                },
                                React.createElement('i', { className: 'fas fa-tachometer-alt me-2' }),
                                'Refresh Dashboard'
                            ),
                            React.createElement('button', {
                                    type: 'button',
                                    className: 'btn btn-primary',
                                    onClick: handleClose
                                },
                                React.createElement('i', { className: 'fas fa-times me-2' }),
                                'Close'
                            )
                        )
                    )
                )
            )
        );
    };
    // Make it globally available
    window.MoneyTransferModal = MoneyTransferModal;
})();



