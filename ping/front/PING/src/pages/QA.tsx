"use client"

import { useState, useMemo, useEffect } from "react"
import { Link } from "react-router-dom"
import {authedAPIRequest} from "../api/auth.tsx";

interface FAQ {
  id: number
  question: string
  answer: string
}

interface FAQResponse {
  id: number
  question: string
  answer: string
}

const FAQ_STORAGE_KEY = "qa"

const defaultFaqs: FAQ[] = []

const QA = () => {
  const [faqs, setFaqs] = useState<FAQ[]>(defaultFaqs)
  const [openIndex, setOpenIndex] = useState<number | null>(null)
  const [searchQuery, setSearchQuery] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const filteredFaqs = useMemo(() => {
    if (!searchQuery.trim()) return faqs

    return faqs.filter(
        (faq) =>
            faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
            faq.answer.toLowerCase().includes(searchQuery.toLowerCase()),
    )
  }, [faqs, searchQuery])

  useEffect(() => {
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

          // Transformer les données de l'API en format FAQ si nécessaire
          const transformedFaqs: FAQ[] = faqData.map(item => ({
            id: item.id,
            question: item.question,
            answer: item.response
          }));

          setFaqs(transformedFaqs);
        } else {
          // En cas d'erreur, garder les FAQs par défaut
          console.error('Erreur lors du chargement des FAQs:', response.status);
          setError('Impossible de charger les FAQs depuis le serveur');
        }
      } catch (err) {
        console.error('Erreur lors du chargement des FAQs:', err);
        setError('Erreur de connexion au serveur');
        // Garder les FAQs par défaut en cas d'erreur
      } finally {
        setLoading(false);
      }
    };

    fetchFAQs();
  }, [])

  const toggle = (idx: number) => {
    setOpenIndex(openIndex === idx ? null : idx)
  }

  return (
      <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
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

        <div className="flex-1 flex flex-col items-center pt-8">
          <div className="w-full flex items-center justify-between mb-8 px-4">
            <div className="flex-1"></div>
            <h1 className="text-4xl font-bold text-white text-center flex-1">Q&A</h1>
            <div className="flex-1 flex justify-end">
              <div className="relative w-80">
                <input
                    type="text"
                    placeholder="Search questions..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="w-full pl-4 pr-12 py-3 rounded-full bg-gray-200 border-0 text-gray-800 placeholder-gray-500 focus:ring-2 focus:ring-[#EA508E] focus:outline-none"
                />
                <svg
                    className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-500 w-5 h-5"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                  <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
              </div>
            </div>
          </div>

          {/* Affichage du loading */}
          {loading && (
              <div className="text-center py-8">
                <p className="text-gray-300 text-lg">Chargement des FAQs...</p>
              </div>
          )}

          {/* Affichage des erreurs */}
          {error && (
              <div className="text-center py-4 px-4 mb-4 bg-red-600/20 border border-red-600 rounded-lg">
                <p className="text-red-300 text-sm">{error}</p>
              </div>
          )}

          <div className="w-full flex flex-col">
            {!loading && filteredFaqs.length === 0 ? (
                <div className="text-center py-8">
                  <p className="text-gray-300 text-lg">
                    {searchQuery ? `Aucune question trouvée pour "${searchQuery}"` : "No FAQs available"}
                  </p>
                </div>
            ) : (
                filteredFaqs.map((faq, idx) => (
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
                            <p>{faq.answer}</p>
                          </div>
                      )}
                    </div>
                ))
            )}
          </div>

          <div className="text-center mt-12 mb-8 text-sm text-gray-300 px-4">
            {"You didn't find your answer? "}
            <Link to="/create-ticket" className="text-[#EA508E] hover:underline font-semibold">
              Create a ticket
            </Link>
          </div>
        </div>
      </div>
  )
}

export default QA