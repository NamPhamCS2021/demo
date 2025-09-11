// QuickActionCard.js
const QuickActionCard = ({ icon, title, description, href, onClick }) => {
    const handleClick = (e) => {
        if (onClick) {
            e.preventDefault();
            onClick(e);
        }
    };

    return React.createElement('div', { className: 'col-md-6 col-lg-3 mb-4' },
        React.createElement('a', {
                href: href,
                onClick: handleClick,
                className: 'quick-action d-block'
            },
            React.createElement('i', { className: icon }),
            React.createElement('h6', { className: 'mt-2 mb-1' }, title),
            React.createElement('small', { className: 'text-muted' }, description)
        )
    );
};

// Make it globally available
window.QuickActionCard = QuickActionCard;