"use client"

import { useState, useMemo, useEffect } from "react"
import { Link } from "react-router-dom"

interface FAQ {
  id: number
  question: string
  answer: string
}

const FAQ_STORAGE_KEY = "qa_faqs"

const defaultFaqs: FAQ[] = [
  {
    id: 1,
    question: "How do I create an account?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    id: 2,
    question: "I forgot my password, what should I do?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    id: 3,
    question: "How can I purchase tickets?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    id: 4,
    question: "Can I get a refund for my ticket?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    id: 5,
    question: "How do I contact support?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
  {
    id: 6,
    question: "What payment methods do you accept?",
    answer:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
  },
]

const QA = () => {
  const [faqs, setFaqs] = useState<FAQ[]>(defaultFaqs)
  const [openIndex, setOpenIndex] = useState<number | null>(null)
  const [searchQuery, setSearchQuery] = useState("")

  const filteredFaqs = useMemo(() => {
    if (!searchQuery.trim()) return faqs

    return faqs.filter(
        (faq) =>
            faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
            faq.answer.toLowerCase().includes(searchQuery.toLowerCase()),
    )
  }, [faqs, searchQuery])

  useEffect(() => {
    const loadFaqs = () => {
      try {
        const stored = localStorage.getItem(FAQ_STORAGE_KEY)
        if (stored) {
          setFaqs(JSON.parse(stored))
        }
      } catch (error) {
        console.error("Failed to load FAQs:", error)
      }
    }

    loadFaqs()

    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === FAQ_STORAGE_KEY) {
        loadFaqs()
      }
    }

    window.addEventListener("storage", handleStorageChange)

    const interval = setInterval(loadFaqs, 1000)

    return () => {
      window.removeEventListener("storage", handleStorageChange)
      clearInterval(interval)
    }
  }, [])

  const toggle = (idx: number) => {
    setOpenIndex(openIndex === idx ? null : idx)
  }

  return (
      <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
        <div className="bg-[#E1A624] px-4 py-3 flex items-center justify-between">
          <Link to="/">
            <div className="flex items-center gap-3">
              <img src="/White-Logo-without-bg.png" alt="logo" className="w-10 h-10" />
            </div>
          </Link>

          <div className="flex gap-4">
            <Link to="/qa">
              <button className="btn">
                Q&A
              </button>
            </Link>
            <Link to="/my-tickets">
              <button className="btn">
                My tickets
              </button>
            </Link>
          </div>

          <Link to="/profile">
            <div className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg">
            <span role="img" aria-label="profile" className="text-2xl">
              👤
            </span>
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

          <div className="w-full flex flex-col">
            {filteredFaqs.length === 0 ? (
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
