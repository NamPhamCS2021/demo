import React, { useEffect, useState } from "react";
import Navbar from "./layout/Navbar.jsx";

function Profile() {
    const [user, setUser] = useState(null);
    const [customer, setCustomer] = useState(null);
    const [accounts, setAccounts] = useState([]);
    const [error, setError] = useState("");

    const handleLogout = async () => {
        try {
            const response = await fetch("/api/auth/logout", {
                method: "POST",
                credentials: "include",
            });
            if (response.ok) {
                window.location.href = "/login";
            } else {
                alert("Logout failed. Please try again.");
            }
        } catch (err) {
            alert("Error during logout: " + err.message);
        }
    };

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await fetch("/api/auth/me", { credentials: "include" });
                const result = await res.json();

                if (res.ok && result.resultCode === "00") {
                    setUser(result.data);

                    const cusRes = await fetch(`/api/customers/profile`, { credentials: "include" });
                    const cusResult = await cusRes.json();
                    if (cusRes.ok && cusResult.resultCode === "00") {
                        setCustomer(cusResult.data);
                    }

                    const accRes = await fetch(`/api/accounts/me`, { credentials: "include" });
                    const accResult = await accRes.json();
                    if (accRes.ok && accResult.resultCode === "00") {
                        setAccounts(accResult.data.content || accResult.data || []);
                    }
                } else {
                    setError(result.resultMessage || "Failed to load user info");
                }
            } catch (err) {
                setError("Network error: " + err.message);
            }
        };

        fetchProfile();
    }, []);

    return (
        <div>
            {/* Shared Navbar */}
            <Navbar user={user} onLogout={handleLogout} />

            {/* Main content */}
            <div className="container mt-4">
                <h3 className="mb-4">Profile</h3>

                {error && <div className="alert alert-danger">{error}</div>}

                <div className="row">
                    {/* Left side: Profile Info */}
                    <div className="col-md-4">
                        <div className="card shadow-sm mb-4">
                            <div className="card-body">
                                {customer ? (
                                    <>
                                        <h5>{customer.firstName} {customer.lastName}</h5>
                                        <p className="text-muted">{customer.email}</p>
                                        <p><strong>Phone:</strong> {customer.phoneNumber}</p>
                                        <p><strong>Status:</strong> {customer.status}</p>
                                    </>
                                ) : (
                                    <p>Loading customer info...</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Right side: Accounts */}
                    <div className="col-md-8">
                        <div className="card shadow-sm">
                            <div className="card-body">
                                <h5 className="mb-3">Accounts</h5>
                                {accounts.length > 0 ? (
                                    <div className="table-responsive">
                                        <table className="table table-bordered table-hover">
                                            <thead className="table-light">
                                            <tr>
                                                <th>Account Number</th>
                                                <th>Status</th>
                                                <th>Opening Date</th>
                                                <th className="text-end">Balance</th>
                                                <th className="text-end">Limit</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {accounts.map((acc, i) => (
                                                <tr key={i}>
                                                    <td>{acc.accountNumber}</td>
                                                    <td>{acc.status}</td>
                                                    <td>{acc.openingDate || "N/A"}</td>
                                                    <td className="text-end">{acc.balance}</td>
                                                    <td className="text-end">{acc.accountLimit}</td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </table>
                                    </div>
                                ) : (
                                    <p>No accounts found.</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <footer className="mt-5 text-center text-muted">
                    <hr />
                    <p>&copy; 2025 Demo Banking</p>
                </footer>
            </div>
        </div>
    );
}

export default Profile;
