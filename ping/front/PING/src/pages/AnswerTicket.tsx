import { useParams, Link } from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import { authedAPIRequest } from "../api/auth";

const MAX_FILE_SIZE_MB = 5;


export default function AnswerTicket() {
    const { id } = useParams<{ id: string }>();
    const [ticket, setTicket] = useState<any>(null);
    const [text, setText] = useState("");
    const [file, setFile] = useState<File | null>(null);
    const [error, setError] = useState("");
    const fileInput = useRef<HTMLInputElement>(null);

    const fetchTicket = async () => {
        console.log("Fetching ticket with id:", id);
        const response = await authedAPIRequest(
            `${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        );

        if (response?.ok) {
            const data = await response.json();
            console.log("Fetched ticket:", data);
            setTicket(data);
        } else {
            console.error("Erreur HTTP", response?.status);
        }
    };

    useEffect(() => {
        fetchTicket();
    }, [id]);

    console.log("Ticket avant set:", ticket);
    useEffect(() => {
        console.log("Ticket après set:", ticket);
    }, [ticket]);


    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFile = e.target.files?.[0];
        if (selectedFile) {
            const sizeInMB = selectedFile.size / (1024 * 1024);
            if (sizeInMB > MAX_FILE_SIZE_MB) {
                alert(`Le fichier est trop volumineux (${sizeInMB.toFixed(2)} Mo). Taille max : ${MAX_FILE_SIZE_MB} Mo.`);
                if (fileInput.current) fileInput.current.value = ""; // réinitialise le champ
                return;
            }
            setFile(selectedFile);
        }
    };


    /*const handleSubmit = async () => {
        if (!text) {
            alert("Le message ne peut pas être vide.");
            return;
        }

        const formData = new FormData();
        formData.append("text", text);
        if (file) {
            formData.append("file", file);
        }

        try {
            const res = await fetch(`https://TON_BACKEND_URL/api/tickets/${id}/answer`, {
                method: "POST",
                body: formData,
            });

            if (!res.ok) throw new Error("Échec de l'envoi");

            alert("Réponse envoyée !");
            setText("");
            setFile(null);
            if (fileInput.current) fileInput.current.value = "";
        } catch (err) {
            alert("Erreur lors de l'envoi de la réponse.");
            console.error(err);
        }
    };*/

    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
                    </div>
                </Link>
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
                <Link to="/profile">
                    <div className="flex items-center justify-center w-8 h-8 bg-white text-[#EA508E] rounded-full shadow-lg text-xl">
                        <span role="img" aria-label="profile">👤</span>
                    </div>
                </Link>
            </div>

            {/* FORM */}
            <div className="max-w-2xl mx-auto pt-4 pb-2 px-2">
                <button className="text-white text-xl mb-2 mt-4" onClick={() => window.history.back()}>
                    <span className="text-2xl mr-2">&#8592;</span>
                </button>
                <h1 className="text-white text-3xl font-bold text-center mb-6">Reply to Ticket</h1>

                <div className="flex gap-2 mb-2">
                    <input
                        type="text"
                        value={ticket?.name || ""}
                        disabled
                        className="w-1/2 px-3 py-2 rounded bg-gray-300 text-gray-600 font-semibold"
                    />
                    <input
                        type="text"
                        value={ticket?.topic.name || ""}
                        disabled
                        className="w-1/2 px-3 py-2 rounded bg-gray-300 text-gray-600 font-semibold"
                    />
                </div>

                <textarea
                    className="w-full min-h-[150px] rounded bg-[#d3d4dc] text-gray-800 px-3 py-2 mb-2 border-none focus:outline-none"
                    placeholder="Your message"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                />

                <div className="flex items-center gap-2 mb-4">
                    <label className="flex-1">
                        <div className="text-xs text-gray-700 bg-white rounded-l px-3 py-2 border border-gray-300 flex items-center">
                            Join document (png, jpeg, pdf) max size : 5 mo
                        </div>
                        <input
                            ref={fileInput}
                            type="file"
                            className="hidden"
                            accept=".png,.jpeg,.jpg,.pdf"
                            onChange={handleFileChange}
                        />
                    </label>
                    {file && (
                        <div className="flex items-center gap-2 text-xs text-gray-200">
                            <span className="text-gray-200">{file.name}</span>
                            <button
                                type="button"
                                onClick={() =>                                         
                                    {
                                        if (fileInput.current != null)
                                        {
                                            fileInput.current.value=""
                                        }
                                        setFile(null)
                                    }
                                }
                                className="ml-1 px-1 rounded bg-[#EA508E] text-white hover:bg-pink-600"
                                title="Retirer le fichier"
                            >
                                ×
                            </button>
                        </div>
                    )}
                    
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
                    {error !== "" && <span className="text-red-500 mt-1 text-sm">{error}</span>}
                </div>

                <div className="flex justify-end">
                    <button
                        className="bg-gradient-to-r from-[#EA508E] to-[#F89BEB] text-white px-6 py-2 rounded-xl font-bold"
                        onClick={() => {
                            if (text === "")
                            {
                                setError("No text written")
                                setTimeout(() => setError(""), 5000)
                                return
                            }
                        }}
                    >
                        Send Answer
                    </button>
                </div>
            </div>
        </div>
    );
}
