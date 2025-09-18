import React from 'react';

const Navbar = ({ user, onLogout }) => {
    const handleLogout = async () => {
        if (window.confirm('Are you sure you want to logout?')) {
            await onLogout();
        }
    };

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
                    <div className="dropdown">
                        <button className="btn btn-link text-white dropdown-toggle text-decoration-none d-flex align-items-center"
                                type="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i className="fas fa-user-circle me-2" style={{ fontSize: '1.5rem' }} />
                            <div className="text-start">
                                <div className="fw-medium">Welcome</div>
                                <small className="opacity-75">{user?.username}</small>
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
                            <li><hr className="dropdown-divider my-2" /></li>
                            <li>
                                <button className="dropdown-item py-2 text-danger" onClick={handleLogout}>
                                    <i className="fas fa-sign-out-alt me-3" />
                                    Logout
                                </button>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
