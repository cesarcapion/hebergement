import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { authedAPIRequest } from "../api/auth";
import {jwtDecode} from "jwt-decode";
import { loadTicketHistory, loadFileFromTicket, loadUserFromId, type ticketStatus, type history, handleDownload, getTicketStatus } from "../utils/Ticket";



type tokenPayload = 
{
    sub: string,
    groups: string[],
    iss: string,
    iat: Date,
    exp: Date,
    jti: string,
}

function formatDate(raw: string): string {
    const date = new Date(raw);
    return date.toLocaleString("en-GB", {
        day: "numeric",
        month: "long",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
    });
}

const token = localStorage.getItem("token");
let decoded: tokenPayload;
try {
  decoded = jwtDecode<tokenPayload>(token == null ? "" : token);
//   console.log(decoded.sub, decoded.exp);
} catch (e) {
  console.error("Invalid token", e);
}

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
    const [ticketHistory, setTicketHistory] = useState<history[]>([]);
    const [ticketInteractors, setTicketInteractors] = useState<string[]>([]);
    const [discussions, setDiscussions] = useState<string[]>([]);
    const [loaded, setLoaded] = useState<boolean>(false);
    const [ticketStatus, setTicketStatus] = useState<ticketStatus>("NONE")

    useEffect(() => {
        const interval = setInterval(() => {
            loadTicketHistory(id).then(async (ticketHistory: history[]) => 
            {
                setTicketHistory(ticketHistory);
                // const interactors: string[] = [];
                // const contentFiles: string[] = [];
                const interactorsPromises = ticketHistory.map(ticket =>
                    loadUserFromId(ticket.interactedBy)
                );
                const contentFilesPromises = ticketHistory.map(ticket =>
                    loadFileFromTicket(id, ticket.contentPath)
                );

                const interactors = await Promise.all(interactorsPromises);
                const contentFiles = await Promise.all(contentFilesPromises);

                setTicketInteractors(interactors);
                setDiscussions(contentFiles);
                setLoaded(true);

                // console.log(`discussions: ${discussions}`)
                // console.log(`fileContents: ${contentFiles}`)

                const status: ticketStatus = await getTicketStatus(id);
                setTicketStatus(status);
                
            });
        }, 2000);
        return () => clearInterval(interval);
    }, []);

    // const loadTicketHistory = async() =>
    // {
    //     const getTicketHistoryCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/${id}`
    //         ,// ?descending=${descending}&filter=${getFilterbyString(filter)}`,
    //         {
    //             method: 'GET',
    //         }
    //     );
    //     // console.log(getMyTicketsCall?.status);
    //     const historyRes: history[] = await getTicketHistoryCall?.json();
    //     return historyRes;
    // }

    // const loadUserFromId = async(userId: string) =>
    // {
    //     const getUserInfoCall : Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/user/${userId}`)
    //     const status: number | undefined = getUserInfoCall?.status;
    //     const getUserInfoRes = await getUserInfoCall?.json();
    //     return status === 404 ? "Deleted user" : getUserInfoRes.displayName;
    // }

    // const loadFileFromTicket = async(filePath: string) =>
    // {
    //     const getLoadFileCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/files?path=${filePath}`);
    //     const getLoadFileRes = await getLoadFileCall?.text();
    //     return getLoadFileRes === undefined ? "unknown content" : getLoadFileRes;
    // }

    


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
                    {ticketHistory.length === 0 && 
                        <div className="mt-10 text-center text-gray-300 text-lg bg-[#2e3743] px-6 py-8 rounded-xl shadow">
                            {loaded ? "🎫 Ticket {id} has no history at the moment." : "Loading..."}
                        </div>
                    }
                    {loaded && ticketHistory.map((hist, i) => (
                        <div key={i} className="flex items-start">
                            <div className="flex items-center space-x-2 mb-1">
                            <div className="w-20 text-white font-semibold">
                                {loaded ? (hist.interactedBy === decoded.sub ? "You" : ticketInteractors[i]) : "Loading..."}
                            </div>
                            <div className="w-36 text-xs text-gray-200 truncate whitespace-nowrap">
                                {loaded ? formatDate(hist.interactedOn) : "Loading..."}
                            </div>
                            <div className="flex-1 text-sm text-gray-100 bg-[#434F5E] p-2 rounded ml-2">
                                {loaded ? discussions[i] : "Loading..."}
                            </div>
                            </div>
                            {hist.resourcePath != null && (
                                <div className="mt-2">
                                    <button onClick={() => 
                                        {
                                            if (hist.resourcePath != null)
                                            {
                                                handleDownload(id, hist.resourcePath)
                                            }
                                        }}
                                    className="inline-block bg-pink-400 text-white text-xs font-semibold px-3 py-1 rounded hover:bg-pink-500">
                                        Download File
                                    </button>
                                </div>
                            )}
                            {i === ticketHistory.length - 1 && decoded.sub !== hist.interactedBy && ticketStatus !== "RESOLVED" && (
                                <Link to={`/answer/${id}/${ticketHistory.length}`}>
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
