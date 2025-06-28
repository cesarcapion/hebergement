import { useState } from "react";
import { Link } from "react-router-dom";

// Juste des exemples pour l'affichage
const tickets = [
    { id: 58562, title: "ticket 58562", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "in progress" },
    { id: 22254, title: "ticket 22254", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "resolved" },
    { id: 15245, title: "ticket 15245", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "pending" },
    { id: 8956, title: "ticket 8956", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "pending" },
    { id: 589, title: "ticket 589", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "resolved" },
];

const statusColors: Record<string, string> = {
    pending: "bg-green-400",
    "in progress": "bg-yellow-400",
    resolved: "bg-red-400",
};

export default function MyTickets() {
    const [filter, setFilter] = useState("");
    const [sort, setSort] = useState("");

    // Ici on conserve tous tes filtres/sorts, rien ne change
    const filteredTickets = (filter
            ? tickets.filter((t) => t.status === filter)
            : tickets
    ).sort((a, b) => {
        if (sort === "asc") return a.id - b.id;
        if (sort === "desc") return b.id - a.id;
        return 0;
    });

    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/">
                <div className="flex items-center gap-3">
                    <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10"/>
                </div></Link>
                <div className="flex gap-6">
                    <Link to="/qa">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl mr-2">
                            Q&amp;A
                        </button>
                    </Link>
                    <Link to="/my-tickets">
                        <button className="bg-[#F89BEB] text-white font-bold px-8 py-2 rounded-xl">My tickets</button>
                    </Link>
                </div>
                <Link to="/profile">
                    <div className="flex items-center justify-center w-8 h-8 bg-white text-[#EA508E] rounded-full shadow-lg text-xl">
                        <span role="img" aria-label="profile">👤</span>
                    </div>
                </Link>
            </div>

            {/* BARRE DE FILTRES */}
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
                <h1 className="text-white text-3xl font-bold text-center">My tickets</h1>
                <select
                    className="bg-white rounded p-2 text-gray-700"
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                >
                    <option value="">Sort</option>
                    <option value="asc">ID asc</option>
                    <option value="desc">ID desc</option>
                </select>
            </div>

            {/* LISTE DES TICKETS (modif ici : on wrap chaque ticket dans un <Link>) */}
            <div className="max-w-3xl mx-auto">
                {filteredTickets.map((ticket) => (
                    <Link
                        to={`/my-tickets/${ticket.id}`}
                        key={ticket.id}
                        style={{ textDecoration: "none" }}
                    >
                        <div className="flex items-center gap-3 mb-3 px-3 py-3 rounded bg-[#434F5E] hover:bg-[#576278] transition cursor-pointer">
                            <div className="w-1/5 font-semibold text-white">{ticket.title}</div>
                            <div className="flex-1 text-sm text-gray-200 truncate">{ticket.desc}</div>
                            <div>
                              <span className={`px-4 py-1 rounded-full font-bold text-xs text-white ${statusColors[ticket.status]}`}>
                                {ticket.status}
                              </span>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}
