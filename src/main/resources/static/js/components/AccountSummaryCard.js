// AccountSummaryCard.js
const AccountSummaryCard = ({ account }) => {
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount || 0);
    };

    const getStatusColor = (status) => {
        switch(status?.toLowerCase()) {
            case 'active': return 'account-status-active';
            case 'inactive': return 'account-status-inactive';
            case 'suspended': return 'account-status-suspended';
            default: return 'text-muted';
        }
    };

    return React.createElement('div', { className: 'col-md-6 col-lg-4 mb-4' },
        React.createElement('div', { className: 'dashboard-card card h-100' },
            React.createElement('div', { className: 'card-body' },
                React.createElement('div', { className: 'd-flex justify-content-between align-items-center mb-3' },
                    React.createElement('h5', { className: 'card-title mb-0' },
                        React.createElement('i', { className: 'fas fa-credit-card me-2' }),
                        'Account'
                    ),
                    React.createElement('span', {
                        className: `text-uppercase fw-bold ${getStatusColor(account.status)}`
                    }, account.status)
                ),
                React.createElement('h3', { className: 'card-text fw-bold' }, formatCurrency(account.balance)),
                React.createElement('p', { className: 'text-muted mb-2' }, `****${account.accountNumber?.slice(-4) || '0000'}`),
                React.createElement('p', { className: 'text-muted mb-4' }, `Limit: ${formatCurrency(account.accountLimit)}`),
                React.createElement('button', {
                    className: 'btn btn-primary btn-sm',
                    onClick: () => {
                        if (account.accountNumber) {
                            window.location.href = `/account/${account.accountNumber}/details`;
                        } else {
                            console.error('Account number is missing, cannot navigate to details page.');
                            alert('Account details not available. Please try again later.');
                        }
                    }
                }, 'View Details')
            )
        )
    );
};

// Make it globally available
window.AccountSummaryCard = AccountSummaryCard;