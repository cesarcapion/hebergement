import { useParams, Link } from "react-router-dom";
import {authedAPIRequest} from "../api/auth.tsx";
import {useEffect, useState} from "react";
import {formatDate, getTicketStatus, handleDownload, loadFileFromTicket, loadTicketHistory, loadUserFromId, type history, type ticketStatus} from "../utils/Ticket.ts"
import {jwtDecode} from "jwt-decode";
import { getUserGroupFromToken } from "../AdminRoute.tsx";


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
        filePath: "",
    },
    {
        sender: "You",
        date: "2025-06-24 08:35:09 pm",
        text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod...",
        filePath: "../src/pages/TicketDiscussionAdmin.tsx",
    },
];
type tokenPayload = 
{
    sub: string,
    groups: string[],
    iss: string,
    iat: Date,
    exp: Date,
    jti: string,
}

type userinfo =
    {
        id: string;
        displayName: string;
        avatar: string;
    }

type Topic = {
    id: string;
    name: string;
};

const token = localStorage.getItem("token");
let decoded: tokenPayload;
try {
  decoded = jwtDecode<tokenPayload>(token == null ? "" : token);
//   console.log(decoded.sub, decoded.exp);
} catch (e) {
  console.error("Invalid token", e);
}

export default function TicketDiscussionAdmin() {
    const group = getUserGroupFromToken();
    const { id } = useParams();
    const [topics, setTopics] = useState<Topic[]>([]);
    const [showTopics, setShowTopics] = useState(false);
    const [selectedTopic, setSelectedTopic] = useState("");
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
                console.log(`heeein mais c quoi ce status ${ticketStatus}`)
            });
        }, 2000);
        return () => clearInterval(interval);
        }, []);

    const fetchTopics = async () => {
        const res = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/topics/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        console.log("Topics:", res);
        const data = await res?.json();
        setTopics(data || []);
    };


    const handleRedirectClick = async () => {
        if (!showTopics) {
            await fetchTopics();
        }
        setShowTopics(!showTopics);
    };


    const handleTopicChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selected = e.target.value;
        setSelectedTopic(selected);
        console.log("Selected topic ID:", selected);
    }

    const getAllUserIds = async () => {
        const res = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        const ticketInfo = await res?.json();
        const membersList = ticketInfo.members as userinfo[];
        const listWithoutFirst: string[] = [];
        //let localNewOwnerId: string | null = null;

        for (const member of membersList as userinfo[]) {
            let isGoodTopic = false;
            const res = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/user/${member.id}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            const userInfoGet = await res?.json();
            const roleId = userInfoGet.roleId;
            const roleRes = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/roles/${roleId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            const roleInfoGet = await roleRes?.json();
            const topics = roleInfoGet.topics as Topic[];
            for (const topic of topics) {
                if (String(topic.id) === selectedTopic.trim()) {
                    isGoodTopic = true;
                }
            }
            console.log(selectedTopic);
            if (userInfoGet.role !== "user" && !isGoodTopic) {

                listWithoutFirst.push(member.id);
            }
            /*if (ticketInfo.owner.id !== userInfoGet.id && !listWithoutFirst.includes(member.id))
            {
                localNewOwnerId = userInfoGet.id;
            }*/
            isGoodTopic = false;
        }
        return { userIds: listWithoutFirst/*, ownerId: localNewOwnerId*/};

    }


    const removeAllUsers = async (ticketId: string | undefined, userIds: string[]) => {
        const token = localStorage.getItem("token");
        for (const userId of userIds) {
            const res = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/remove-user`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ userId })

            });
            if (res && res.ok) {
                if ("status" in res) {
                    console.warn(`Échec suppression pour ${userId} (status ${res.status})`);
                }
            }
        }
    };


    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER identique à la liste */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/admin">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10"/>
                    </div></Link>
                <div className="flex gap-6">
                    <Link to="/qa/admin">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl mr-2">
                            Q&amp;A
                        </button>
                    </Link>
                    <Link to="/my-tickets/admin">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl">Inbox</button>
                    </Link>
                </div>
                <Link to="/profile/admin">
                    <div className="flex items-center justify-center w-8 h-8 bg-white text-[#EA508E] rounded-full shadow-lg text-xl">
                        <span role="img" aria-label="profile">👤</span>
                    </div>
                </Link>
            </div>

            {/* TICKET HEADER */}
            <div className="max-w-3xl mx-auto py-6">

                <div className="flex items-center gap-4 mb-4">
                {group?.toString() === "admin" && (<Link to="/my-tickets/admin">
                        <button className="text-white text-2xl">&larr;</button>

                    </Link>)}
                {group?.toString() !== "admin" && (<Link to="/my-tickets/dev">
                        <button className="text-white text-2xl">&larr;</button>
                    </Link>)}
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
                                    {/* <a
                                    href={`http://localhost:8080/tmp/www/projects/${id}/${hist.resourcePath}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    download
                                    className="inline-block bg-pink-400 text-white text-xs font-semibold px-3 py-1 rounded hover:bg-pink-500">
                                        Download file
                                    </a> */}
                                </div>
                            )}
                            {i === ticketHistory.length - 1 && decoded.sub !== hist.interactedBy && ticketStatus !== "RESOLVED" && (
                                <Link to={`/answer/admin/${id}/${ticketHistory.length}`}>
                                    <button className="ml-4 px-6 py-1 rounded bg-[#F89BEB] text-white text-sm font-bold">answer</button>
                                </Link>
                            )}
                            {i === ticketHistory.length - 1 && ticketStatus !== "RESOLVED" && (
                                // <><Link to={`/answer/admin/${id}`}>
                                //     <button
                                //         className="ml-4 px-6 py-1 rounded bg-[#F89BEB] text-white text-sm font-bold">answer
                                //     </button>
                                // </Link>
                                <button
                                    onClick={handleRedirectClick}
                                    className="ml-4 px-6 py-1 rounded bg-[#F89BEB] text-white text-sm font-bold"
                                >
                                    redirect
                                </button>
                            )}
                            {showTopics && (
                                <div className="mt-2 ml-4">
                                    <select
                                        onChange={handleTopicChange}
                                        value={selectedTopic}
                                        className="px-4 py-2 rounded bg-white text-gray-800"
                                    >
                                        <option value="">Choose a topic</option>
                                        {topics.map((topic) => (
                                            <option key={topic.id} value={topic.id}>
                                                {topic.name}
                                            </option>
                                        ))}
                                    </select>

                                    <button
                                        onClick={async () => {
                                            if (!selectedTopic) {
                                                alert("Veuillez sélectionner un topic.");
                                                return;
                                            }

                                            const {userIds/*, ownerId*/} = await getAllUserIds();
                                            await removeAllUsers(id, userIds);

                                            const token = localStorage.getItem("token");
                                            console.log("topic:", selectedTopic);
                                            await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}`, {
                                                method: 'PUT',
                                                headers: {
                                                    'Content-Type': 'application/json',
                                                    Authorization: `Bearer ${token}`,
                                                },
                                                body: JSON.stringify({
                                                    // newOwnerId: ownerId,
                                                    ticketStatus: "PENDING",
                                                    newTopicId: selectedTopic,
                                                }),
                                            });
                                            /*await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/6d38b8e8-c1e6-4779-9e28-3824e1143992/leave`, {
                                                method: 'GET',
                                                headers: {
                                                    'Content-Type': 'application/json',
                                                }});*/
                                            setShowTopics(false);

                                        }}
                                        className="ml-4 px-6 py-1 rounded bg-green-600 text-white text-sm font-bold"
                                    >
                                        Confirmer
                                    </button>
                                </div>

                            )}
                        </div>
                    ))}
                    {ticketStatus === "RESOLVED" && 
                     <div className="mt-10 text-center text-gray-300 text-lg bg-[#2e3743] px-6 py-8 rounded-xl shadow">
                    🎫 This ticket has been closed
                    </div>}
                </div>
            </div>
        </div>
    );
}

