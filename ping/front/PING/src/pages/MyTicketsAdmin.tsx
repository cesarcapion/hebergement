import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

// Exemple de tickets
const initialTickets = [
    { id: 58562, title: "ticket 58562", desc: "Lorem ipsum...", status: "in progress" },
    { id: 22254, title: "ticket 22254", desc: "Lorem ipsum...", status: "resolved" },
    { id: 15245, title: "ticket 15245", desc: "Lorem ipsum...", status: "pending" },
    { id: 8956, title: "ticket 8956", desc: "Lorem ipsum...", status: "pending" },
    { id: 589, title: "ticket 589", desc: "Lorem ipsum...", status: "resolved" },
];

const statusOrder = ["in progress", "pending", "resolved"];


export default function AdminTickets() {
    const [tickets, setTickets] = useState(initialTickets);
    const [filter, setFilter] = useState("");
    const [sort, setSort] = useState("");
    const navigate = useNavigate();

    const handleStatusChange = (id: number, newStatus: string) => {
        setTickets((prev) =>
            prev.map((t) => (t.id === id ? { ...t, status: newStatus } : t))
        );
    };

    const filteredTickets = filter
        ? tickets.filter((t) => t.status === filter)
        : tickets;

    const sortedTickets = [...filteredTickets].sort((a, b) => {
        if (sort === "asc") return a.id - b.id;
        if (sort === "desc") return b.id - a.id;
        if (sort === "status") {
            return statusOrder.indexOf(a.status) - statusOrder.indexOf(b.status);
        }
        return 0;
    });

    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/admin">
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
                    <Link to="/my-tickets/admin">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl">
                            Inbox
                        </button>
                    </Link>
                </div>
                <Link to="/profile">
                    <div className="flex items-center justify-center w-8 h-8 bg-white text-[#EA508E] rounded-full shadow-lg text-xl">
                        <span role="img" aria-label="profile">👤</span>
                    </div>
                </Link>
            </div>

            {/* FILTRES */}
            <div className="flex items-center justify-between max-w-3xl mx-auto pt-4 pb-2">
                <select
                    className="bg-white rounded p-2 text-gray-700"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                >
                    <option value="">Filter</option>
                    <option value="pending">Pending</option>
                    <option value="in progress">In progress</option>
                    <option value="resolved">Resolved</option>
                </select>

                <h1 className="text-white text-3xl font-bold text-center">Inbox</h1>

                <select
                    className="bg-white rounded p-2 text-gray-700"
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                >
                    <option value="">Sort</option>
                    <option value="asc">ID asc</option>
                    <option value="desc">ID desc</option>
                    <option value="status">Status</option>
                </select>
            </div>

            {/* TICKETS */}
            <div className="max-w-3xl mx-auto">
                {sortedTickets.map((ticket) => (
                    <div
                        key={ticket.id}
                        className="flex items-center gap-3 mb-3 px-3 py-3 rounded bg-[#434F5E] hover:bg-[#4f5d6f] cursor-pointer"
                        onClick={() => navigate(`/my-tickets/admin/${ticket.id}`)}
                    >
                        <div className="w-1/5 font-semibold text-white">{ticket.title}</div>
                        <div className="flex-1 text-sm text-gray-200 truncate">{ticket.desc}</div>
                        <div onClick={(e) => e.stopPropagation()}>
                            <select
                                className="px-3 py-1 rounded-full font-bold text-xs text-white"
                                style={{
                                    backgroundColor:
                                        ticket.status === "pending"
                                            ? "#4ade80" // green-400
                                            : ticket.status === "in progress"
                                                ? "#facc15" // yellow-400
                                                : "#f87171", // red-400
                                }}
                                value={ticket.status}
                                onChange={(e) => handleStatusChange(ticket.id, e.target.value)}
                            >
                                <option
                                    value="in progress"
                                    style={{ backgroundColor: "#facc15", color: "white" }}
                                >
                                    in progress
                                </option>
                                <option
                                    value="pending"
                                    style={{ backgroundColor: "#4ade80", color: "white" }}
                                >
                                    pending
                                </option>
                                <option
                                    value="resolved"
                                    style={{ backgroundColor: "#f87171", color: "white" }}
                                >
                                    resolved
                                </option>
                            </select>

                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
