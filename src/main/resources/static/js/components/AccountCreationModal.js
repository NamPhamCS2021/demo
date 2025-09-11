// AccountCreationModal.js
const { useState } = React;

const AccountCreationModal = ({ show, onHide, onAccountCreated, customerId }) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [newAccount, setNewAccount] = useState(null);

    const handleCreateAccount = async () => {
        if (!customerId) {
            setError('Customer ID is required to create an account');
            return;
        }

        setLoading(true);
        setError('');
        setSuccess(false);

        try {
            const response = await fetch('/api/accounts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    customerId: customerId
                })
            });

            const result = await response.json();

            if (response.ok && result.resultCode === '00') {
                setNewAccount(result.data);
                setSuccess(true);

                setTimeout(() => {
                    if (onAccountCreated) {
                        onAccountCreated(result.data);
                    }
                }, 2000);
            } else {
                setError(result.resultMessage || 'Failed to create account');
            }
        } catch (error) {
            console.error('Account creation error:', error);
            setError(`Network error: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setError('');
        setSuccess(false);
        setNewAccount(null);
        onHide();
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount || 0);
    };

    if (!show) return null;

    return React.createElement('div', {
            className: 'modal fade show d-block',
            style: { backgroundColor: 'rgba(0,0,0,0.5)' }
        },
        React.createElement('div', { className: 'modal-dialog modal-dialog-centered' },
            React.createElement('div', { className: 'modal-content position-relative' },
                loading ? React.createElement('div', { className: 'loading-overlay' },
                    React.createElement('div', { className: 'text-center' },
                        React.createElement('div', { className: 'spinner' }),
                        React.createElement('p', { className: 'mt-3' }, 'Creating your account...')
                    )
                ) : null,

                React.createElement('div', { className: 'modal-header' },
                    React.createElement('h5', { className: 'modal-title' },
                        React.createElement('i', { className: 'fas fa-plus-circle me-2' }),
                        success ? 'Account Created Successfully!' : 'Create New Account'
                    ),
                    React.createElement('button', {
                        type: 'button',
                        className: 'btn-close',
                        onClick: handleClose,
                        disabled: loading
                    })
                ),

                React.createElement('div', { className: 'modal-body' },
                    success ? (
                        React.createElement('div', { className: 'success-animation text-center' },
                            React.createElement('i', {
                                className: 'fas fa-check-circle text-success',
                                style: { fontSize: '4rem' }
                            }),
                            React.createElement('h4', { className: 'text-success mt-3' }, 'Account Created!'),
                            newAccount ? React.createElement('div', { className: 'mt-4' },
                                React.createElement('div', { className: 'card bg-light' },
                                    React.createElement('div', { className: 'card-body' },
                                        React.createElement('h6', { className: 'card-title' }, 'Account Details'),
                                        React.createElement('p', { className: 'mb-2' },
                                            React.createElement('strong', null, 'Account Number: '),
                                            React.createElement('code', null, newAccount.accountNumber)
                                        ),
                                        React.createElement('p', { className: 'mb-2' },
                                            React.createElement('strong', null, 'Balance: '),
                                            formatCurrency(newAccount.balance)
                                        ),
                                        React.createElement('p', { className: 'mb-2' },
                                            React.createElement('strong', null, 'Account Limit: '),
                                            formatCurrency(newAccount.accountLimit)
                                        ),
                                        React.createElement('p', { className: 'mb-0' },
                                            React.createElement('strong', null, 'Status: '),
                                            React.createElement('span', { className: 'badge bg-success' }, newAccount.status)
                                        )
                                    )
                                )
                            ) : null,
                            React.createElement('p', { className: 'text-muted mt-3' },
                                'You can now start using your new account!'
                            )
                        )
                    ) : (
                        React.createElement('div', null,
                            React.createElement('div', { className: 'text-center mb-4' },
                                React.createElement('i', {
                                    className: 'fas fa-credit-card text-primary',
                                    style: { fontSize: '3rem' }
                                }),
                                React.createElement('h5', { className: 'mt-3' }, 'Ready to create your new account?'),
                                React.createElement('p', { className: 'text-muted' },
                                    'A new account will be created instantly with default settings based on your customer type.'
                                )
                            ),

                            React.createElement('div', { className: 'alert alert-info' },
                                React.createElement('h6', null,
                                    React.createElement('i', { className: 'fas fa-info-circle me-2' }),
                                    'Account Features:'
                                ),
                                React.createElement('ul', { className: 'mb-0' },
                                    React.createElement('li', null, 'Instant account activation'),
                                    React.createElement('li', null, 'Automatic account limit based on customer type'),
                                    React.createElement('li', null, 'Zero initial balance'),
                                    React.createElement('li', null, 'Secure account number generation'),
                                    React.createElement('li', null, 'Full transaction capabilities')
                                )
                            ),

                            error ? React.createElement('div', { className: 'alert alert-danger' },
                                React.createElement('i', { className: 'fas fa-exclamation-triangle me-2' }),
                                error
                            ) : null
                        )
                    )
                ),

                React.createElement('div', { className: 'modal-footer' },
                    success ? (
                        React.createElement('div', { className: 'd-flex justify-content-between w-100' },
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
                    ) : (
                        React.createElement('div', { className: 'd-flex justify-content-end' },
                            React.createElement('button', {
                                type: 'button',
                                className: 'btn btn-secondary me-2',
                                onClick: handleClose,
                                disabled: loading
                            }, 'Cancel'),
                            React.createElement('button', {
                                    type: 'button',
                                    className: 'btn btn-primary',
                                    onClick: handleCreateAccount,
                                    disabled: loading || !customerId
                                },
                                loading ? (
                                    React.createElement('span', null,
                                        React.createElement('span', {
                                            className: 'spinner-border spinner-border-sm me-2',
                                            role: 'status'
                                        }),
                                        'Creating...'
                                    )
                                ) : (
                                    React.createElement('span', null,
                                        React.createElement('i', { className: 'fas fa-plus me-2' }),
                                        'Create Account'
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    );
};

// Make it globally available
window.AccountCreationModal = AccountCreationModal;