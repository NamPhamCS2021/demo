// src/components/UserDropdown.jsx
import React from "react";

const UserDropdown = ({ user, onLogout }) => {
    if (!user) return null;

    return (
        <div className="dropdown">
            <button
                className="btn btn-light dropdown-toggle"
                data-bs-toggle="dropdown"
            >
                {user.username}
            </button>
            <ul className="dropdown-menu dropdown-menu-end">
                <li>
                    <button className="dropdown-item" onClick={onLogout}>
                        Logout
                    </button>
                </li>
            </ul>
        </div>
    );
};

export default UserDropdown;
