// ===== src/components/ui/LoadingSpinner.jsx =====
import React from 'react';

const LoadingSpinner = ({ size = 'md', text = 'Loading...' }) => {
    const sizeClasses = {
        sm: 'spinner-border-sm',
        md: '',
        lg: 'spinner-border-lg'
    };

    return (
        <div className="text-center py-5">
            <div className={`spinner-border text-primary ${sizeClasses[size]}`} role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
            {text && <p className="mt-3 text-muted">{text}</p>}
        </div>
    );
};

export default LoadingSpinner;