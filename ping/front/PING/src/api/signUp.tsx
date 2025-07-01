export const signUp = async (email: string, password: string): Promise<void> => {
    const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/api/user/new-account`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mail: email, password, isAdmin: false }),
    });

    if (response.status === 400) {
        throw new Error("The password must be composed of 12 characters including symbols, numbers, letters, lowercase and uppercase letters.");
    }

    if (!response.ok) {
        throw new Error("Failed to create account. Please try again.");
    }
};
