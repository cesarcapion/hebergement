import { useState } from "react";
import {Link, useNavigate} from "react-router-dom";

// Juste des exemples pour l'affichage
const tickets = [
    { id: 58562, title: "ticket 58562", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "in progress" },
    { id: 22254, title: "ticket 22254", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "resolved" },
    { id: 15245, title: "ticket 15245", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "pending" },
    { id: 8956, title: "ticket 8956", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "pending" },
    { id: 589, title: "ticket 589", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "resolved" },
];

const statusOrder = ["in progress", "pending", "resolved"];

const statusColors: Record<string, string> = {
    pending: "bg-green-400",
    "in progress": "bg-yellow-400",
    resolved: "bg-red-400",
};

export default function MyTickets() {
    const [filter, setFilter] = useState("");
    const [sort, setSort] = useState("");
    const navigate = useNavigate();


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
            <div className="bg-[#FFD068] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10"/>
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

            {/* FILTRES */}
            <div className="flex items-center justify-between max-w-3xl mx-auto pt-4 pb-2">
                <select
                    className="bg-white rounded p-2 text-gray-700 filter"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                >
                    <option value="">Filter</option>
                    <option value="pending">Pending</option>
                    <option value="in progress">In progress</option>
                    <option value="resolved">Resolved</option>
                </select>

                <h1 className="text-white text-3xl font-bold text-center">My tickets</h1>

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

            {/* LISTE DES TICKETS */}
            <div className="max-w-3xl mx-auto">
                {sortedTickets.map((ticket) => (
                    <div
                        key={ticket.id}
                        className="flex items-center gap-3 mb-3 px-3 py-3 rounded bg-[#434F5E] hover:bg-[#4f5d6f] cursor-pointer"
                        onClick={() => navigate(`/my-tickets/${ticket.id}`)}
                    >
                        <div className="w-1/5 font-semibold text-white">{ticket.title}</div>
                        <div className="flex-1 text-sm text-gray-200 truncate">{ticket.desc}</div>
                        <div>
              <span className={`px-4 py-1 rounded-full font-bold text-xs text-white ${statusColors[ticket.status]}`}>
                {ticket.status}
              </span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
