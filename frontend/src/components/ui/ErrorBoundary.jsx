// ===== src/components/ui/ErrorBoundary.jsx =====
import React from 'react'

class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props)
        this.state = { hasError: false, error: null }
    }

    static getDerivedStateFromError(error) {
        return { hasError: true, error }
    }

    componentDidCatch(error, errorInfo) {
        console.error('Error caught by boundary:', error, errorInfo)
    }

    render() {
        if (this.state.hasError) {
            return (
                <div className="min-h-screen d-flex align-items-center justify-content-center bg-light">
                    <div className="text-center">
                        <i className="fas fa-exclamation-triangle text-danger mb-4" style={{ fontSize: '4rem' }} />
                        <h2 className="text-dark mb-2">Something went wrong</h2>
                        <p className="text-muted mb-4">
                            We're sorry, but something unexpected happened.
                        </p>
                        <button
                            onClick={() => window.location.reload()}
                            className="btn btn-primary"
                        >
                            Refresh Page
                        </button>
                    </div>
                </div>
            )
        }

        return this.props.children
    }
}

export default ErrorBoundary