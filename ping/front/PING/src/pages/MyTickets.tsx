import { useEffect, useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import { authedAPIRequest } from "../api/auth";
import { formatDate, statusColors, stringToSortingStrategy, stringToTicketStatus, ticketStatusToString, type sortingStrategy, type ticket, type ticketStatus } from "../utils/Ticket";

// Juste des exemples pour l'affichage


// const tickets_test: ticket_test[] = [
//     { id: 58562, title: "ticket 58562", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status:  "IN_PROGRESS"},
//     { id: 22254, title: "ticket 22254", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "RESOLVED" },
//     { id: 15245, title: "ticket 15245", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "PENDING" },
//     { id: 8956, title: "ticket 8956", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "PENDING" },
//     { id: 589, title: "ticket 589", desc: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...", status: "RESOLVED" },
// ];




function getSortingStrategyByString(name: string) : sortingStrategy
{
    const output: undefined | sortingStrategy = stringToSortingStrategy.get(name);
    return output === undefined ? "NONE" : output;
}

function getFilterbyString(name: string) : ticketStatus
{
    const output : undefined | ticketStatus = stringToTicketStatus.get(name);
    return output === undefined ? "NONE" : output;
}

function getStringbyFilter(status: ticketStatus): string 
{
    const output = ticketStatusToString.get(status);
    return output === undefined ? "none" : output;
}

export default function MyTickets() {
    const [tickets, setTickets] = useState<ticket[]>([])
    const [filter, setFilter] = useState<string>("");
    const [sort, setSort] = useState("");
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [descending, setDescending] = useState(false);
    const [byStatus, setByStatus] = useState<ticketStatus>("NONE");
    // use a button on off to tell reversed or not

    useEffect(() => {
        loadMyTickets().then((tickets: ticket[]) => setTickets(tickets));
    })

    const loadMyTickets = async() =>
    {
        const getMyTicketsCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets?descending=${descending}&filter=${getFilterbyString(filter)}&sorting=${getSortingStrategyByString(sort)}`
            ,// ?descending=${descending}&filter=${getFilterbyString(filter)}`,
            {
                method: 'GET',
            }
        );
        // console.log(getMyTicketsCall?.status);
        const topicsRes: ticket[] = await getMyTicketsCall?.json();
        return topicsRes;
    }

    const filteredTickets = () => {
        const realFilter : ticketStatus = getFilterbyString(filter);
        return realFilter !== "NONE"
                ? tickets.filter((t) => t.status === filter)
                : tickets;
    }

    return (
        <div className="w-screen h-screen bg-[#384454]">
            {/* HEADER */}
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

            {/* FILTRES */}
            <div className="flex items-center justify-between max-w-3xl mx-auto pt-4 pb-2">
                <select
                    className="bg-white rounded p-2 text-gray-700 filter"
                    value={filter}
                    onChange={(e) => {
                        const v = e.target.value
                        setFilter(v === "none" ? "" : v)
                    }}
                >
                    <option value="" disabled hidden>Filter</option>
                    <option value="none">None</option>
                    <option value="pending">Pending</option>
                    <option value="in progress">In progress</option>
                    <option value="resolved">Resolved</option>
                </select>

                <h1 className="text-white text-3xl font-bold text-center">My tickets</h1>
                {/* <label className="inline-flex items-center cursor-pointer">
                    <span className=" text-white">reversed</span>
                    <div className="w-11 h-6 bg-gray-300 rounded-full peer peer-checked:bg-green-500 transition duration-300 relative">
                        <div className="w-5 h-5 bg-white rounded-full absolute top-0.5 left-0.5 peer-checked:translate-x-5 transition-transform" />
                    </div>
                    <input
                        type="checkbox"
                        className="ml-2 *:sr-only peer"
                        checked={descending}
                        onChange={() => setDescending(!descending)}
                    />
                </label> */}
                <select
                    className="bg-white rounded p-2 text-gray-700"
                    value={sort}
                    onChange={(e) => {
                        const v = e.target.value
                        setSort(v === "none" ? "" : v)
                    }}
                >
                    <option value="" disabled hidden>Sort</option>
                    <option value="none">None</option>
                    <option value="last modified">Last Modified</option>
                    <option value="status">Ticket Status</option>
                </select>

            </div>

            {/* LISTE DES TICKETS */}
            <div className="max-w-3xl mx-auto">
                {tickets.length === 0 && 
                <div className="mt-10 text-center text-gray-300 text-lg bg-[#2e3743] px-6 py-8 rounded-xl shadow">
                    🎫 You have not any tickets at the moment.
                </div>}
                {tickets.map((ticket) => (
                    <div
                    key={ticket.id}
                    className="flex items-center justify-between gap-4 mb-3 px-4 py-3 rounded bg-[#434F5E] hover:bg-[#4f5d6f] cursor-pointer"
                    onClick={() => navigate(`/my-tickets/${ticket.id}`)}
                    >
                    {/* Left: Date */}
                    <div className="text-sm text-gray-300 w-40 whitespace-nowrap">
                        {formatDate(ticket.lastModified)}
                    </div>

                    {/* Center: topic and name side by side */}
                    <div className="flex items-center justify-between w-full gap-4">
                    <div className="flex justify-center flex-1 text-sm text-gray-200">
                        {ticket.name}
                    </div>
                    <div className="font-semibold text-white whitespace-nowrap">
                        {ticket.topic.name}
                    </div>
                    </div>

                    {/* Right: Status */}
                    <div className="ml-4">
                        <span className={`px-4 py-1 rounded-full font-bold text-xs text-white whitespace-nowrap ${statusColors[ticket.status]}`}>
                        {getStringbyFilter(ticket.status)}
                        </span>
                    </div>
                </div>
                ))}
            </div>
        </div>
    );
}
