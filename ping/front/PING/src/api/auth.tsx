const apiURL = import.meta.env.VITE_SERVER_URL;
const frontURL = import.meta.env.VITE_BASE_URL;

type FetchOptions = RequestInit;

async function fetchWithToken(endpoint: string, token: string, options: FetchOptions = {}): Promise<Response> {
    return await fetch(`${apiURL}/api${endpoint}`, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${token}`,
        },
    });
}


export async function authedAPIRequest(endpoint: string, options: FetchOptions = {}): Promise<Response | null> {
    const token = localStorage.getItem("token");

    if (!token) {
        console.warn("Token manquant.");
        redirectToLogin();
        return authedAPIRequest(endpoint,options);
    }

    const response = await fetchWithToken(endpoint, token, options);
    if (response.status === 401) {
        console.warn("Token expiré.");
        redirectToLogin();
        return authedAPIRequest(endpoint,options);
    }

    return response;
}


function redirectToLogin(): void {
    localStorage.clear();
    window.location.href = `${frontURL}/login`;
}
