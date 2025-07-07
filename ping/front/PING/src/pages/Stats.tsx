import React, { useState, useEffect } from "react";
import {authedAPIRequest} from "../api/auth.tsx"
import {Link} from "react-router-dom";
const StatsPage: React.FC = () => {
    const [email, setEmail] = useState("");
    const [filterRange, setFilterRange] = useState<string>("Last 5 days");
    const [bottomRange, setBottomRange] = useState("Last 5 days");
    const [pending, setPending] = useState(0);
    const [resolved, setResolved] = useState(0);
    const [inProgress, setInProgress] = useState(0);
    const [error, setError] = useState('');
    const [allStats, setAllStats] = useState<any[]>([]);

    const [avgTime, setAvgTime] = useState("0d0h0m");
    function parseFilterRange(filterRange: string): number {
        const match = filterRange.match(/Last\s+(\d+)\s+days/i);
        if (match && match[1]) {
            return parseInt(match[1], 10);
        }
        return 5;
    }
    const handleAllStats = async () => {
        const days = parseFilterRange(bottomRange);
        const token = localStorage.getItem('token');

        const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/stats`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ mail: "menfou", days }),
        });

        if (response?.status == 200) {
            const data = await response.json();
            console.log("Received allStats:", data);
            setAllStats(data);
        } else {
            console.error("Failed to fetch all stats");
            setAllStats([]);
        }
    };
    const handleStats = async () => {
        const days = parseFilterRange(filterRange);

        console.log("Fetching stats for:", { email,days});
        const token = localStorage.getItem('token');

        const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/stat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ mail: email, days: days }),
        });
        setError('');
        if (response?.status === 400) {
            console.log("RESPONSE 400")
            setError('This email is from a user');
        }
        if (response?.status === 404) {
            console.log("RESPONSE 404")
            setError('Email not found');
        }
        const data = await response?.json();
        console.log(data);
     //   const data = await response.json();

     //   console.log(data);

        if (response?.status === 200) {
            setPending(data.PendingTickets);
            setResolved(data.ResolvedTickets);
            setInProgress(data.InProgressTickets);
            setAvgTime(data.AverageAnswerTime);
        }

    };

    const handleEmailKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            handleStats();
        }

    };

    useEffect(() => {
        handleStats();
    }, [filterRange]);

    return (
        <div style={styles.body}>
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

            <div style={styles.content}>
                <h1 style={styles.h1}>Stats</h1>

                <div style={styles.filters}>
                    <input
                        type="text"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        onKeyDown={handleEmailKeyDown}
                        style={styles.input}
                    />
                    <select
                        value={filterRange}
                        onChange={(e) => setFilterRange(e.target.value)}
                        style={styles.select}
                    >
                        <option>Last 5 days</option>
                        <option>Last 7 days</option>
                        <option>Last 30 days</option>
                    </select>
                </div>
                {error && (
                    <p className="text-sm text-red-600">{error}</p>
                )}
                <table style={styles.table}>
                    <thead>
                    <tr>
                        <th>Pending tickets</th>
                        <th>Resolved tickets</th>
                        <th>In progress tickets</th>
                        <th>Average time to answer a ticket</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>{pending}</td>
                        <td>{resolved}</td>
                        <td>{inProgress}</td>
                        <td>{avgTime}</td>
                    </tr>
                    </tbody>
                </table>

                <div style={styles.bottomSection}>
                    <button style={styles.btnBottom} onClick={handleAllStats}>
                        Show all employees
                    </button>
                    <select
                        value={bottomRange}
                        onChange={(e) => setBottomRange(e.target.value)}
                        style={styles.select}
                    >
                        <option>Last 5 days</option>
                        <option>Last 7 days</option>
                        <option>Last 30 days</option>
                    </select>
                </div>
                {allStats.length > 0 && (
                    <div style={{marginTop: "40px"}}>
                        <h2 style={styles.h1}>Stats by employee</h2>
                        {allStats.map((stat, idx) => (
                            <div key={idx} style={{marginBottom: "20px"}}>
                                <h3 style={{textAlign: "left", marginBottom: "10px"}}>{stat.mail}</h3>
                                <table style={styles.table}>
                                    <thead>
                                    <tr>
                                        <th>Pending</th>
                                        <th>Resolved</th>
                                        <th>In progress</th>
                                        <th>Average Time</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>{stat.PendingTickets}</td>
                                        <td>{stat.ResolvedTickets}</td>
                                        <td>{stat.InProgressTickets}</td>
                                        <td>{stat.AverageAnswerTime}</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        ))}
                    </div>
                )}

            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    body: {
        margin: 0,
        fontFamily: "Arial, sans-serif",
        backgroundColor: "#2f3a48",
        color: "white",
        minHeight: "100vh",
        width: "100vw",
    },
    header: {
        backgroundColor: "#FCD34D",
        padding: "10px 20px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
    },
    logo: {
        height: "40px",
    },
    headerButtons: {
        display: "flex",
        gap: "20px",
    },
    btnHeader: {
        background: "linear-gradient(to bottom, #f472b6, #d946ef)",
        color: "white",
        fontWeight: "bold",
        padding: "10px 30px",
        borderRadius: "14px",
        border: "none",
        boxShadow: "2px 5px 8px rgba(0, 0, 0, 0.2)",
        fontSize: "16px",
        cursor: "pointer",
    },
    profileIcon: {
        width: "32px",
        height: "32px",
        borderRadius: "50%",
        background: "linear-gradient(to bottom, #f472b6, #d946ef)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "18px",
    },
    content: {
        padding: "30px 20px",
        maxWidth: "900px",
        margin: "auto",
    },
    h1: {
        textAlign: "center",
        fontSize: "36px",
        marginBottom: "20px",
    },
    filters: {
        display: "flex",
        gap: "20px",
        justifyContent: "center",
        flexWrap: "wrap",
        marginBottom: "30px",
    },
    input: {
        padding: "10px",
        borderRadius: "6px",
        border: "none",
        backgroundColor: "#d3d4dc",
        fontSize: "14px",
    },
    select: {
        padding: "10px",
        borderRadius: "6px",
        border: "none",
        backgroundColor: "#d3d4dc",
        fontSize: "14px",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
        marginBottom: "30px",
        backgroundColor: "#e0e0e0",
        color: "black",
        textAlign: "center",
    },
    bottomSection: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        gap: "20px",
    },
    btnBottom: {
        background: "linear-gradient(to bottom, #f472b6, #d946ef)",
        color: "white",
        fontWeight: "bold",
        padding: "10px 30px",
        borderRadius: "14px",
        border: "none",
        fontSize: "16px",
        cursor: "pointer",
    },
};

export default StatsPage;
