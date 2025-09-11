// UserDropdown.js
const UserDropdown = ({ user, onLogout }) => {
    return React.createElement('div', { className: 'dropdown' },
        React.createElement('a', {
                className: 'nav-link dropdown-toggle text-white',
                href: '#',
                id: 'navbarDropdown',
                role: 'button',
                'data-bs-toggle': 'dropdown',
                'aria-expanded': 'false'
            },
            React.createElement('i', { className: 'fas fa-user me-2' }),
            user ? `Welcome, ${user.firstName || user.username}` : 'User'
        ),
        React.createElement('ul', {
                className: 'dropdown-menu dropdown-menu-end',
                'aria-labelledby': 'navbarDropdown'
            },
            React.createElement('li', null,
                React.createElement('a', { className: 'dropdown-item', href: '/profile' },
                    React.createElement('i', { className: 'fas fa-user me-2' }),
                    'Profile'
                )
            ),
            React.createElement('li', null,
                React.createElement('a', { className: 'dropdown-item', href: '/dashboard' },
                    React.createElement('i', { className: 'fas fa-tachometer-alt me-2' }),
                    'Dashboard'
                )
            ),
            React.createElement('li', null,
                React.createElement('hr', { className: 'dropdown-divider' })
            ),
            React.createElement('li', null,
                React.createElement('button', {
                        className: 'dropdown-item text-danger',
                        onClick: onLogout,
                        type: 'button'
                    },
                    React.createElement('i', { className: 'fas fa-sign-out-alt me-2' }),
                    'Logout'
                )
            )
        )
    );
};

// Make it globally available
window.UserDropdown = UserDropdown;