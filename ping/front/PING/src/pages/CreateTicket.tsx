import { useRef, useState } from "react";
import { Link } from "react-router-dom";

const topics = [
    "Support",
    "Payment",
    "Technical issue",
    "Account",
    "Other"
];

export default function CreateTicket() {
    const [object, setObject] = useState("");
    const [topic, setTopic] = useState("");
    const [text, setText] = useState("");
    const fileInput = useRef<HTMLInputElement>(null);

    return (
        <div className="w-screen min-h-screen bg-[#384454]">
            {/* HEADER */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
                </div>
                <div className="flex gap-6">
                    <Link to="/qa">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl mr-2">
                            Q&amp;A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl">
                            My tickets
                        </button>
                    </Link>
                </div>
                <div className="flex items-center justify-center w-8 h-8 bg-white text-[#EA508E] rounded-full shadow-lg text-xl">
                    <span role="img" aria-label="profile">👤</span>
                </div>
            </div>

            <div className="max-w-2xl mx-auto pt-4 pb-2 px-2">
                <button className="text-white text-xl mb-2 mt-4" onClick={() => window.history.back()}>
                    <span className="text-2xl mr-2">&#8592;</span>
                </button>
                <h1 className="text-white text-3xl font-bold text-center mb-6">New ticket</h1>

                <div className="flex gap-2 mb-2">
                    <input
                        type="text"
                        placeholder="Object"
                        className="w-1/2 px-3 py-2 rounded border-none bg-[#d3d4dc] text-gray-800 font-medium focus:outline-none"
                        value={object}
                        onChange={e => setObject(e.target.value)}
                    />
                    <select
                        className="w-1/2 px-3 py-2 rounded bg-[#d3d4dc] text-gray-800 font-medium focus:outline-none"
                        value={topic}
                        onChange={e => setTopic(e.target.value)}
                    >
                        <option value="">Topic</option>
                        {topics.map(t => (
                            <option value={t} key={t}>{t}</option>
                        ))}
                    </select>
                </div>

                <textarea
                    className="w-full min-h-[150px] rounded bg-[#d3d4dc] text-gray-800 px-3 py-2 mb-2 border-none focus:outline-none"
                    placeholder="Text"
                    value={text}
                    onChange={e => setText(e.target.value)}
                />

                <div className="flex items-center gap-2 mb-4">
                    <label className="flex-1">
                        <div className="text-xs text-gray-700 bg-white rounded-l px-3 py-2 border border-gray-300 flex items-center">
                            Join document (png, jpeg, pdf) max size : X mo
                        </div>
                        <input
                            ref={fileInput}
                            type="file"
                            className="hidden"
                            accept=".png,.jpeg,.jpg,.pdf"
                            onChange={e => setFile(e.target.files?.[0] || null)}
                        />
                    </label>
                    <button
                        type="button"
                        onClick={() => fileInput.current?.click()}
                        className="bg-[#d3d4dc] h-full px-2 rounded-r border border-gray-300"
                        title="Upload"
                    >
                        <svg width="20" height="20" fill="none" viewBox="0 0 24 24">
                            <path stroke="#434F5E" strokeWidth="2" d="M12 17V3m0 0l-4 4m4-4l4 4"/>
                            <rect width="20" height="2" y="19" x="2" fill="#434F5E" rx="1"/>
                        </svg>
                    </button>
                </div>

                <div className="flex justify-end">
                    <button
                        className="bg-gradient-to-r from-[#EA508E] to-[#F89BEB] text-white px-6 py-2 rounded-xl font-bold shadow transition hover:opacity-90"
                        onClick={() => alert("Ticket sent (simulé)")}
                    >
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
}
