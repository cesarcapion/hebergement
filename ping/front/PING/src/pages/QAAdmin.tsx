"use client"

import { useState, useEffect } from "react"
import { Link } from "react-router-dom"
import {authedAPIRequest} from "../api/auth.tsx";
import { getUserGroupFromToken } from "../AdminRoute.tsx";

interface FAQ {
    id: number
    question: string
    answer: string
}

interface FAQResponse {
    id: number
    question: string
    response: string
    // Ajoutez d'autres champs selon votre structure
}

interface FAQRequest {
    id?: number
    question: string
    response: string
    // Ajoutez d'autres champs selon votre structure
}

const defaultFaqs: FAQ[] = [
    {
        id: 1,
        question: "Contact us",
        answer:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    },
    {
        id: 2,
        question: "Contact us",
        answer:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    },
    {
        id: 3,
        question: "Contact us",
        answer:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    },
    {
        id: 4,
        question: "Contact us",
        answer:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    },
]

const QAAdmin = () => {
    const group = getUserGroupFromToken();
    const [faqs, setFaqs] = useState<FAQ[]>(defaultFaqs)
    const [openIndex, setOpenIndex] = useState<number | null>(null)
    const [isEditMode, setIsEditMode] = useState(false)
    const [editingFaq, setEditingFaq] = useState<FAQ | null>(null)
    const [showAddFaq, setShowAddFaq] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [operationLoading, setOperationLoading] = useState(false)
    const [formData, setFormData] = useState({
        question: "",
        answer: "",
    })

    // Fonction pour charger les FAQs depuis l'API
    const fetchFAQs = async () => {
        setLoading(true)
        setError(null)

        try {
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/FAQ`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                const faqData: FAQResponse[] = await response.json();

                const transformedFaqs: FAQ[] = faqData.map(item => ({
                    id: item.id,
                    question: item.question,
                    answer: item.response
                }));

                setFaqs(transformedFaqs);
            } else {
                console.error('Erreur lors du chargement des FAQs:', response.status);
                setError('Impossible de charger les FAQs depuis le serveur');
            }
        } catch (err) {
            console.error('Erreur lors du chargement des FAQs:', err);
            setError('Erreur de connexion au serveur');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchFAQs();
    }, [])

    const toggle = (idx: number) => {
        setOpenIndex(openIndex === idx ? null : idx)
    }

    // Fonction pour ajouter une nouvelle FAQ
    const handleAddFaq = async () => {
        if (!formData.question.trim() || !formData.answer.trim()) {
            alert('Veuillez remplir tous les champs')
            return
        }

        setOperationLoading(true)
        try {
            const requestData: FAQRequest = {
                question: formData.question,
                response: formData.answer
            }

            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/FAQ`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                const newFaq: FAQResponse = await response.json();
                const transformedFaq: FAQ = {
                    id: newFaq.id,
                    question: newFaq.question,
                    answer: newFaq.response
                };

                setFaqs([...faqs, transformedFaq]);
                setShowAddFaq(false);
                setFormData({ question: "", answer: "" });
                alert('FAQ ajoutée avec succès');
            } else {
                const errorData = await response.json();
                alert(`Erreur lors de l'ajout: ${errorData.message || 'Erreur inconnue'}`);
            }
        } catch (err) {
            console.error('Erreur lors de l\'ajout de la FAQ:', err);
            alert('Erreur de connexion au serveur');
        } finally {
            setOperationLoading(false);
        }
    }

    // Fonction pour modifier une FAQ
    const handleEditFaq = (faq: FAQ) => {
        setEditingFaq(faq)
        setFormData({
            question: faq.question,
            answer: faq.answer
        })
    }

    // Fonction pour mettre à jour une FAQ
    const handleUpdateFaq = async () => {
        if (!editingFaq || !formData.question.trim() || !formData.answer.trim()) {
            alert('Veuillez remplir tous les champs')
            return
        }

        setOperationLoading(true)
        try {
            const requestData: FAQRequest = {
                id: editingFaq.id,
                question: formData.question,
                response: formData.answer
            }

            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/FAQ`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                const updatedFaq: FAQResponse = await response.json();
                const transformedFaq: FAQ = {
                    id: updatedFaq.id,
                    question: updatedFaq.question,
                    answer: updatedFaq.response
                };

                setFaqs(faqs.map(faq => faq.id === editingFaq.id ? transformedFaq : faq));
                setEditingFaq(null);
                setFormData({ question: "", answer: "" });
                alert('FAQ modifiée avec succès');
            } else {
                const errorData = await response.json();
                alert(`Erreur lors de la modification: ${errorData.message || 'Erreur inconnue'}`);
            }
        } catch (err) {
            console.error('Erreur lors de la modification de la FAQ:', err);
            alert('Erreur de connexion au serveur');
        } finally {
            setOperationLoading(false);
        }
    }

    // Fonction pour supprimer une FAQ
    const handleDeleteFaq = async (id: number) => {
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette FAQ ?')) {
            return
        }

        setOperationLoading(true)
        try {
            const response = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/FAQ/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok || response.status === 204) {
                setFaqs(faqs.filter(faq => faq.id !== id));
                alert('FAQ supprimée avec succès');
            } else {
                const errorData = await response.json();
                alert(`Erreur lors de la suppression: ${errorData.message || 'Erreur inconnue'}`);
            }
        } catch (err) {
            console.error('Erreur lors de la suppression de la FAQ:', err);
            alert('Erreur de connexion au serveur');
        } finally {
            setOperationLoading(false);
        }
    }

    return (
        <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
            {/* Navbar */}
            <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
                <Link to="/admin">
                    <div className="flex items-center gap-3">
                        <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
                    </div>
                </Link>

                <div className="flex gap-4">
                    <Link to="/qa/admin">
                        <button className="bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white font-bold px-8 py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-200">
                            Q&A
                        </button>
                    </Link>
                    {group?.toString() === "admin" && (<Link to="/my-tickets/admin">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white font-bold text-2xl px-16 py-6 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            Inbox
                        </button>
                    </Link>)}
                    {group?.toString() !== "admin" && (<Link to="/my-tickets/dev">
                        <button className="bg-gradient-to-b from-[#F89BEB] to-[#842D50] text-white font-bold text-2xl px-16 py-6 rounded-full shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 min-w-[200px]">
                            Inbox
                        </button>
                    </Link>)}
                </div>

                <Link to="/profile/admin">
                    <div className="flex items-center justify-center w-12 h-12 bg-white text-[#EA508E] rounded-full shadow-lg">
                        <span role="img" aria-label="profile" className="text-2xl">
                            👤
                        </span>
                    </div>
                </Link>
            </div>

            {/* Main Content */}
            <div className="flex-1 flex flex-col items-center pt-8">
                {/* Title and Edit Button */}
                <div className="w-full flex items-center justify-between mb-8 px-4">
                    <div className="flex-1"></div>
                    <h1 className="text-5xl font-bold text-white text-center flex-1">Q&A</h1>
                    <div className="flex-1 flex justify-end">
                        <button
                            onClick={() => setIsEditMode(!isEditMode)}
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-6 py-2 rounded-full flex items-center gap-2 transition-colors"
                        >
                            <span>Edit</span>
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                                />
                            </svg>
                        </button>
                    </div>
                </div>

                {/* Loading et Error */}
                {loading && (
                    <div className="text-center py-8">
                        <p className="text-gray-300 text-lg">Chargement des FAQs...</p>
                    </div>
                )}

                {error && (
                    <div className="text-center py-4 px-4 mb-4 bg-red-600/20 border border-red-600 rounded-lg">
                        <p className="text-red-300 text-sm">{error}</p>
                    </div>
                )}

                {operationLoading && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white p-4 rounded-lg">
                            <p className="text-lg">Opération en cours...</p>
                        </div>
                    </div>
                )}

                {/* Debug info */}
                <div className="w-full mb-4 px-4">
                    <p className="text-gray-400 text-sm">Total FAQs: {faqs.length}</p>
                </div>

                {/* FAQ Items */}
                <div className="w-full flex flex-col">
                    {faqs.map((faq, idx) => (
                        <div key={faq.id} className="border-b border-gray-600">
                            <button
                                type="button"
                                onClick={() => toggle(idx)}
                                className="w-full text-left px-6 py-6 font-semibold flex justify-between items-center text-white hover:bg-gray-600/20 transition-colors duration-200"
                            >
                                <span className="text-3xl">{faq.question}</span>
                                <span
                                    className={`text-[#EA508E] transition-transform duration-200 ${openIndex === idx ? "rotate-180" : ""}`}
                                >
                                    {openIndex === idx ? "▲" : "▼"}
                                </span>
                            </button>
                            {openIndex === idx && (
                                <div className="px-6 pb-6 text-gray-300 leading-relaxed">
                                    <div className="flex justify-between items-start">
                                        <p className="flex-1 mr-4">{faq.answer}</p>
                                        {isEditMode && (
                                            <div className="flex gap-2 mr-4">
                                                <button
                                                    onClick={() => handleEditFaq(faq)}
                                                    className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-full flex items-center gap-1"
                                                >
                                                    <span>Edit</span>
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path
                                                            strokeLinecap="round"
                                                            strokeLinejoin="round"
                                                            strokeWidth={2}
                                                            d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                                                        />
                                                    </svg>
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteFaq(faq.id)}
                                                    className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition-colors"
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </div>

                {/* Add FAQ Button */}
                {isEditMode && (
                    <div className="w-full flex justify-center mt-8 px-4">
                        <button
                            onClick={() => setShowAddFaq(true)}
                            className="bg-green-600 hover:bg-green-700 text-white font-bold px-6 py-3 rounded-lg transition-colors"
                        >
                            + Add FAQ
                        </button>
                    </div>
                )}
            </div>

            {/* Add/Edit FAQ Modal */}
            {(showAddFaq || editingFaq) && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
                        <h2 className="text-2xl font-bold mb-4">{editingFaq ? "Edit FAQ" : "Add FAQ"}</h2>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Question</label>
                                <input
                                    type="text"
                                    value={formData.question}
                                    onChange={(e) => setFormData({ ...formData, question: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#E87BBE] focus:outline-none"
                                    placeholder="Enter the question..."
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Answer</label>
                                <textarea
                                    value={formData.answer}
                                    onChange={(e) => setFormData({ ...formData, answer: e.target.value })}
                                    rows={4}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#E87BBE] focus:outline-none"
                                    placeholder="Enter the answer..."
                                />
                            </div>
                        </div>
                        <div className="flex justify-end gap-4 mt-6">
                            <button
                                onClick={() => {
                                    setShowAddFaq(false)
                                    setEditingFaq(null)
                                    setFormData({ question: "", answer: "" })
                                }}
                                className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={editingFaq ? handleUpdateFaq : handleAddFaq}
                                disabled={operationLoading}
                                className="px-6 py-2 bg-[#E87BBE] text-white rounded-lg hover:bg-[#d63f7a] transition-colors disabled:opacity-50"
                            >
                                {operationLoading ? "..." : (editingFaq ? "Update" : "Add")}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default QAAdmin