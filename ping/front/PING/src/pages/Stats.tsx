import React, { useState } from "react";

const StatsPage: React.FC = () => {
    const [email, setEmail] = useState("");
    const [filterRange, setFilterRange] = useState("Last 5 days");
    const [bottomRange, setBottomRange] = useState("Last 5 days");

    return (
        <div style={styles.body}>
            <header style={styles.header}>
                <img
                    src="White-Logo-without-bg.png"
                    alt="Logo"
                    style={styles.logo as React.CSSProperties}
                />
                <div style={styles.headerButtons}>
                    <button style={styles.btnHeader}>Q&A</button>
                    <button style={styles.btnHeader}>Inbox</button>
                </div>
                <div style={styles.profileIcon}>👤</div>
            </header>

            <div style={styles.content}>
                <h1 style={styles.h1}>Stats</h1>

                <div style={styles.filters}>
                    <input
                        type="text"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                        <td>24</td>
                        <td>53</td>
                        <td>7</td>
                        <td>1d5h9m</td>
                    </tr>
                    </tbody>
                </table>

                <div style={styles.bottomSection}>
                    <button style={styles.btnBottom}>Show all employees</button>
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
