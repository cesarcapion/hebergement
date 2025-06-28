import { useState } from 'react';
import { Link } from 'react-router-dom';

const faqs = [
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

  const toggle = (idx: number) => {
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <div className="w-screen h-screen bg-[#384454] flex flex-col">
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

        <div className="w-full max-w-2xl flex flex-col gap-4 px-4">
          {faqs.map((faq, idx) => (
            <div key={idx} className="rounded-lg overflow-hidden">
              <button
                type="button"
                onClick={() => toggle(idx)}
                className="w-full text-left px-6 py-4 font-semibold flex justify-between items-center bg-white text-[#384454] shadow focus:outline-none"
              >
                {faq.question}
                <span className={`text-[#EA508E] transition-transform ${openIndex === idx ? 'rotate-180' : ''}`}>▼</span>
              </button>
              {openIndex === idx && (
                <div className="px-6 py-4 bg-white text-[#384454] border-t">
                  <p>{faq.answer}</p>
                </div>
              )}
            </div>
          ))}
        </div>

        <div className="text-center mt-8 text-sm text-gray-200">
          You didn't find your answer?{' '}
          <Link to="/create-ticket" className="text-[#EA508E] hover:underline">
            Create a ticket
          </Link>
        </div>
      </div>
    </div>
  );
};

export default QA;

