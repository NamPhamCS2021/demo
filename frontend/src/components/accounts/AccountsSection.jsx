import React from 'react';
import AccountCard from './AccountCard';
import LoadingSpinner from '../ui/LoadingSpinner';

const AccountsSection = ({accounts, loading, error, onRefresh, onViewAccountDetails}) => {
    if (loading) {
        return <LoadingSpinner text="Loading your accounts..."/>;
    }

    if (error) {
        return (
            <div className="text-center py-5">
                <div className="mb-4">
                    <i className="fas fa-exclamation-triangle text-warning" style={{fontSize: '4rem'}}/>
                </div>
                <h4 className="text-dark mb-2">Unable to Load Accounts</h4>
                <p className="text-muted mb-4">{error}</p>
                <button className="btn btn-primary rounded-pill px-4" onClick={onRefresh}>
                    <i className="fas fa-retry me-2"/>
                    Try Again
                </button>
            </div>
        );
    }

    return (
        <div className="mb-5">
            {/* Section Header */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 className="fw-bold text-dark mb-1 d-flex align-items-center">
                        <i className="fas fa-credit-card me-3 text-primary"/>
                        My Accounts
                    </h3>
                    <p className="text-muted mb-0">
                        {accounts?.length || 0} account{accounts?.length !== 1 ? 's' : ''} available
                    </p>
                </div>
                <button className="btn btn-outline-primary rounded-pill px-4" onClick={onRefresh}>
                    <i className="fas fa-sync-alt me-2"/>
                    Refresh
                </button>
            </div>

            {/* Accounts Grid */}
            {accounts && accounts.length > 0 ? (
                <div className="row">
                    {accounts.map((account, index) => (
                        <AccountCard
                            key={account.id || account.accountNumber || index}
                            account={account}
                            onViewDetails={onViewAccountDetails}
                        />
                    ))}
                </div>
            ) : (
                <div className="text-center py-5">
                    <div className="mb-4">
                        <i className="fas fa-wallet text-muted" style={{fontSize: '4rem'}}/>
                    </div>
                    <h4 className="text-dark mb-2">No Accounts Found</h4>
                    <p className="text-muted mb-4">You don't have any accounts set up yet.</p>
                    <button className="btn btn-primary rounded-pill px-4">
                        <i className="fas fa-plus me-2"/>
                        Open New Account
                    </button>
                </div>
            )}
        </div>
    );
};

export default AccountsSection;
