// ===== src/components/ui/UserDropdown.jsx =====
import React, { useState } from 'react'

const UserDropdown = ({ user, onLogout }) => {
    const [isOpen, setIsOpen] = useState(false)

    if (!user) return null

    const handleLogout = async () => {
        if (window.confirm('Are you sure you want to logout?')) {
            await onLogout()
        }
    }

    const toggleDropdown = () => {
        setIsOpen(!isOpen)
    }

    const handleNavigation = (path) => {
        window.location.href = path
        setIsOpen(false)
    }

    return (
        <div className="dropdown position-relative">
            <button
                className="btn btn-link text-white text-decoration-none d-flex align-items-center"
                type="button"
                onClick={toggleDropdown}
                style={{ border: 'none', background: 'none' }}
            >
                <i className="fas fa-user-circle me-2" style={{ fontSize: '1.5rem' }} />
                <div className="text-start">
                    <div className="fw-medium">Welcome</div>
                    <small className="opacity-75">{user.firstName || user.username}</small>
                </div>
                <i className={`fas fa-chevron-${isOpen ? 'up' : 'down'} ms-2`} />
            </button>

            {isOpen && (
                <>
                    {/* Backdrop to close dropdown when clicking outside */}
                    <div
                        className="position-fixed top-0 start-0 w-100 h-100"
                        style={{ zIndex: 1040 }}
                        onClick={() => setIsOpen(false)}
                    />

                    <ul
                        className="dropdown-menu show dropdown-menu-end shadow-lg border-0 position-absolute"
                        style={{
                            borderRadius: '12px',
                            minWidth: '200px',
                            zIndex: 1050,
                            right: 0,
                            top: '100%'
                        }}
                    >
                        <li>
                            <button
                                className="dropdown-item py-2 border-0 bg-transparent w-100 text-start"
                                onClick={() => handleNavigation('/profile')}
                                style={{ cursor: 'pointer' }}
                            >
                                <i className="fas fa-user me-3 text-primary" />
                                My Profile
                            </button>
                        </li>
                        <li>
                            <button
                                className="dropdown-item py-2 border-0 bg-transparent w-100 text-start"
                                onClick={() => handleNavigation('/dashboard')}
                                style={{ cursor: 'pointer' }}
                            >
                                <i className="fas fa-tachometer-alt me-3 text-primary" />
                                Dashboard
                            </button>
                        </li>
                        <li>
                            <button
                                className="dropdown-item py-2 border-0 bg-transparent w-100 text-start"
                                onClick={() => {
                                    const urlParams = new URLSearchParams(window.location.search)
                                    const customerId = user.customerPublicId
                                    const year = new Date().getFullYear()
                                    const month = new Date().getMonth() + 1
                                    handleNavigation(`/statement?customerId=${customerId}&year=${year}&month=${month}`)
                                }}
                                style={{ cursor: 'pointer' }}
                            >
                                <i className="fas fa-file-invoice-dollar me-3 text-primary" />
                                Statements
                            </button>
                        </li>
                        <li><hr className="dropdown-divider my-2" /></li>
                        <li>
                            <button
                                className="dropdown-item py-2 text-danger border-0 bg-transparent w-100 text-start"
                                onClick={handleLogout}
                                style={{ cursor: 'pointer' }}
                            >
                                <i className="fas fa-sign-out-alt me-3" />
                                Logout
                            </button>
                        </li>
                    </ul>
                </>
            )}
        </div>
    )
}

export default UserDropdown