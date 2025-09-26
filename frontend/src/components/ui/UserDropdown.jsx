// ===== src/components/ui/UserDropdown.jsx =====
import React from 'react'

const UserDropdown = ({ user, onLogout }) => {
    if (!user) return null

    const handleLogout = async () => {
        if (window.confirm('Are you sure you want to logout?')) {
            await onLogout()
        }
    }

    return (
        <div className="dropdown">
            <button
                className="btn btn-link text-white dropdown-toggle text-decoration-none d-flex align-items-center"
                type="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
            >
                <i className="fas fa-user-circle me-2" style={{ fontSize: '1.5rem' }} />
                <div className="text-start">
                    <div className="fw-medium">Welcome</div>
                    <small className="opacity-75">{user.firstName || user.username}</small>
                </div>
            </button>
            <ul className="dropdown-menu dropdown-menu-end shadow-lg border-0"
                style={{ borderRadius: '12px', minWidth: '200px' }}>
                <li>
                    <a className="dropdown-item py-2" href="/profile">
                        <i className="fas fa-user me-3 text-primary" />
                        My Profile
                    </a>
                </li>
                <li>
                    <a className="dropdown-item py-2" href="/dashboard">
                        <i className="fas fa-tachometer-alt me-3 text-primary" />
                        Dashboard
                    </a>
                </li>
                <li>
                    <a className="dropdown-item py-2" href="/statement">
                        <i className="fas fa-file-invoice-dollar me-3 text-primary" />
                        Statements
                    </a>
                </li>
                <li><hr className="dropdown-divider my-2" /></li>
                <li>
                    <button className="dropdown-item py-2 text-danger" onClick={handleLogout}>
                        <i className="fas fa-sign-out-alt me-3" />
                        Logout
                    </button>
                </li>
            </ul>
        </div>
    )
}

export default UserDropdown