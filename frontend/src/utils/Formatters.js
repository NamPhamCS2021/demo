export const formatCurrency = (amount, currency = "USD") => {
    if (amount === null || amount === undefined) return "-";
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency,
    }).format(amount);
};

export const formatDateTime = (isoString) => {
    if (!isoString) return "-";
    return new Date(isoString).toLocaleString("en-GB", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

export const maskAccountNumber = (accountNumber) => {
    if (!accountNumber) return "-";
    return accountNumber.replace(/.(?=.{4})/g, "*");
};
