import { useState } from 'react';
import { Link } from 'react-router-dom';

const isAdmin = true;

const initialCategories = [
  {
    id: 1,
    name: "Compte",
    faqs: [
      {
        question: "Comment créer un compte ?",
        answer: "Rendez-vous sur la page d'inscription et suivez les étapes.",
      },
      {
        question: "J'ai oublié mon mot de passe",
        answer: "Cliquez sur 'mot de passe oublié' et suivez les instructions.",
      }
    ]
  },
  {
    id: 2,
    name: "Billets",
    faqs: [
      {
        question: "Comment acheter un billet ?",
        answer: "Connectez-vous et accédez à la section Billetterie.",
      },
      {
        question: "Puis-je obtenir un remboursement ?",
        answer: "Les remboursements sont possibles jusqu'à 48h avant l'événement.",
      }
    ]
  }
];

const QA = () => {
  const [categories, setCategories] = useState(initialCategories);
  const [editFaq, setEditFaq] = useState<{ catIdx: number; faqIdx: number } | null>(null);
  const [editCategory, setEditCategory] = useState<number | null>(null);
  const [editedQuestion, setEditedQuestion] = useState('');
  const [editedAnswer, setEditedAnswer] = useState('');
  const [editedCategoryName, setEditedCategoryName] = useState('');
  const [expandedCategories, setExpandedCategories] = useState<number[]>([]);

  const toggleCategory = (catIdx: number) => {
    setExpandedCategories(prev =>
        prev.includes(catIdx)
            ? prev.filter(idx => idx !== catIdx)
            : [...prev, catIdx]
    );
  };

  return (
      <div className="w-screen min-h-screen bg-[#4A5568] flex flex-col">
        {/* Navbar */}
        <div className="bg-[#F6D55C] px-6 py-4 flex items-center justify-between">
          <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center">
            <div className="w-6 h-6 border-2 border-gray-600 rounded-full flex items-center justify-center">
              <div className="w-3 h-3 bg-gray-600 rounded-full"></div>
            </div>
          </div>
          <div className="flex gap-4">
            <Link to="/qa">
              <button className="bg-[#B794F6] text-white font-semibold px-8 py-3 rounded-full">Q&A</button>
            </Link>
            <Link to="/inbox">
              <button className="bg-[#B794F6] text-white font-semibold px-8 py-3 rounded-full">Inbox</button>
            </Link>
          </div>
          <div className="w-10 h-10 bg-[#B794F6] rounded-full flex items-center justify-center">
            <span className="text-white text-lg">👤</span>
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1 px-6 py-8">
          <div className="flex items-center justify-between mb-8">
            <h1 className="text-4xl font-bold text-white">Q&A</h1>


          </div>

          {isAdmin && (
              <button
                  onClick={() => {
                    const newCategory = {
                      id: Date.now(),
                      name: 'Nouvelle catégorie',
                      faqs: [],
                    };
                    setCategories([...categories, newCategory]);
                  }}
                  className="mb-6 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
              >
                ➕ Ajouter une catégorie
              </button>
          )}

          <div className="max-w-4xl">
            {categories.map((cat, catIdx) => {
              const isExpanded = expandedCategories.includes(catIdx);

              return (
                  <div key={cat.id} className="mb-4">
                    <div
                        className="flex items-center justify-between py-6 border-b border-gray-600 cursor-pointer hover:bg-gray-700/30 transition-colors"
                        onClick={() => toggleCategory(catIdx)}
                    >
                      <div className="flex items-center gap-4">
                        {editCategory === catIdx ? (
                            <div className="flex items-center gap-2">
                              <input
                                  className="text-2xl font-normal bg-transparent border-b border-white text-white focus:outline-none"
                                  value={editedCategoryName}
                                  onChange={(e) => setEditedCategoryName(e.target.value)}
                                  placeholder="Nom de la catégorie..."
                                  onClick={(e) => e.stopPropagation()}
                              />
                              <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    const updated = [...categories];
                                    updated[catIdx].name = editedCategoryName;
                                    setCategories(updated);
                                    setEditCategory(null);
                                    setEditedCategoryName('');
                                  }}
                                  className="bg-green-600 text-white px-2 py-1 rounded text-sm"
                              >
                                💾
                              </button>
                            </div>
                        ) : (
                            <h2 className="text-2xl text-white font-normal">{cat.name}</h2>
                        )}
                      </div>

                      <div className="flex items-center gap-4">
                        {isAdmin && editCategory !== catIdx && (
                            <div className="flex gap-2">
                              <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    setEditCategory(catIdx);
                                    setEditedCategoryName(cat.name);
                                  }}
                                  className="bg-gray-600 text-white px-3 py-1 rounded text-sm flex items-center gap-1"
                              >
                                <span>Edit</span>
                                <span>✏️</span>
                              </button>
                              <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    const updated = categories.filter((_, i) => i !== catIdx);
                                    setCategories(updated);
                                  }}
                                  className="text-red-400 text-sm px-2"
                              >
                                ❌
                              </button>
                            </div>
                        )}
                        <span className={`text-white text-xl transition-transform ${isExpanded ? 'rotate-180' : ''}`}>
                        ▼
                      </span>
                      </div>
                    </div>

                    {isExpanded && (
                        <div className="py-4 pl-6">
                          {cat.faqs.length === 0 && (
                              <div className="text-gray-300 text-sm mb-4">Aucune question dans cette catégorie.</div>
                          )}

                          {cat.faqs.map((faq, faqIdx) => {
                            const isEditing = editFaq?.catIdx === catIdx && editFaq?.faqIdx === faqIdx;

                            return (
                                <div key={faqIdx} className="mb-4 pb-4 border-b border-gray-600 last:border-b-0">
                                  <div className="flex justify-between items-start mb-2">
                                    <div className="flex-grow">
                                      {isEditing ? (
                                          <input
                                              className="w-full bg-transparent border-b border-gray-400 text-white focus:outline-none pb-1"
                                              value={editedQuestion}
                                              onChange={(e) => setEditedQuestion(e.target.value)}
                                              placeholder="Question..."
                                          />
                                      ) : (
                                          <h3 className="text-white font-medium text-lg">{faq.question}</h3>
                                      )}
                                    </div>

                                    {isAdmin && (
                                        <div className="ml-4 flex gap-2">
                                          {!isEditing && (
                                              <button
                                                  onClick={() => {
                                                    setEditFaq({ catIdx, faqIdx });
                                                    setEditedQuestion(faq.question);
                                                    setEditedAnswer(faq.answer);
                                                  }}
                                                  className="bg-gray-600 text-white px-3 py-1 rounded text-sm flex items-center gap-1"
                                              >
                                                <span>Edit</span>
                                                <span>✏️</span>
                                              </button>
                                          )}
                                          {!isEditing && (
                                              <button
                                                  onClick={() => {
                                                    const updated = [...categories];
                                                    updated[catIdx].faqs.splice(faqIdx, 1);
                                                    setCategories(updated);
                                                  }}
                                                  className="text-red-400 text-sm px-2"
                                              >
                                                ❌
                                              </button>
                                          )}
                                        </div>
                                    )}
                                  </div>

                                  <div className="text-gray-300 text-sm leading-relaxed">
                                    {isEditing ? (
                                        <div>
                              <textarea
                                  className="w-full min-h-[80px] bg-transparent border-b border-gray-400 text-gray-300 focus:outline-none resize-none pb-2"
                                  value={editedAnswer}
                                  onChange={(e) => setEditedAnswer(e.target.value)}
                                  placeholder="Réponse..."
                              />
                                          <button
                                              className="mt-2 bg-green-600 text-white px-3 py-1 rounded text-sm"
                                              onClick={() => {
                                                const updated = [...categories];
                                                updated[catIdx].faqs[faqIdx].question = editedQuestion;
                                                updated[catIdx].faqs[faqIdx].answer = editedAnswer;
                                                setCategories(updated);
                                                setEditFaq(null);
                                                setEditedAnswer('');
                                                setEditedQuestion('');
                                              }}
                                          >
                                            💾 Enregistrer
                                          </button>
                                        </div>
                                    ) : (
                                        <p>{faq.answer}</p>
                                    )}
                                  </div>
                                </div>
                            );
                          })}

                          {isAdmin && (
                              <button
                                  onClick={() => {
                                    const updated = [...categories];
                                    updated[catIdx].faqs.push({ question: '', answer: '' });
                                    setCategories(updated);
                                    setEditFaq({ catIdx, faqIdx: updated[catIdx].faqs.length - 1 });
                                    setEditedQuestion('');
                                    setEditedAnswer('');
                                  }}
                                  className="text-gray-300 text-sm hover:text-white transition-colors"
                              >
                                ➕ Ajouter une question
                              </button>
                          )}
                        </div>
                    )}
                  </div>
              );
            })}
          </div>

          {!isAdmin && (
              <div className="text-center text-sm text-gray-300 mt-8">
                Vous n'avez pas trouvé votre réponse ?{' '}
                <Link to="/create-ticket" className="text-[#B794F6] hover:underline">
                  Créez un ticket
                </Link>
              </div>
          )}
        </div>
      </div>
  );
};

export default QA;