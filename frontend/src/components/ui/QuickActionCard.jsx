// ===== src/components/ui/QuickActionCard.jsx =====
import React from 'react'

const QuickActionCard = ({ icon, title, description, href, onClick, disabled = false }) => {
    const handleClick = (e) => {
        if (disabled) {
            e.preventDefault()
            return
        }
        if (onClick) {
            e.preventDefault()
            onClick(e)
        } else if (href) {
            window.location.href = href
        }
    }

    return (
        <div className="col-md-6 col-lg-3 mb-4">
            <div
                className={`card border-0 shadow-sm text-center h-100 ${disabled ? 'opacity-50' : ''}`}
                style={{
                    borderRadius: '12px',
                    cursor: disabled ? 'not-allowed' : 'pointer',
                    transition: 'transform 0.2s'
                }}
                onClick={handleClick}
                onMouseEnter={(e) => !disabled && (e.currentTarget.style.transform = 'translateY(-4px)')}
                onMouseLeave={(e) => !disabled && (e.currentTarget.style.transform = 'translateY(0)')}
            >
                <div className="card-body py-4">
                    <i className={`${icon} text-primary mb-3`} style={{ fontSize: '2rem' }} />
                    <h6 className="fw-bold">{title}</h6>
                    <p className="text-muted small mb-0">{description}</p>
                </div>
            </div>
        </div>
    )
}

export default QuickActionCard