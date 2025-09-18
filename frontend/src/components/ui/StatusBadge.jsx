import React from 'react';

const StatusBadge = ({ status }) => {
    const getStatusConfig = (status) => {
        switch (status?.toUpperCase()) {
            case 'ACTIVE':
                return {
                    className: 'bg-success bg-opacity-10 text-success border border-success border-opacity-25',
                    icon: 'fas fa-check-circle'
                };
            case 'BLOCKED':
                return {
                    className: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-25',
                    icon: 'fas fa-exclamation-triangle'
                };
            case 'CLOSED':
                return {
                    className: 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25',
                    icon: 'fas fa-times-circle'
                };
            default:
                return {
                    className: 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25',
                    icon: 'fas fa-question-circle'
                };
        }
    };

    const config = getStatusConfig(status);

    return (
        <span className={`badge px-3 py-2 rounded-pill fw-medium ${config.className}`}>
            <i className={`${config.icon} me-1`} />
            {status || 'Unknown'}
        </span>
    );
};

export default StatusBadge