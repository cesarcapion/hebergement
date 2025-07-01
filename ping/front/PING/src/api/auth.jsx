const apiURL = import.meta.env.VITE_SERVER_URL
const frontURL = import.meta.env.VITE_BASE_URL
async function fetchWithToken(endpoint, token, options = {}) {
    return await fetch(`${apiURL}/api${endpoint}`, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${token}`,
        },
    });
}


export async function authedAPIRequest(endpoint, options) {

    let token = localStorage.getItem("token");

    // pas de token
    if (!token) {
        console.log("LA YA PAS DE TOKEN");

        redirectToLogin();
        return;
    }
    //on a un token
    let response = await fetchWithToken(endpoint, token, options);

    console.log(response);
    // check si le token est expiré
    if (response.status === 401) {
        redirectToLogin();
        return;
    }

    //le fetch a fonctionné
    return response;
}
function redirectToLogin() {
    localStorage.clear();
    window.location.href = `${frontURL}/login`;
}