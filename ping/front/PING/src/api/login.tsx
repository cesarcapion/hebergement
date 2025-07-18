import {getUserGroupFromToken} from "../AdminRoute.tsx";

export const login = async (email: string, password: string): Promise<string> => {
  try {
    console.log("login");
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
      if (group?.toString() === "user")
      {
        console.log("pas normal");
        window.location.href = `${import.meta.env.VITE_BASE_URL}`;
      }
      else
      {
        console.log("normal");

        window.location.href = `${import.meta.env.VITE_BASE_URL}admin`;
      }

      return ''
    } else {
      //alert(data.message || "Erreur de connexion.");
      return data.message;
    }
  } catch (error) {
    console.error("Login error:", error);
    //alert("Erreur réseau");
    return ""
  }
};
