// ===== FILE: src/services/api.js =====
// Base fetch function with authentication
export async function fetchWithAuth(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(`API error ${response.status}: ${text}`);
    }

    return response.json();
}

// Legacy login function
export async function login(credentials) {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(credentials),
    });
    return response.json();
}

// Main API Service - ONLY includes your actual endpoints
export const apiService = {
    // =====================================
    // AUTHENTICATION (from your existing code)
    // =====================================
    async login(credentials) {
        return login(credentials);
    },

    async logout() {
        return fetchWithAuth("/api/auth/logout", { method: "POST" });
    },

    async getCurrentUser() {
        return fetchWithAuth("/api/auth/me");
    },

    // =====================================
    // CUSTOMER ENDPOINTS (from CustomerController)
    // =====================================
    async getCustomerProfile() {
        return fetchWithAuth("/api/customers/profile");
    },

    async getCustomerById(id) {
        return fetchWithAuth(`/api/customers/${id}`);
    },

    // =====================================
    // ACCOUNT ENDPOINTS (from AccountController)
    // =====================================
    async getAccounts() {
        return fetchWithAuth("/api/accounts/me");
    },

    async getAccountById(id) {
        return fetchWithAuth(`/api/accounts/${id}`);
    },

    async getAccountByAccountNumber(accountNumber) {
        return fetchWithAuth(`/api/accounts/byAccountNumber/${accountNumber}`);
    },

    async getReceiver(accountNumber) {
        return fetchWithAuth(`/api/accounts/byAccountNumber/receiver/${accountNumber}`);
    },

    async getAccountsByCustomerId(customerId, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/accounts/customer/${customerId}?${queryString}`);
    },

    async getAccountsByCustomerIdAndStatus(customerId, status, params = {}) {
        const queryString = new URLSearchParams({ ...params, status }).toString();
        return fetchWithAuth(`/api/accounts/customer/status/${customerId}?${queryString}`);
    },

    async searchSelfAccounts(customerId, searchData, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/accounts/search/${customerId}?${queryString}`, {
            method: "POST",
            body: JSON.stringify(searchData)
        });
    },

    // =====================================
    // TRANSACTION ENDPOINTS (from TransactionController)
    // =====================================
    async deposit(transactionData) {
        return fetchWithAuth("/api/transactions/deposit", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async withdraw(transactionData) {
        return fetchWithAuth("/api/transactions/withdraw", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async transfer(transactionData) {
        return fetchWithAuth("/api/transactions/transfer", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async depositByAccountNumber(transactionData) {
        return fetchWithAuth("/api/transactions/depositByAccountNumber", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async withdrawByAccountNumber(transactionData) {
        return fetchWithAuth("/api/transactions/withdrawByAccountNumber", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async transferByAccountNumber(transactionData) {
        return fetchWithAuth("/api/transactions/transferByAccountNumber", {
            method: "POST",
            body: JSON.stringify(transactionData)
        });
    },

    async getTransaction(id) {
        return fetchWithAuth(`/api/transactions/${id}`);
    },

    async searchAccountTransactions(accountId, searchData, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/transactions/account/${accountId}/search?${queryString}`, {
            method: "PUT",
            body: JSON.stringify(searchData)
        });
    },

    async searchTransactionsByAccountNumber(accountNumber, searchData, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/transactions/account/accountnumber/${accountNumber}/search?${queryString}`, {
            method: "PUT",
            body: JSON.stringify(searchData)
        });
    },

    // =====================================
    // STATEMENT ENDPOINTS (from StatementController)
    // =====================================
    async getMonthlyStatement(customerId, year, month, params = {}) {
        const queryString = new URLSearchParams({ year, month, ...params }).toString();
        return fetchWithAuth(`/api/statements/${customerId}?${queryString}`);
    },

    // =====================================
    // PERIODICAL PAYMENT ENDPOINTS (from PeriodicalPaymentController)
    // =====================================
    async getPeriodicalPayment(id) {
        return fetchWithAuth(`/api/payments/${id}`);
    },

    async getPeriodicalPaymentsByAccount(accountId, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/payments/account/${accountId}?${queryString}`);
    },

    async getPeriodicalPaymentsByAccountAndStatus(accountId, status, params = {}) {
        const queryString = new URLSearchParams({ id: accountId, status, ...params }).toString();
        return fetchWithAuth(`/api/payments/account/status?${queryString}`);
    },

    async searchSelfPeriodicalPayments(accountId, searchData, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/payments/search/${accountId}?${queryString}`, {
            method: "POST",
            body: JSON.stringify(searchData)
        });
    }
};

// =====================================
// HOW TO ADD NEW CUSTOMER ENDPOINTS
// =====================================
/*
When you add new customer-facing endpoints to your backend, just add them here:

Example - if you add a new endpoint like GET /api/customers/preferences:

async getCustomerPreferences() {
    return fetchWithAuth("/api/customers/preferences");
},

async updateCustomerPreferences(preferences) {
    return fetchWithAuth("/api/customers/preferences", {
        method: "PUT",
        body: JSON.stringify(preferences)
    });
},
*/

export default apiService;