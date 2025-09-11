// TransactionCard.js
const TransactionCard = ({ transaction }) => {
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount || 0);
    };

    const getAmountClass = (type) => {
        return type?.toLowerCase() === 'deposit' || type?.toLowerCase() === 'transfer'
            ? 'transaction-amount-positive'
            : 'transaction-amount-negative';
    };

    const getTransactionIcon = (type) => {
        switch(type?.toLowerCase()) {
            case 'deposit': return 'fas fa-arrow-down text-success';
            case 'withdrawal': return 'fas fa-arrow-up text-danger';
            case 'transfer': return 'fas fa-exchange-alt text-primary';
            default: return 'fas fa-money-bill';
        }
    };

    const isDepositOrTransfer = transaction.type?.toLowerCase() === 'deposit' || transaction.type?.toLowerCase() === 'transfer';
    const amountDisplay = isDepositOrTransfer ? `+${formatCurrency(transaction.amount)}` : `-${formatCurrency(transaction.amount)}`;

    return React.createElement('div', { className: 'col-md-6 col-lg-4 mb-3' },
        React.createElement('div', { className: 'transaction-card card h-100' },
            React.createElement('div', { className: 'card-body' },
                React.createElement('div', { className: 'd-flex justify-content-between align-items-center' },
                    React.createElement('div', null,
                        React.createElement('h6', { className: 'mb-0' },
                            React.createElement('i', { className: `${getTransactionIcon(transaction.type)} me-2` }),
                            transaction.type
                        ),
                        React.createElement('small', { className: 'text-muted' }, new Date(transaction.createdAt).toLocaleDateString())
                    ),
                    React.createElement('span', { className: `fw-bold ${getAmountClass(transaction.type)}` }, amountDisplay)
                )
            )
        )
    );
};

// Make it globally available
window.TransactionCard = TransactionCard;