import React, { useState, useEffect } from "react";
import { authedAPIRequest } from "../api/auth.tsx";
import {Link} from "react-router-dom";

const ManagePage: React.FC = () => {
    const [roleName, setRoleName] = useState("");
    const [roleTopic, setRoleTopic] = useState("");
    const [employeeEmail, setEmployeeEmail] = useState("");
    const [employeeRole, setEmployeeRole] = useState("");
    const [removeRole, setRemoveRole] = useState('Role');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    type Role = {
        id: string;
        name: string;
        topics: string[];
    };
    type Topic ={
        id: string;
        name: string;
        role: string[];
    };

    const [roles, setRoles] = useState<Role[]>([]);
    const [topics, setTopics] = useState<Topic[]>([]);

    const fetchRoles = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/roles/all`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch roles");
            const data = await response.json();
            setRoles(data);
        } catch (err) {
            console.error(err);
            setError("Failed to fetch roles");
        }
        try {
            const token = localStorage.getItem('token');
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/topics/all`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch topics");
            const data = await response.json();
            setTopics(data);
        } catch (err) {
            console.error(err);
            setError("Failed to fetch topics");
        }
    };

    const handleAddRole = async () => {
        if (!roleName.trim()) {
            setError('Please fill in role name');
            return;
        }
        const roleID = roleName.trim();
        try {
            const token = localStorage.getItem('token');
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/roles`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ name: roleName }),
            });

            if (response.status === 409) {
                setError('This role already exists');
                return;
            }

            if (!response.ok) {
                throw new Error('Failed to add role');
            }

            const data = await response.json();

            setSuccess('Role added successfully');
            setRoles([...roles, { id: data.id || Date.now().toString(), name: roleName, topics: [roleTopic] }]);
            setRoleName('');
            setRoleTopic('');
        } catch (err) {
            console.error(err);
            setError("Error adding role");
        }
        try {
            const token = localStorage.getItem('token');
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/roles/all`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch roles");
            const data = await response.json();
            setRoles(data);
        } catch (err) {
            console.error(err);
            setError("Failed to fetch roles");
        }
    };
    const handleDelTopic = async () => {
        if (!roleName.trim() || !roleTopic.trim()) {
            setError('Please fill in both role name and topic');
            return;
        }

        const existingRole = roles.find((role) => {
            const roleLower = role.name.toLowerCase().trim();
            const searchLower = roleName.toLowerCase().trim();

            console.log(`Comparaison: "${roleLower}" === "${searchLower}"`);
            return roleLower === searchLower;
        });
        if (!existingRole) {
            setError("This role does not exist");
            return;
        }

        const existingTopic = topics.find((topic) => topic.name === roleTopic.trim());
        if (!existingTopic) {
            setError("This topic does not exist");
            return;
        }

        try {
            const token = localStorage.getItem('token');

            const response = await authedAPIRequest(
                `${import.meta.env.VITE_SERVER_URL}/api/roles/${existingRole.id}/remove-topic`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify({ topicId: existingTopic.id }),
                }
            );

            if (response.status === 404) {
                setError('Role or Topic not found');
                return;
            }
            if (response.status === 405) {
                setError('Role is read-only, cannot remove topic');
                return;
            }
            if (response.status === 409) {
                setError('Topic is not assigned to this role');
                return;
            }
            if (!response.ok) {
                throw new Error('Failed to remove topic');
            }

            // Mise à jour du state local
            const updatedRoles = roles.map((role) =>
                role.id === existingRole.id
                    ? { ...role, topics: role.topics.filter((topic) => topic !== roleTopic) }
                    : role
            );

            setRoles(updatedRoles);
            setSuccess('Topic removed from role successfully');
            setRoleTopic('');
        } catch (err) {
            console.error(err);
            setError("Error removing topic from role");
        }
    };

    const handleAddTopic = async () => {
        if (!roleName.trim() || !roleTopic.trim()) {
            setError('Please fill in both role name and topic');
            return;
        }

        const existingRole = roles.find((role) => role.name.toLowerCase() === roleName.toLowerCase());
        if (!existingRole) {
            console.log(existingRole);
            setError("This role does not exist");
            return;
        }

        try {
            const token = localStorage.getItem('token');

            // 1. Create topic if it doesn't exist (assumes API handles duplication or throws 409)
            const createTopicRes = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/topics`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ name: roleTopic }),
            });

            if (createTopicRes.status === 409) {
                // Topic already exists, continue
            } else if (!createTopicRes.ok) {
                throw new Error('Failed to create topic');
            }

            // 2. Fetch updated topics list
            let updatedTopics;
            try {
                const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/topics/all`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                });
                if (!response.ok) throw new Error("Failed to fetch topics");
                updatedTopics = await response.json();
                setTopics(updatedTopics);
            } catch (err) {
                console.error(err);
                setError("Failed to fetch topics");
                return;
            }

            // 3. Find the topic in the updated list
            const existingTopic = updatedTopics.find((topic) => topic.name.toLowerCase() === roleTopic.trim().toLowerCase());

            if (!existingTopic) {
                setError("Topic not found after creation");
                return;
            }

            // 4. Assign topic to role
            const assignRes = await authedAPIRequest(
                `${import.meta.env.VITE_SERVER_URL}/api/roles/${existingRole.id}/add-topic`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify({ topicId: existingTopic.id }),
                }
            );

            if (!assignRes.ok) {
                throw new Error('Failed to assign topic to role');
            }

            // 5. Update local state
            const updatedRoles = roles.map((role) =>
                role.id === existingRole.id
                    ? { ...role, topics: [...(role.topics || []), roleTopic] }
                    : role
            );

            setRoles(updatedRoles);
            setSuccess('Topic added to role successfully');
            setRoleTopic('');
        } catch (err) {
            console.error(err);
            setError("Error adding topic to role");
        }
    };

    const handleAddEmployee = async () => {
        if (!employeeEmail.trim() || !employeeRole.trim()) {
            setError('Please fill in both email and role');
            return;
        }

        const selectedRole = roles.find((role) => role.name === employeeRole.trim());

        if (!selectedRole) {
            setError("Selected role not found");
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await authedAPIRequest(
                `${import.meta.env.VITE_SERVER_URL}/api/user/role`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify({ mail : employeeEmail,id: selectedRole.id }),
                }
            );

            if (!response.ok) {
                if (response.status === 404) {
                    setError("User not found");
                } else {
                    setError("Failed to assign role to employee");
                }
                return;
            }

            setSuccess('Role successfully assigned to employee');
            setEmployeeEmail('');
            setEmployeeRole('');
        } catch (err) {
            console.error(err);
            setError("Error assigning role to employee");
        }
    };


    const handleRemoveEmployee = async () => {
        if (!employeeEmail.trim() || !employeeRole.trim()) {
            setError('Please fill in both email and role');
            return;
        }

        try {
            // Replace this mock call with a real API call
            setSuccess('Employee removed successfully');
            setEmployeeEmail('');
            setEmployeeRole('');
        } catch (err) {
            console.error(err);
            setError("Error removing employee");
        }
    };

    const handleRemoveRole = async () => {
        console.log(removeRole);
        if (removeRole.match("Role")) {
            setError('Please provide a role name to delete');
            return;
        }

        const roleToDelete = roles.find((role) => role.name === removeRole.trim());

        if (!roleToDelete) {
            setError('Role does not exist');
            return;
        }

        try {
            const token = localStorage.getItem('token');

            const response = await authedAPIRequest(
                `${import.meta.env.VITE_SERVER_URL}/api/roles/${roleToDelete.id}`,
                {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                }
            );

            if (response.status === 404) {
                setError('Role not found');
                return;
            }

            if (response.status === 405) {
                const data = await response.json();
                setError(data?.message || 'Role cannot be deleted (read-only or used by users)');
                return;
            }

            if (!response.ok) {
                throw new Error('Failed to delete role');
            }

            // Remove from local state
            const updatedRoles = roles.filter((role) => role.id !== roleToDelete.id);
            setRoles(updatedRoles);

            setSuccess('Role deleted successfully');
            setRoleName('');
        } catch (err) {
            console.error(err);
            setError('Error deleting role');
        }
    };


    useEffect(() => {
        fetchRoles();
    }, []);

    useEffect(() => {
        if (error || success) {
            const timer = setTimeout(() => {
                setError('');
                setSuccess('');
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [error, success]);

    return (
        <div style={styles.body}>
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/admin">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
                    </div>
                </Link>

                <div className="flex gap-4">
                    <Link to="/qa/admin">
                        <button className="btn">
                            Q&A
                        </button>
                    </Link>
                    <Link to="/my-tickets/admin">
                        <button className="btn">
                            Inbox
                        </button>
                    </Link>
                </div>

                <div className="flex items-center">
                    <Link
                        className="flex items-center justify-center w-10 h-10 bg-white text-[#EA508E] rounded-full shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
                        to="/profile/admin"
                        title="Mon Profil"
                    >
                        <span className="text-lg">👤</span>
                    </Link>
                </div>
            </div>

            <div style={styles.content}>
                <button onClick={() => window.history.back()} style={styles.backButton}>
                    <span style={styles.backArrow}>←</span>
                </button>

                <h1 style={styles.h1}>Manage</h1>

                {error && <p className="text-sm text-red-600 text-center mb-4">{error}</p>}
                {success && <p className="text-sm text-green-600 text-center mb-4">{success}</p>}

                {/* New Role Section */}
                <div style={styles.section}>
                    <h2 style={styles.sectionTitle}>New role</h2>
                    <div style={styles.roleForm}>
                        <div style={styles.inputGroup}>
                            <input
                                type="text"
                                placeholder="Name of the role"
                                value={roleName}
                                onChange={(e) => setRoleName(e.target.value)}
                                style={styles.input}
                            />
                            <div style={styles.topicContainer}>
                                <input
                                    type="text"
                                    placeholder="Topic"
                                    value={roleTopic}
                                    onChange={(e) => setRoleTopic(e.target.value)}
                                    style={styles.input}
                                />
                                <button style={styles.addTopicBtn} onClick={handleAddTopic}>+</button>
                                <button style={styles.delTopicBtn} onClick={handleDelTopic}>-</button>
                            </div>
                        </div>
                        <div style={styles.buttonContainer}>
                            <button style={styles.btnPrimary} onClick={handleAddRole}>
                                Add
                            </button>
                        </div>
                    </div>
                </div>

                <div style={styles.section}>
                    <h2 style={styles.sectionTitle}>Change a user's role</h2>
                    <div style={styles.employeeForm}>
                        <div style={styles.inputGroup}>
                            <input
                                type="email"
                                placeholder="Email"
                                value={employeeEmail}
                                onChange={(e) => setEmployeeEmail(e.target.value)}
                                style={styles.input}
                            />
                            <select
                                value={employeeRole}
                                onChange={(e) => setEmployeeRole(e.target.value)}
                                style={styles.select}
                            >
                                <option value="">Role</option>
                                {roles.map((role, index) => (
                                    <option key={index} value={role.name}>{role.name}</option>
                                ))}
                            </select>
                        </div>
                        <div style={styles.buttonContainer}>
                            <button style={styles.btnPrimary} onClick={handleAddEmployee}>
                                Change
                            </button>
                        </div>
                    </div>
                </div>

                {/* Remove Role Section */}
                <div style={styles.section}>
                    <div style={styles.removeRoleForm}>
                        <select
                            value={removeRole}
                            onChange={(e) => setRemoveRole(e.target.value)}
                            style={styles.select}
                        >
                            <option value="">Role</option>
                            {roles.map((role, index) => (
                                <option key={index} value={role.name}>{role.name}</option>
                            ))}
                        </select>
                        <button style={styles.btnSecondary} onClick={handleRemoveRole}>
                            Remove
                        </button>
                    </div>
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
    content: {
        padding: "30px 20px",
        maxWidth: "900px",
        margin: "auto",
        position: "relative",
    },
    backButton: {
        position: "absolute",
        top: "30px",
        left: "20px",
        color: "white",
        textDecoration: "none",
        fontSize: "24px",
        fontWeight: "bold",
    },
    backArrow: {
        fontSize: "32px",
    },
    h1: {
        textAlign: "center",
        fontSize: "48px",
        marginBottom: "40px",
        fontWeight: "bold",
    },
    section: {
        marginBottom: "40px",
    },
    sectionTitle: {
        fontSize: "24px",
        marginBottom: "20px",
        fontWeight: "bold",
        textAlign: "left",
    },
    roleForm: {
        display: "flex",
        flexDirection: "column",
        gap: "20px",
    },
    employeeForm: {
        display: "flex",
        flexDirection: "column",
        gap: "20px",
    },
    removeRoleForm: {
        display: "flex",
        gap: "20px",
        alignItems: "center",
        justifyContent: "center",
        flexWrap: "wrap",
    },
    inputGroup: {
        display: "flex",
        gap: "20px",
        alignItems: "center",
        flexWrap: "wrap",
    },
    topicContainer: {
        display: "flex",
        alignItems: "center",
        gap: "10px",
    },
    input: {
        padding: "12px",
        borderRadius: "8px",
        border: "none",
        backgroundColor: "#d3d4dc",
        fontSize: "14px",
        minWidth: "200px",
        flex: 1,
    },
    select: {
        padding: "12px",
        borderRadius: "8px",
        border: "none",
        backgroundColor: "#d3d4dc",
        fontSize: "14px",
        minWidth: "150px",
        cursor: "pointer",
    },
    addTopicBtn: {
        width: "40px",
        height: "40px",
        borderRadius: "8px",
        backgroundColor: "#d3d4dc",
        border: "none",
        fontSize: "20px",
        fontWeight: "bold",
        cursor: "pointer",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
    },
    delTopicBtn: {
        width: "40px",
        height: "40px",
        borderRadius: "8px",
        backgroundColor: "#d3d4dc",
        border: "none",
        fontSize: "20px",
        fontWeight: "bold",
        cursor: "pointer",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
    },
    buttonContainer: {
        display: "flex",
        gap: "20px",
        justifyContent: "center",
        flexWrap: "wrap",
    },
    btnPrimary: {
        background: "linear-gradient(to bottom, #f472b6, #d946ef)",
        color: "white",
        fontWeight: "bold",
        padding: "12px 40px",
        borderRadius: "25px",
        border: "none",
        fontSize: "16px",
        cursor: "pointer",
        minWidth: "100px",
    },
    btnSecondary: {
        background: "linear-gradient(to bottom, #f472b6, #d946ef)",
        color: "white",
        fontWeight: "bold",
        padding: "12px 40px",
        borderRadius: "25px",
        border: "none",
        fontSize: "16px",
        cursor: "pointer",
        minWidth: "100px",
    },
};

export default ManagePage;
