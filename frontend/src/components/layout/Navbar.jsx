// ===== src/components/layout/Navbar.jsx =====
import React from 'react'
import UserDropdown from '../ui/UserDropdown'

const Navbar = ({ user, onLogout }) => {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark shadow-sm"
             style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
            <div className="container">
                <a className="navbar-brand fw-bold d-flex align-items-center" href="/dashboard">
                    <div className="bg-white text-primary rounded-circle d-flex align-items-center justify-content-center me-3"
                         style={{ width: '40px', height: '40px', fontSize: '18px', fontWeight: 'bold' }}>
                        D
                    </div>
                    <span style={{ fontSize: '1.25rem' }}>Demo Banking</span>
                </a>

                <div className="navbar-nav ms-auto">
                    <UserDropdown user={user} onLogout={onLogout} />
                </div>
            </div>
        </nav>
    )
}

export default Navbar