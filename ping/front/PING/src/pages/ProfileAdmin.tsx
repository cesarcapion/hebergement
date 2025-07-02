"use client"

import { Link } from "react-router-dom"

export default function ProfileAdmin() {
    // Données utilisateur admin (à remplacer par de vraies données)
    const userEmail = "xavier.login@epita.fr"
    const memberSince = "02/01/2025"
    const userRole = "Administrator"

    const handleLogout = () => {
        // Logique de déconnexion
        if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
            console.log("Admin logged out")
            // navigate("/login")
        }
    }

    const handleManage = () => {
        // Logique pour gérer le compte
        console.log("Manage account")
        // navigate("/manage-account")
    }

    const handleStats = () => {
        // Logique pour voir les statistiques
        console.log("View stats")
        // navigate("/admin-stats")
    }

    return (
        <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
            {/* Navbar */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
                    </div>
                </Link>

                <div className="flex gap-4">
                    <Link to="/qa">
                        <button className="btn">
                            Q&A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button className="btn">
                            My tickets
                        </button>
                    </Link>
                </div>

                <Link to="/profile">
                    <div className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg">
            <span role="img" aria-label="profile" className="text-2xl">
              👤
            </span>
                    </div>
                </Link>
            </div>

            {/* Main Content */}
            <div className="flex-1 flex flex-col items-center justify-center px-4">
                {/* Title */}
                <h1 className="text-5xl font-bold text-white text-center mb-12">My account</h1>

                {/* User Information */}
                <div className="text-center mb-12 space-y-4">
                    <p className="text-xl text-gray-300">
                        <span className="text-white font-medium">Email : </span>
                        {userEmail}
                    </p>
                    <p className="text-xl text-gray-300">
                        <span className="text-white font-medium">Member since </span>
                        {memberSince}
                    </p>
                    <p className="text-xl text-gray-300">
                        <span className="text-white font-medium">Role : </span>
                        <span className="text-[#EA508E] font-bold">{userRole}</span>
                    </p>
                </div>

                {/* Action Buttons */}
                <div className="flex flex-col items-center gap-6">
                    <button
                        onClick={handleManage}
                        className="btn"
                    >
                        Manage
                    </button>

                    <Link to="/stats">
                    <button
                        onClick={handleStats}
                        className="btn"
                    >
                        Stats
                    </button>
                    </Link>
                    <button
                        onClick={handleLogout}
                        className="btn"
                    >
                        Log-out
                    </button>
                </div>
            </div>

            {/* Help Button */}
            <div className="fixed bottom-6 right-6">
                <button className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
                    <span className="text-xl">?</span>
                </button>
            </div>
        </div>
    )
}
