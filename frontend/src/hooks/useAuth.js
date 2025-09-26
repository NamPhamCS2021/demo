// ===== src/components/hooks/useAuth.jsx =====
import { useState, useEffect } from 'react';
import { apiService } from '../services/api';

export const useAuth = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const result = await apiService.getCurrentUser();
            if (result.resultCode === "00") {
                setUser(result.data);
            } else {
                setError(result.resultMessage);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const login = async (credentials) => {
        try {
            setError(null);
            const result = await apiService.login(credentials);
            if (result.resultCode === "00") {
                setUser(result.data);
                return { success: true };
            } else {
                throw new Error(result.resultMessage || "Login failed");
            }
        } catch (err) {
            setError(err.message);
            return { success: false, error: err.message };
        }
    };

    const logout = async () => {
        try {
            await apiService.logout();
            setUser(null);
            window.location.href = "/login";
        } catch (err) {
            console.error("Logout failed:", err);
            setUser(null);
            window.location.href = "/login";
        }
    };

    return { user, loading, error, login, logout, refetch: checkAuthStatus };
};
