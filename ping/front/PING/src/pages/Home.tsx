"use client"

import { Link } from "react-router-dom"

const Home = () => {
    return (
        <div className="w-screen min-h-screen bg-gradient-to-b from-[#E1A624] to-[#D4D3DC] flex flex-col relative">
            {/* Profile Button */}
            <div className="absolute top-6 right-6">
                <Link to="/profile">
                    <div className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#842D50] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
            <span role="img" aria-label="profile" className="text-2xl">
              👤
            </span>
                    </div>
                </Link>
            </div>

            {/* Main Content */}
            <div className="flex-1 flex items-center justify-center px-8">
                <div className="flex items-center justify-between w-full max-w-6xl">
                    {/* Left Side - Text Content */}
                    <div className="flex-1 pr-16">
                        <h1 className="text-6xl font-bold text-white mb-6">Tick-E Taka:</h1>
                        <div className="text-3xl text-white font-medium leading-relaxed">
                            <div className="underline decoration-2 underline-offset-4">Every question passes,</div>
                            <div className="underline decoration-2 underline-offset-4 mt-2">Every answer scores.</div>
                        </div>
                    </div>

                    {/* Right Side - Logo */}
                    <div className="flex-1 flex justify-center">
                        <div className="w-80 h-80 flex items-center justify-center">
                            <img
                                src="/White-Logo-without-bg.png"
                                alt="Tick-E Taka Logo"
                                className="w-full h-full object-contain filter drop-shadow-lg"
                            />
                        </div>
                    </div>
                </div>
            </div>

            {/* Bottom Buttons */}
            <div className="pb-20 flex justify-center">
                <div className="flex gap-8">
                    <Link to="/qa_faqs">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white font-bold text-2xl px-16 py-6 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            Q&A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white font-bold text-2xl px-16 py-6 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            My tickets
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default Home
