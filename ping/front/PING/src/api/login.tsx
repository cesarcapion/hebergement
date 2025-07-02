import {getUserGroupFromToken} from "../AdminRoute.tsx";

export const login = async (email: string, password: string): Promise<void> => {
  try {
    const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/api/user/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ mail: email, password }),
    });
    console.log(`mail: ${email}, password: ${password}, response:`, response);
    const data = await response.json();

    if (response.ok) {
      localStorage.setItem('token', data.token);
      const group = getUserGroupFromToken();
      if (group?.toString() == "user")
      {
        window.location.href = `${import.meta.env.VITE_BASE_URL}`;
      }
      else
      {
        window.location.href = `${import.meta.env.VITE_BASE_URL}admin`;
      }
    } else {
      alert(data.message || "Erreur de connexion.");
    }
    console.log("CA AMRAHCZIBGZKJEBGUORBGIUBGJNRJONGKJLEZGNVJREIUVB")
  } catch (error) {
    console.error("Login error:", error);
    alert("Erreur réseau");
  }
};