// ===== src/services/api.js =====
export const fetchWithAuth = async (url, options = {}) => {
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
};

export const login = async (credentials) => {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(credentials),
    });
    return response.json();
};

export const apiService = {
    // Auth
    async login(credentials) {
        return login(credentials);
    },
    async logout() {
        return fetchWithAuth("/api/auth/logout", { method: "POST" });
    },
    async getCurrentUser() {
        return fetchWithAuth("/api/auth/me");
    },

    // Customer
    async getCustomerProfile() {
        return fetchWithAuth("/api/customers/profile");
    },

    // Accounts
    async getAccounts() {
        return fetchWithAuth("/api/accounts/me");
    },
    async getAccountByAccountNumber(accountNumber) {
        return fetchWithAuth(`/api/accounts/byAccountNumber/${accountNumber}`);
    },
    async createAccount(customerId) {
        return fetchWithAuth("/api/accounts", {
            method: "POST",
            body: JSON.stringify({ customerId })
        });
    },
    async getReceiver(accountNumber) {
        return fetchWithAuth(`/api/accounts/byAccountNumber/receiver/${accountNumber}`);
    },

    // Transactions
    async transferByAccountNumber(transferData) {
        return fetchWithAuth("/api/transactions/transferByAccountNumber", {
            method: "POST",
            body: JSON.stringify(transferData)
        });
    },
    async searchTransactionsByAccountNumber(accountNumber, searchData, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        return fetchWithAuth(`/api/transactions/account/accountnumber/${accountNumber}/search?${queryString}`, {
            method: "PUT",
            body: JSON.stringify(searchData)
        });
    },

    // Statements
    async getMonthlyStatement(customerId, year, month, params = {}) {
        const queryString = new URLSearchParams({ year, month, ...params }).toString();
        return fetchWithAuth(`/api/statements/${customerId}?${queryString}`);
    }
};