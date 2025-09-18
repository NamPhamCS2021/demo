import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useApi } from '../hooks/useApi';
import { apiService } from '../services/api';

import Navbar from '../components/layout/Navbar';
import Footer from '../components/layout/Footer';
import ProfileHeader from '../components/profile/ProfileHeader';
import AccountsSection from '../components/accounts/AccountsSection';
import ProfileInformation from '../components/profile/ProfileInformation';

function Dashboard() {
    const { user, logout } = useAuth();
    const [customer, setCustomer] = useState(null);
    const [showProfileInfo, setShowProfileInfo] = useState(false);

    // Fetch customer profile
    const { data: customerData, loading: customerLoading, error: customerError } = useApi(
        () => apiService.getCustomerProfile(),
        []
    );

    // Fetch accounts
    const {
        data: accountsData,
        loading: accountsLoading,
        error: accountsError,
        refetch: refetchAccounts
    } = useApi(
        () => apiService.getAccounts(),
        []
    );

    useEffect(() => {
        if (customerData && customerData.resultCode === "00") {
            setCustomer(customerData.data);
        }
    }, [customerData]);

    const handleToggleProfileInfo = () => {
        setShowProfileInfo(!showProfileInfo);
    };

    const handleRefreshAccounts = async () => {
        try {
            await refetchAccounts();
        } catch (err) {
            console.error('Failed to refresh accounts:', err);
        }
    };

    const handleViewAccountDetails = (accountId) => {
        // Navigate to account details page
        window.location.href = `/accounts/${accountId}`;
    };

    // Extract accounts from API response
    const accounts = accountsData?.resultCode === "00" ?
        (accountsData.data?.content || accountsData.data || []) : [];

    if (!user) {
        return (
            <div className="min-h-screen d-flex align-items-center justify-content-center">
                <div className="text-center">
                    <div className="spinner-border text-primary mb-3" />
                    <p>Loading...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-light">
            <Navbar user={user} onLogout={logout} />

            <div className="container mt-4">
                <ProfileHeader
                    user={user}
                    customer={customer}
                    onToggleInfo={handleToggleProfileInfo}
                />

                <AccountsSection
                    accounts={accounts}
                    loading={accountsLoading}
                    error={accountsError}
                    onRefresh={handleRefreshAccounts}
                    onViewAccountDetails={handleViewAccountDetails}
                />

                <ProfileInformation
                    user={user}
                    customer={customer}
                    isVisible={showProfileInfo}
                />
            </div>

            <Footer />
        </div>
    );
}

export default Dashboard;