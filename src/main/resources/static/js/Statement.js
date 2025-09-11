(() => {
    const { useState, useEffect } = React;

    const Statement = ({ year: initialYear, month: initialMonth, customerId }) => {
        const [year, setYear] = useState(initialYear || new Date().getFullYear());
        const [month, setMonth] = useState(initialMonth || new Date().getMonth() + 1);
        const [loading, setLoading] = useState(false);
        const [error, setError] = useState('');
        const [statement, setStatement] = useState(null);

        const fetchStatement = async () => {
            setLoading(true);
            setError('');
            try {
                const response = await fetch(`/api/statements/${customerId}?year=${year}&month=${month}`, {
                    credentials: 'include'
                });
                const result = await response.json();
                if (response.ok && result.resultCode === '00') {
                    setStatement(result.data);
                } else {
                    setError(result.resultMessage || 'Failed to load statement');
                }
            } catch (err) {
                setError('Network error: ' + err.message);
            } finally {
                setLoading(false);
            }
        };

        useEffect(() => {
            if (customerId) {
                fetchStatement();
            }
        }, [year, month, customerId]);

        const handlePrevMonth = () => {
            if (month === 1) {
                setMonth(12);
                setYear(year - 1);
            } else {
                setMonth(month - 1);
            }
        };

        const handleNextMonth = () => {
            if (month === 12) {
                setMonth(1);
                setYear(year + 1);
            } else {
                setMonth(month + 1);
            }
        };

        return React.createElement('div', { className: 'container my-4' },
            React.createElement('h3', { className: 'mb-3' }, 'Monthly Statement'),

            loading
                ? React.createElement('div', { className: 'text-center py-4' },
                    React.createElement('div', { className: 'spinner-border text-primary', role: 'status' }),
                    React.createElement('p', { className: 'mt-3' }, 'Loading statement...')
                )
                : error
                    ? React.createElement('div', { className: 'alert alert-danger' }, error)
                    : statement
                        ? React.createElement('div', null,
                            // Navigation
                            React.createElement('div', { className: 'd-flex justify-content-between align-items-center mb-3' },
                                React.createElement('button', { className: 'btn btn-outline-secondary btn-sm', onClick: handlePrevMonth }, '← Prev'),
                                React.createElement('h6', null, `${year} - ${month.toString().padStart(2, '0')}`),
                                React.createElement('button', { className: 'btn btn-outline-secondary btn-sm', onClick: handleNextMonth }, 'Next →')
                            ),
                            // Summary
                            React.createElement('div', { className: 'mb-3' },
                                React.createElement('p', null, React.createElement('strong', null, 'Opening Balance: '), statement.openingBalance),
                                React.createElement('p', null, React.createElement('strong', null, 'Closing Balance: '), statement.closingBalance),
                                React.createElement('p', null, React.createElement('strong', null, 'Total In: '), statement.totalCredits),
                                React.createElement('p', null, React.createElement('strong', null, 'Total Out: '), statement.totalDebits)
                            ),
                            // Transactions
                            React.createElement('ul', { className: 'list-group' },
                                statement.transactions && statement.transactions.length > 0
                                    ? statement.transactions.map((tx, i) =>
                                        React.createElement('li', { key: i, className: 'list-group-item d-flex justify-content-between' },
                                            React.createElement('div', null,
                                                React.createElement('div', null, `${tx.type} - ${tx.location}`),
                                                React.createElement('small', { className: 'text-muted' }, new Date(tx.timestamp).toLocaleString())
                                            ),
                                            React.createElement('div', { className: tx.type === 'DEPOSIT' ? 'text-success' : 'text-danger' },
                                                (tx.type === 'DEPOSIT' ? '+' : '-') + tx.amount
                                            )
                                        )
                                    )
                                    : React.createElement('li', { className: 'list-group-item text-center' }, 'No transactions this month')
                            )
                        )
                        : React.createElement('p', null, 'No statement available')
        );
    };
    (() => {
        const { useState, useEffect } = React;

        const Statement = ({ year: initialYear, month: initialMonth, customerId }) => {
            const [year, setYear] = useState(initialYear || new Date().getFullYear());
            const [month, setMonth] = useState(initialMonth || new Date().getMonth() + 1);
            const [loading, setLoading] = useState(false);
            const [error, setError] = useState('');
            const [statement, setStatement] = useState(null);

            const fetchStatement = async () => {
                setLoading(true);
                setError('');
                try {
                    const response = await fetch(`/api/statements/${customerId}?year=${year}&month=${month}`, {
                        credentials: 'include'
                    });
                    const result = await response.json();
                    if (response.ok && result.resultCode === '00') {
                        setStatement(result.data);
                    } else {
                        setError(result.resultMessage || 'Failed to load statement');
                    }
                } catch (err) {
                    setError('Network error: ' + err.message);
                } finally {
                    setLoading(false);
                }
            };

            useEffect(() => {
                if (customerId) {
                    fetchStatement();
                }
            }, [year, month, customerId]);

            const handlePrevMonth = () => {
                if (month === 1) {
                    setMonth(12);
                    setYear(year - 1);
                } else {
                    setMonth(month - 1);
                }
            };

            const handleNextMonth = () => {
                if (month === 12) {
                    setMonth(1);
                    setYear(year + 1);
                } else {
                    setMonth(month + 1);
                }
            };

            return React.createElement('div', { className: 'container my-4' },
                React.createElement('h3', { className: 'mb-3' }, 'Monthly Statement'),

                loading
                    ? React.createElement('div', { className: 'text-center py-4' },
                        React.createElement('div', { className: 'spinner-border text-primary', role: 'status' }),
                        React.createElement('p', { className: 'mt-3' }, 'Loading statement...')
                    )
                    : error
                        ? React.createElement('div', { className: 'alert alert-danger' }, error)
                        : statement
                            ? React.createElement('div', null,
                                // Navigation
                                React.createElement('div', { className: 'd-flex justify-content-between align-items-center mb-3' },
                                    React.createElement('button', { className: 'btn btn-outline-secondary btn-sm', onClick: handlePrevMonth }, '← Prev'),
                                    React.createElement('h6', null, `${year} - ${month.toString().padStart(2, '0')}`),
                                    React.createElement('button', { className: 'btn btn-outline-secondary btn-sm', onClick: handleNextMonth }, 'Next →')
                                ),
                                // Summary
                                React.createElement('div', { className: 'mb-3' },
                                    React.createElement('p', null, React.createElement('strong', null, 'Opening Balance: '), statement.openingBalance),
                                    React.createElement('p', null, React.createElement('strong', null, 'Closing Balance: '), statement.closingBalance),
                                    React.createElement('p', null, React.createElement('strong', null, 'Total In: '), statement.totalCredits),
                                    React.createElement('p', null, React.createElement('strong', null, 'Total Out: '), statement.totalDebits)
                                ),
                                // Transactions
                                React.createElement('ul', { className: 'list-group' },
                                    statement.transactions && statement.transactions.length > 0
                                        ? statement.transactions.map((tx, i) =>
                                            React.createElement('li', { key: i, className: 'list-group-item d-flex justify-content-between' },
                                                React.createElement('div', null,
                                                    React.createElement('div', null, `${tx.type} - ${tx.location}`),
                                                    React.createElement('small', { className: 'text-muted' }, new Date(tx.timestamp).toLocaleString())
                                                ),
                                                React.createElement('div', { className: tx.type === 'DEPOSIT' ? 'text-success' : 'text-danger' },
                                                    (tx.type === 'DEPOSIT' ? '+' : '-') + tx.amount
                                                )
                                            )
                                        )
                                        : React.createElement('li', { className: 'list-group-item text-center' }, 'No transactions this month')
                                )
                            )
                            : React.createElement('p', null, 'No statement available')
            );
        };

        // Attach globally
        window.Statement = Statement;

        // Auto-mount if statement-root exists
        const rootEl = document.getElementById("statement-root");
        if (rootEl) {
            const year = parseInt(rootEl.getAttribute("data-year"));
            const month = parseInt(rootEl.getAttribute("data-month"));
            const customerId = parseInt(rootEl.getAttribute("data-customer-id"));

            ReactDOM.createRoot(rootEl).render(
                React.createElement(window.Statement, { year, month, customerId })
            );
        }
    })();

    // Attach globally
    window.Statement = Statement;
// Initialize the dashboard when DOM is loaded
    document.addEventListener('DOMContentLoaded', () => {
        const statementRoot = document.getElementById('statement-root');
        if (statementRoot) {
            console.log(">>> Rendering Statement...");
            const customerId = statementRoot.dataset.customerId;
            const year = parseInt(statementRoot.dataset.year);
            const month = parseInt(statementRoot.dataset.month);
            ReactDOM.render(
                React.createElement(Statement, {
                    customerId,
                    initialYear: year,
                    initialMonth: month
                }),
                statementRoot
            );
        }
    });
    window.Dashboard = Dashboard;
    // // Auto-mount if statement-root exists
    // const rootEl = document.getElementById("statement-root");
    // if (rootEl) {
    //     const year = parseInt(rootEl.getAttribute("data-year"));
    //     const month = parseInt(rootEl.getAttribute("data-month"));
    //     const customerId = parseInt(rootEl.getAttribute("data-customer-id"));
    //
    //     ReactDOM.createRoot(rootEl).render(
    //         React.createElement(window.Statement, { year, month, customerId })
    //     );
    // }
})();
