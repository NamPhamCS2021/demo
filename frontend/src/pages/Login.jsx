// ===== FILE: src/pages/Login.jsx =====
import React, { useState } from 'react'
import { useAuth } from '../hooks/useAuth.js'

const Login = () => {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const { login } = useAuth()

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError("")

        try {
            const result = await login({ username: email, password })
            if (result.success) {
                window.location.href = "/dashboard"
            } else {
                setError(result.error || "Login failed")
            }
        } catch (err) {
            setError("Network error: " + err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen d-flex align-items-center justify-content-center bg-light">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-6 col-lg-4">
                        <div className="card border-0 shadow-lg" style={{ borderRadius: '20px' }}>
                            <div className="card-body p-5">
                                <div className="text-center mb-4">
                                    <div className="bg-primary bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3"
                                         style={{ width: '60px', height: '60px' }}>
                                        <i className="fas fa-university text-primary" style={{ fontSize: '1.5rem' }} />
                                    </div>
                                    <h2 className="fw-bold text-dark mb-1">Welcome Back</h2>
                                    <p className="text-muted">Sign in to your account</p>
                                </div>

                                {error && (
                                    <div className="alert alert-danger rounded-pill" role="alert">
                                        <i className="fas fa-exclamation-triangle me-2" />
                                        {error}
                                    </div>
                                )}

                                <form onSubmit={handleSubmit}>
                                    <div className="mb-4">
                                        <label className="form-label fw-medium text-dark">Email Address</label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light border-end-0">
                                                <i className="fas fa-envelope text-muted" />
                                            </span>
                                            <input
                                                type="email"
                                                className="form-control border-start-0 bg-light"
                                                value={email}
                                                onChange={(e) => setEmail(e.target.value)}
                                                required
                                                disabled={loading}
                                                style={{ borderRadius: '0 12px 12px 0' }}
                                            />
                                        </div>
                                    </div>

                                    <div className="mb-4">
                                        <label className="form-label fw-medium text-dark">Password</label>
                                        <div className="input-group">
                                            <span className="input-group-text bg-light border-end-0">
                                                <i className="fas fa-lock text-muted" />
                                            </span>
                                            <input
                                                type="password"
                                                className="form-control border-start-0 bg-light"
                                                value={password}
                                                onChange={(e) => setPassword(e.target.value)}
                                                required
                                                disabled={loading}
                                                style={{ borderRadius: '0 12px 12px 0' }}
                                            />
                                        </div>
                                    </div>

                                    <button
                                        className="btn btn-primary w-100 rounded-pill py-3 fw-medium"
                                        type="submit"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" />
                                                Signing In...
                                            </>
                                        ) : (
                                            <>
                                                <i className="fas fa-sign-in-alt me-2" />
                                                Sign In
                                            </>
                                        )}
                                    </button>
                                </form>

                                <div className="text-center mt-4">
                                    <small className="text-muted">
                                        Protected by industry-standard security
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login