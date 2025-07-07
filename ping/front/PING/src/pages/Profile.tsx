import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authedAPIRequest } from "../api/auth.tsx";

export const getUserIdFromToken = (): string | null => {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.sub || null;
    } catch (err) {
        console.error("Erreur lors du décodage du token :", err);
        return null;
    }
};

export default function Profile() {
    const [userEmail, setUserEmail] = useState<string | null>(null);
    const [memberSince, setMemberSince] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserData = async () => {
            const userId = getUserIdFromToken();
            if (!userId) {
                console.error("Aucun ID utilisateur trouvé dans le token.");
                return;
            }
            console.log(userId);
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/user/${userId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            console.log(response?.statusText);
            if (!response) return;

            const data = await response.json();
            setUserEmail(data.mail);
            setMemberSince(data.created);
        };

        fetchUserData();
    }, []);

    const handleLogout = () => {
        if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
            localStorage.clear();
            navigate("/login");
        }
    };

    return (
        <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
            {/* Navbar */}
            <div className="bg-[#FFD068] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-auto"/>
                    </div>
                </Link>
                <div className="flex gap-6">
                    <Link to="/qa">
                        <button
                            className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white text-2xl px-8 py-2 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            Q&amp;A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button
                            className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white text-2xl px-8 py-2 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">My
                            tickets
                        </button>
                    </Link>
                </div>
                <Link to="/profile">
                    <div
                        className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#842D50] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
                        <span role="img" aria-label="profile" className="text-2xl">👤</span>
                    </div>
                </Link>
            </div>

            {/* Main Content */}
            <div className="flex-1 flex flex-col items-center justify-center px-4">
                <h1 className="text-5xl font-bold text-white text-center mb-12">My account</h1>

                <div className="text-center mb-12 space-y-4">
                    <p className="text-xl text-gray-300">
                        <span className="text-white font-medium">Email : </span>
                        {userEmail || "Chargement..."}
                    </p>
                    <p className="text-xl text-gray-300">
                        <span className="text-white font-medium">Member since </span>
                        {memberSince || "Chargement..."}
                    </p>
                </div>

                <div className="flex flex-col items-center gap-6">
                    <button onClick={handleLogout} className="btn">Log-out</button>
                </div>
            </div>

            <div className="fixed bottom-6 right-6">
                <button
                    className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
                    <span className="text-xl">?</span>
                </button>
            </div>
        </div>
    );
}
