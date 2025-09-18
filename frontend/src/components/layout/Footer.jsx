import React from 'react';
import { formatDateTime } from '../../utils/formatters';
import Navbar from "./Navbar.jsx";

const Footer = () => {
    const currentTime = formatDateTime(new Date().toISOString());

    return (
        <footer className="mt-5 py-4 border-top bg-light">
            <div className="container">
                <div className="row align-items-center">
                    <div className="col-md-6">
                        <div className="d-flex align-items-center text-muted">
                            <i className="fas fa-university me-2" />
                            <span>&copy; 2025 Demo Banking. All rights reserved.</span>
                        </div>
                    </div>
                    <div className="col-md-6 text-md-end">
                        <small className="text-muted">
                            <i className="fas fa-clock me-1" />
                            Server Time: {currentTime}
                        </small>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
