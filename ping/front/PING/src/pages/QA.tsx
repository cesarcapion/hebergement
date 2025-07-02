import { useState } from 'react';
import { Link } from 'react-router-dom';

const isAdmin = false
let categories = [
  {
    id: 1,
    name: "Category 1"
  },
  {
    id: 2,
    name: "Category 2"
  },
  {
    id: 3,
    name: "Category 3"
  }
]
let initialFAQs = [
  {
    question: 'How do I create an account?',
    answer: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
  {
    question: 'I forgot my password, what should I do?',
    answer: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
  {
    question: 'How can I purchase tickets?',
    answer: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
  {
    question: 'Can I get a refund for my ticket?',
    answer: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  },
];


const QA = () => {
  const [openIndex, setOpenIndex] = useState<number | null>(null);
  const [editIndex, setEditedIndex ] = useState<number | null>(null);
  const [faqs, setFaqs] = useState(initialFAQs)
  const [editedQuestion, setEditedQuestion] = useState<string>('')
  const [editedAnswer, setEditedAnswer] = useState<string>('')
  const [confirmDeleteIndex, setConfirmDeleteIndex] = useState<number | null>(null)
  const toggle = (idx: number) => {
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <div className="w-screen min-h-screen bg-[#384454] flex flex-col">
      {/* Navbar */}
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

      {/* Main Content */}
      <div className="flex-1 flex flex-col items-center justify-start pt-12">
        <h1 className="text-3xl font-bold text-white mb-8 text-center">Q&amp;A</h1>

        {isAdmin && 
        (<div>
          <button className={`bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mb-6 ${editIndex !== null ? 'text-gray-400 cursor-not-allowed' : ''}`}
          onClick={() =>
            {
              initialFAQs.unshift({question: "", answer: ""})
              setFaqs(initialFAQs)
              setEditedIndex(0)
              setEditedAnswer(faqs[0].answer)
              setEditedQuestion(faqs[0].question)
              setOpenIndex(0)
            }}
          >
            ➕ Add Question
          </button>
        </div>)
        }
        <div className="w-full max-w-2xl flex flex-col gap-4 px-4">
          {faqs.length === 0 && 
          (  <div className="text-3xl text-center text-white font-semibold mt-10">
               No questions yet!
            </div>)
          }
          {
            faqs.map((faq, idx) => (
              
            <div key={idx} className="rounded-lg overflow-hidden">
            <div key={idx} className="w-full text-left font-semibold bg-white text-[#384454] shadow focus:outline-none">
              <button
                type="button"
                onClick={() => {
                  if (editIndex === null)
                  {
                    toggle(idx)
                  }
                }}
                className="w-full text-left px-6 py-4 font-semibold flex justify-between items-center bg-white text-[#384454] shadow focus:outline-none">
                {isAdmin && <button id="edit_button"
                  onClick={(e) => {
                    if (openIndex === idx)
                    {
                      e.stopPropagation()
                    }
                    setEditedIndex(idx)
                    setEditedAnswer(faqs[idx].answer)
                    setEditedQuestion(faqs[idx].question)
                  }}
                  disabled={editIndex !== null && editIndex !== idx}
                  className={`font-medium whitespace-nowrap ${editIndex !== null && editIndex !== idx ? 'text-gray-400 cursor-not-allowed' : ''}`}
                  >
                  <span>edit ✏️</span>
                </button>}
                <div className='flex items-center justify-between w-full'>
                  {editIndex !== idx ? 
                  <span>{faq.question}</span> : 
                  <input placeholder='Type the question...' value={editedQuestion}
                   onChange={(e) => setEditedQuestion(e.target.value)} 
                  className="w-full text-[#384454] bg-transparent border-b border-gray-300 focus:outline-none"/>
                   }
                  
                  {editIndex === idx && (
                  <button
                  onClick={(e) => {
                    e.stopPropagation()
                    // FIXME handle the case where updated question or updated answer is empty
                    faqs[idx].answer = editedAnswer
                    faqs[idx].question = editedQuestion
                    setEditedIndex(null)
                    setEditedAnswer('')
                    setEditedQuestion('')
                    }
                  }
                  className="ml-4 whitespace-nowrap">
                    <span>Save 💾</span>
                  </button>
                )}
                </div>
                <span className={`text-[#EA508E] transition-transform ${openIndex === idx ? 'rotate-180' : ''}`}>▼</span>
              </button>
              {openIndex === idx && (
                <div className="px-6 py-4 bg-white text-[#384454] border-t">
                  {editIndex !== idx ?
                   <p>{faq.answer}</p> : 
                  (
                    <textarea value={editedAnswer} placeholder='Type the answer...'
                    className="w-full min-h-[80px] text-[#384454] bg-transparent border-b border-gray-300 focus:outline-none resize-none"
                    onChange={(e) => {
                      setEditedAnswer(e.target.value)
                    }}/>
                  )}
                </div>
              )}
              {isAdmin && editIndex == null && (
                confirmDeleteIndex !== idx ? 
                (
                <button className="ml-4 bg-red-500 text-white font-bold px-4 py-2 rounded hover:bg-red-700 focus:outline-none"
                onClick={() => {setConfirmDeleteIndex(idx)}}>Delete</button>
                ) :
                (
                <div className="mt-2 flex gap-2">
                  <button
                    className="bg-red-500 text-white font-bold px-3 py-1 rounded hover:bg-red-700"
                    onClick={() => {
                      // your actual delete logic here
                      faqs.splice(idx, 1)
                      setConfirmDeleteIndex(null)
                    }}
                  >
                    Confirm ❌
                  </button>
                  <button
                    className="bg-gray-300 text-black font-bold px-3 py-1 rounded hover:bg-gray-400"
                    onClick={() => setConfirmDeleteIndex(null)}
                  >
                    Cancel
                  </button>
                </div>
                )
              )}
            </div>
            </div>
          ))
          }
        {/* FIXME no buttons in buttons, find another way to do this*/}
        </div>
        {!isAdmin && 
        <div className="text-center mt-8 text-sm text-gray-200 mb-10">
          You didn't find your answer?{' '}
          <Link to="/create-ticket" className="text-[#EA508E] hover:underline">
            Create a ticket
          </Link>
        </div>
        }
      </div>
    </div>
  );
};

export default QA;

