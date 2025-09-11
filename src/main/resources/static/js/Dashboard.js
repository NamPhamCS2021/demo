// Dashboard.js - Main dashboard component
console.log(">>> Dashboard.js loaded");
(()=>{
    const { useState, useEffect } = React;

    const Dashboard = () => {
        const [user, setUser] = useState(null);
        const [accounts, setAccounts] = useState([]);
        const [stats, setStats] = useState({
            totalBalance: 0,
            activeAccounts: 0,
            recentTransactions: 0
        });
        const [recentTransactions, setRecentTransactions] = useState([]);
        const [loading, setLoading] = useState(true);
        const [accountsLoading, setAccountsLoading] = useState(true);
        const [error, setError] = useState(null);
        const [showAccountModal, setShowAccountModal] = useState(false);
        const [showTransferModal, setShowTransferModal] = useState(false);

        useEffect(() => {
            checkAuthAndFetchDashboardData();
        }, []);

        const checkAuthAndFetchDashboardData = async () => {
            setLoading(true);
            try {
                const userResponse = await fetch('/api/auth/me', { credentials: 'include' });
                if (!userResponse.ok) {
                    window.location.href = '/login';
                    return;
                }
                const userData = await userResponse.json();

                const profileResponse = await fetch('/api/customers/profile', { credentials: 'include' });
                if (!profileResponse.ok) {
                    throw new Error('Failed to fetch customer profile.');
                }
                const profileData = await profileResponse.json();
                const customerProfile = profileData.data;
                setUser({ ...userData.data, id: customerProfile.id, firstName: customerProfile.firstName });

                await fetchAccountsAndTransactions(customerProfile.id);

            } catch (err) {
                console.error('Authentication check or data fetch failed:', err);
                if (err.message.includes('Authentication') || err.message.includes('login')) {
                    window.location.href = '/login';
                } else {
                    setError('Failed to load dashboard data. Please try again.');
                }
            } finally {
                setLoading(false);
            }
        };

        const fetchAccountsAndTransactions = async (customerId) => {
            setAccountsLoading(true);
            try {
                const accountsResponse = await fetch(`/api/accounts/search/${customerId}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({})
                });

                if (!accountsResponse.ok) {
                    throw new Error('Failed to fetch accounts');
                }
                const accountsData = await accountsResponse.json();

                const fetchedAccounts = accountsData.data?.content || [];
                setAccounts(fetchedAccounts);
                updateStats(fetchedAccounts);

                if (fetchedAccounts.length > 0) {
                    const allTransactions = await Promise.all(
                        fetchedAccounts.map(acc =>
                            fetchRecentTransactions(acc.accountNumber))
                    );

                    const merged = allTransactions.flat().sort(
                        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
                    );
                    setRecentTransactions(merged.slice(0, 5));
                    setStats(prevStats => ({
                        ...prevStats,
                        recentTransactions: merged.length
                    }));
                }

            } catch (err) {
                console.error('Failed to fetch accounts and transactions:', err);
                setError('Failed to load accounts and transactions. Please try again.');
            } finally {
                setAccountsLoading(false);
            }
        };

        const updateStats = (accounts) => {
            const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);
            const activeAccounts = accounts.filter(account => account.status === 'ACTIVE').length;
            setStats(prevStats => ({
                ...prevStats,
                totalBalance: totalBalance,
                activeAccounts: activeAccounts,
            }));
        };

        const fetchRecentTransactions = async (accountId) => {
            try {
                const response = await fetch(`api/transactions/account/accountnumber/${accountId}/search`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({ page: 0, size: 5, sort: 'createdDate', direction: 'DESC' })
                });

                if (response.ok) {
                    const result = await response.json();
                    return result.data.content || [];
                } else {
                    console.error('Failed to fetch transactions:', response.status);
                    return [];
                }
            } catch (error) {
                console.error('Network error fetching transactions:', error);
                return [];
            }
        };

        const handleLogout = async () => {
            try {
                const response = await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                if (response.ok) {
                    window.location.href = '/login';
                } else {
                    alert('Logout failed. Please try again.');
                }
            } catch (error) {
                alert('An error occurred during logout. Please try again.');
            }
        };

        const handleAccountCreated = (newAccount) => {
            console.log('New account created:', newAccount);
            if (user?.id) {
                fetchAccountsAndTransactions(user.id);
            }
            setTimeout(() => {
                setShowAccountModal(false);
            }, 1000);
        };

        const handleTransferCompleted = (transactionData) => {
            console.log('Transfer completed:', transactionData);
            if (user?.id) {
                fetchAccountsAndTransactions(user.id);
            }
            setTimeout(() => {
                setShowTransferModal(false);
            }, 1000);
        };

        // Update navbar with user dropdown
        useEffect(() => {
            if (user) {
                const dropdownContainer = document.getElementById('user-dropdown');
                if (dropdownContainer && window.UserDropdown) {
                    ReactDOM.render(
                        React.createElement(window.UserDropdown, { user, onLogout: handleLogout }),
                        dropdownContainer
                    );
                }
            }
        }, [user]);

        const formatCurrency = (amount) => {
            return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount || 0);
        };

        if (loading) {
            return React.createElement('div', { className: 'text-center' },
                React.createElement('div', { className: 'loading-spinner' }),
                React.createElement('p', null, 'Loading dashboard...')
            );
        }

        if (error) {
            return React.createElement('div', { className: 'text-center alert alert-danger' }, error);
        }

        return React.createElement('div', null,
            // Modals
            React.createElement(window.AccountCreationModal, {
                show: showAccountModal,
                onHide: () => setShowAccountModal(false),
                onAccountCreated: handleAccountCreated,
                customerId: user?.id
            }),

            React.createElement(window.MoneyTransferModal, {
                show: showTransferModal,
                onHide: () => setShowTransferModal(false),
                onTransferCompleted: handleTransferCompleted,
                userAccounts: accounts
            }),

            // Stats Overview
            React.createElement('div', { className: 'row' },
                React.createElement('h4', { className: 'mb-4' }, 'Stats Overview'),
                React.createElement('div', { className: 'col-md-4' },
                    React.createElement('div', { className: 'stat-card shadow-sm text-center' },
                        React.createElement('i', { className: 'fas fa-dollar-sign text-primary mb-3', style: { fontSize: '2rem' } }),
                        React.createElement('h3', { className: 'stat-number' }, formatCurrency(stats.totalBalance || 0)),
                        React.createElement('p', { className: 'text-muted' }, 'Total Balance')
                    )
                ),
                React.createElement('div', { className: 'col-md-4' },
                    React.createElement('div', { className: 'stat-card shadow-sm text-center' },
                        React.createElement('i', { className: 'fas fa-credit-card text-success mb-3', style: { fontSize: '2rem' } }),
                        React.createElement('h3', { className: 'stat-number' }, stats.activeAccounts),
                        React.createElement('p', { className: 'text-muted' }, 'Active Accounts')
                    )
                ),
                React.createElement('div', { className: 'col-md-4' },
                    React.createElement('div', { className: 'stat-card shadow-sm text-center' },
                        React.createElement('i', { className: 'fas fa-exchange-alt text-info mb-3', style: { fontSize: '2rem' } }),
                        React.createElement('h3', { className: 'stat-number' }, stats.recentTransactions),
                        React.createElement('p', { className: 'text-muted' }, 'Recent Transactions')
                    )
                )
            ),

            // Quick Actions
            React.createElement('div', { className: 'row mt-5' },
                React.createElement('h4', { className: 'mb-4' }, 'Quick Actions'),
                React.createElement(window.QuickActionCard, {
                    icon: 'fas fa-user-circle',
                    title: 'My Profile',
                    description: 'View and edit your personal information',
                    href: '/profile'
                }),
                React.createElement(window.QuickActionCard, {
                    icon: 'fas fa-plus',
                    title: 'New Account',
                    description: 'Open a new checking or savings account',
                    onClick: (e) => {
                        e.preventDefault();
                        setShowAccountModal(true);
                    }
                }),
                React.createElement(window.QuickActionCard, {
                    icon: 'fas fa-exchange-alt',
                    title: 'Transfer Money',
                    description: 'Move funds between accounts',
                    onClick: (e) => {
                        e.preventDefault();
                        if (accounts.filter(acc => acc.status === 'ACTIVE').length < 1) {
                            alert('You need at least one active account to make transfers');
                        } else {
                            setShowTransferModal(true);
                        }
                    }
                }),
                React.createElement(window.QuickActionCard, {
                    icon: 'fas fa-file-invoice-dollar',
                    title: 'Statements',
                    description: 'Review your monthly statements',
                    href: `/statement?customerId=${user.id}&year=${new Date().getFullYear()}&month=${new Date().getMonth() + 1}`
                })
            ),

            // Your Accounts
            React.createElement('div', { className: 'row mt-5' },
                React.createElement('h4', { className: 'mb-4' }, 'Your Accounts'),
                accountsLoading ? (
                    React.createElement('div', { className: 'text-center' },
                        React.createElement('div', { className: 'loading-spinner' }),
                        React.createElement('p', null, 'Loading your accounts...')
                    )
                ) : accounts.length > 0 ? (
                    accounts.map(account =>
                        React.createElement(window.AccountSummaryCard, {
                            key: account.id || account.accountNumber,
                            account: account
                        })
                    )
                ) : (
                    React.createElement('div', { className: 'text-center py-5' },
                        React.createElement('i', { className: 'fas fa-credit-card text-muted', style: { fontSize: '3rem' } }),
                        React.createElement('h5', { className: 'text-muted mt-3' }, 'No accounts found'),
                        React.createElement('p', { className: 'text-muted' }, 'It looks like you don\'t have any accounts. Try opening one!')
                    )
                )
            ),

            // Recent Activity
            React.createElement('div', { className: 'row mt-5' },
                React.createElement('div', { className: 'col-12' },
                    React.createElement('div', { className: 'dashboard-card card' },
                        React.createElement('div', { className: 'card-header' },
                            React.createElement('h5', { className: 'mb-0' },
                                React.createElement('i', { className: 'fas fa-history me-2' }),
                                'Recent Activity'
                            )
                        ),
                        React.createElement('div', { className: 'card-body' },
                            recentTransactions.length > 0 ? (
                                React.createElement('div', { className: 'row' },
                                    recentTransactions.map(transaction =>
                                        React.createElement(window.TransactionCard, {
                                            key: transaction.id,
                                            transaction: transaction
                                        })
                                    )
                                )
                            ) : (
                                React.createElement('div', { className: 'text-center py-4 text-muted' },
                                    React.createElement('i', { className: 'fas fa-clock', style: { fontSize: '2rem' } }),
                                    React.createElement('p', { className: 'mt-3' }, 'No recent activity to display'),
                                    React.createElement('small', null, 'Your transactions will appear here once you start banking with us')
                                )
                            )
                        )
                    )
                )
            )
        );
    };

// Initialize the dashboard when DOM is loaded
    document.addEventListener('DOMContentLoaded', () => {
        const dashboardRoot = document.getElementById('dashboard-root');
        if (dashboardRoot) {
            console.log(">>> Rendering Dashboard...");
            ReactDOM.render(
                React.createElement(Dashboard),
                dashboardRoot
            );
        }
    });
    window.Dashboard = Dashboard;
})();

