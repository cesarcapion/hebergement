import { useParams, Link } from "react-router-dom";

const dummyMessages = [
    {
        sender: "You",
        date: "2025-06-22 05:03:07 pm",
        text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod...",
        filePath: "",
    },
    {
        sender: "Nicolas",
        date: "2025-06-22 05:47:21 pm",
        text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
        filePath: "White-Logo-without-bg.png",
    },
    {
        sender: "You",
        date: "2025-06-24 08:35:09 pm",
        text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod...",
        filePath: "../src/pages/TicketDiscussion.tsx",
    },
];

export default function TicketDiscussion() {
    const { id } = useParams();

    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER identique à la liste */}
            <div className="bg-[#FFD068] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-auto"/>
                    </div></Link>
                <div className="flex gap-6">
                    <Link to="/qa">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white text-2xl px-8 py-2 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            Q&amp;A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white text-2xl px-8 py-2 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">My tickets</button>
                    </Link>
                </div>
                <Link to="/profile">
                    <div className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#842D50] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
                        <span role="img" aria-label="profile" className="text-2xl">👤</span>
                    </div>
                </Link>
            </div>

            {/* TICKET HEADER */}
            <div className="max-w-3xl mx-auto py-6">
                <div className="flex items-center gap-4 mb-4">
                    <Link to="/my-tickets">
                        <button className="text-white text-2xl">&larr;</button>
                    </Link>
                    <h2 className="text-2xl font-bold text-white mx-auto">Ticket {id}</h2>
                </div>
                <div className="space-y-4">
                    {dummyMessages.map((msg, i) => (
                        <div key={i} className="flex items-start">
                            <div className="w-20 text-[#fff] font-semibold">{msg.sender}</div>
                            <div className="w-36 text-xs text-gray-200">{msg.date}</div>
                            <div className="flex-1 text-sm text-gray-100 bg-[#434F5E] p-2 rounded ml-2">{msg.text}</div>
                            {msg.filePath && (
                                <div className="mt-2">
                                    <a
                                    href={msg.filePath}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    download
                                    className="inline-block bg-pink-400 text-white text-xs font-semibold px-3 py-1 rounded hover:bg-pink-500">
                                        Download file
                                    </a>
                                </div>
                            )}
                            {i === dummyMessages.length - 1 && (
                                <Link to={`/answer/${id}`}>
                                    <button className="ml-4 px-6 py-1 rounded bg-[#F89BEB] text-white text-sm font-bold">answer</button>
                                </Link>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
