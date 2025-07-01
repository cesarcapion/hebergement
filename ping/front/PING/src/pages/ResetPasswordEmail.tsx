import { useState } from 'react';
import { Link } from 'react-router-dom';

const ResetPasswordEmail = () => {
  const [email, setEmail] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      alert('Enter your email.');
      return;
    }
    await fetch(`${import.meta.env.VITE_SERVER_URL}/api/user/request-reset`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ mail: email}),
    });
    alert('A reset link has been sent to your email address.');
  };

  return (
    <div className="w-screen h-screen bg-[#384454] flex items-center justify-center">
      <div className="bg-[#384454] p-8 rounded-lg shadow-lg w-full max-w-md">
        <img
          src="/White-Logo-without-bg.png"
          alt="Logo"
          className="mx-auto mb-6 w-96 h-auto"
        />

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            required
            placeholder="your@email.com"
            className="bg-[#d3d4dc] w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <button
            type="submit"
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Reset Password
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          <Link to="/login" className="text-[#EA508E] hover:underline">
            Back to connection
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordEmail;

