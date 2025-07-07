import { useEffect, useImperativeHandle, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authedAPIRequest } from "../api/auth";

type Topic = {
    id: number,
    name: string
}
type addHistoryObj = {
    contentPath: string,
    resourcePath: string | null
}
// const topics: Topic[] = [
    // "Support",
    // "Payment",
    // "Technical issue",
    // "Account",
    // "Other"
// ];
const MAX_FILE_SIZE_MB = 5;



export default function CreateTicket() {
    const [object, setObject] = useState("");
    const [topicId, setTopicId] = useState(-1);
    const [selectedTopic, setSelectedTopic] = useState<string | null>(null);
    const [text, setText] = useState("");
    const [resourceFile, setFile] = useState<File | null>(null);
    const [topics, setTopics] = useState<Topic[]>([]);
    const [error, setError] = useState("");
    const fileInput = useRef<HTMLInputElement>(null);

    const navigate = useNavigate();
    useEffect(() => {
        loadTopics().then((topics: Topic[]) => {setTopics(topics)})
    })

    const loadTopics = async() =>
    {
        const topicCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/topics/all`,
            {
                method: 'GET',
            }
        );
        console.log(topicCall?.status);
        const topicsRes: Topic[] = await topicCall?.json();
        return topicsRes;
    }

    const rename = (filePath: string) => {
        const handledExtensions = [".pdf", ".png", ".jpeg", ".jpg"]
        // console.log(`filepath: ${filePath}`)
        for (const ext of handledExtensions)
        {
            // console.log(`filepath: ${filePath} ends with ${ext}? -> ${filePath.endsWith(ext)}`)
            if (filePath.endsWith(ext))
            {
                const splitted = filePath.split(ext);
                return `r0${ext}`;
            }
        }
        return "should not be reached";
    }
    // function renameFile(file: File): File {
    //     return new File([file], rename(file.name), {
    //         type: file.type,
    //         lastModified: file.lastModified,
    //     });
    // }


    const submitTicket = async () => {
        const resourcesFolder = "resources"
        const answersFolder = "answers"
        // TODO
        const createTicket = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({subject: object, topicId: topicId})
        })
        const ticketResponse = await createTicket?.json();
        await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${ticketResponse.id}/folders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({relativePath: `${answersFolder}`})
        })
        await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${ticketResponse.id}/folders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({relativePath: `${resourcesFolder}`})
        })
        await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${ticketResponse.id}/files`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({relativePath: `${answersFolder}/a0.txt`, content: text})
        })
        const addToHistory: addHistoryObj = {contentPath: `${answersFolder}/a0.txt`, resourcePath: null}
        if (resourceFile != null)
        {
            // const renamedFile = renameFile(resourceFile);
            const resourcePath = `${resourcesFolder}/${rename(resourceFile.name)}`
            const postResource = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${ticketResponse.id}/files/upload?path=${resourcePath}`, {
                method: 'POST', 
                headers: { 'Content-Type': 'application/octet-stream' },
                body: resourceFile
            })
            console.log(`posted resource got ${postResource?.status}`);
            const resPostedResource = await postResource?.json();
            addToHistory["resourcePath"] = resourcePath; 
            console.log(`response info: ${JSON.stringify(resPostedResource)}`);
        }
        const postHistory = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/${ticketResponse.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(addToHistory)
        })
        // const contentFile = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/${ticketResponse.id}/`)
    }

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

    return (
        <div className="w-screen min-h-screen bg-[#384454]">
            {/* HEADER */}
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
                        className={`w-1/2 px-3 py-2 rounded bg-[#d3d4dc] text-gray-800 font-medium focus:outline-none ${topics.length === 0 ? 'cursor-not-allowed' : ''}`}
                        value={topicId}
                        name={topics.length === 0 ? "Topics not available" : "Topic"}
                        disabled={topics.length === 0}
                        onChange={e => {
                            setTopicId(parseInt(e.target.value));
                            // const selected = topics.find(t => t.name === e.target.value)
                            // if (selected !== undefined)
                            // {
                            //     setTopic(selected.id);
                            // }
                        }
                        }>
                        <option value="none"
                                hidden>{topics.length === 0 ? "Topics not available" : "Select Topic"}</option>
                        {topics.map(t => (
                            <option value={t.id} key={t.id}>{t.name}</option>
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
                        <div
                            className="text-xs text-gray-700 bg-white rounded-l px-3 py-2 border border-gray-300 flex items-center">
                            Join document (png, jpeg, pdf) max size : 5 mo
                        </div>
                        <input
                            ref={fileInput}
                            type="file"
                            className="hidden"
                            accept=".png,.jpeg,.jpg,.pdf"
                            onChange={(e) => {
                                handleFileChange(e);
                            }
                            }
                        />
                    </label>
                    {resourceFile && (
                        <div className="flex items-center gap-2 text-xs text-gray-200">
                            <span className="text-gray-200">{resourceFile.name}</span>
                            <button
                                type="button"
                                onClick={() => {
                                    if (fileInput.current != null) {
                                        fileInput.current.value = ""
                                    }
                                    setFile(null)
                                }}
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
                        className="bg-gradient-to-r from-[#EA508E] to-[#F89BEB] text-white px-6 py-2 rounded-xl font-bold shadow transition hover:opacity-90"
                        onClick={() => {
                            if (object === "") {
                                setError("The object is empty")
                                setTimeout(() => setError(""), 5000)
                                return
                            }
                            if (topicId === -1) {
                                setError("No topic selected")
                                setTimeout(() => setError(""), 5000)
                                return
                            }
                            if (text === "") {
                                setError("No text written")
                                setTimeout(() => setError(""), 5000)
                                return
                            }
                            submitTicket()
                            navigate(`/my-tickets`)
                        }
                        }
                    >
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
}
