// ===== src/components/ui/TransactionCard.jsx =====
import React from 'react'
import { formatCurrency, formatDateTime } from '../../utils/formatters'

const TransactionCard = ({ transaction }) => {
    const getTransactionIcon = (type) => {
        switch(type?.toLowerCase()) {
            case 'deposit': return 'fas fa-arrow-down text-success'
            case 'withdrawal': return 'fas fa-arrow-up text-danger'
            case 'transfer': return 'fas fa-exchange-alt text-primary'
            default: return 'fas fa-money-bill'
        }
    }

    const isPositive = transaction.type?.toLowerCase() === 'deposit' ||
        (transaction.type?.toLowerCase() === 'transfer' && transaction.receiverId)
    const amountClass = isPositive ? 'text-success' : 'text-danger'
    const amountPrefix = isPositive ? '+' : '-'

    return (
        <div className="col-md-6 col-lg-4 mb-3">
            <div
                className="card h-100 shadow-sm"
                style={{
                    borderRadius: '8px',
                    transition: 'transform 0.2s, box-shadow 0.2s'
                }}
                onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-2px)'
                    e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)'
                }}
                onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)'
                    e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.12)'
                }}
            >
                <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            <h6 className="mb-1">
                                <i className={`${getTransactionIcon(transaction.type)} me-2`} />
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
                        <div className="text-center mb-2">
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

export default TransactionCard