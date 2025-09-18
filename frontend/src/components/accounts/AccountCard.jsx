import React from 'react';
import { formatCurrency, maskAccountNumber } from '../../utils/Formatters.js';
import StatusBadge from '../ui/StatusBadge.jsx';

const AccountCard = ({ account, onViewDetails }) => {
    const handleViewDetails = () => {
        onViewDetails(account.id || account.accountId);
    };

    return (
        <div className="col-lg-4 col-md-6 mb-4">
            <div className="card h-100 border-0 shadow-sm account-card"
                 style={{ borderRadius: '16px', transition: 'all 0.3s ease' }}>
                <div className="card-body p-4">
                    {/* Card Header */}
                    <div className="d-flex justify-content-between align-items-start mb-3">
                        <div className="d-flex align-items-center">
                            <div className="bg-primary bg-opacity-10 rounded-circle p-2 me-3">
                                <i className="fas fa-credit-card text-primary" />
                            </div>
                            <div>
                                <h6 className="mb-0 fw-bold text-dark">Account</h6>
                                <small className="text-muted account-number">
                                    {maskAccountNumber(account.accountNumber)}
                                </small>
                            </div>
                        </div>
                        <StatusBadge status={account.status} />
                    </div>

                    {/* Balance */}
                    <div className="text-center my-4">
                        <h2 className="account-balance text-dark mb-1 fw-bold"
                            style={{ fontSize: '2rem', letterSpacing: '-0.5px' }}>
                            {formatCurrency(account.balance)}
                        </h2>
                        <small className="text-muted fw-medium">Current Balance</small>
                    </div>

                    {/* Account Details */}
                    <div className="row text-center mb-4">
                        <div className="col-6">
                            <div className="border-end">
                                <div className="fw-bold text-dark">
                                    {formatCurrency(account.accountLimit)}
                                </div>
                                <small className="text-muted">Credit Limit</small>
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

                    {/* Action Button */}
                    <button className="btn btn-primary w-100 rounded-pill fw-medium"
                            onClick={handleViewDetails}>
                        <i className="fas fa-eye me-2" />
                        View Details & Transactions
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AccountCard;
