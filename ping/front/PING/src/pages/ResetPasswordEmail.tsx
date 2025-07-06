import { useState } from 'react';
import { Link } from 'react-router-dom';

const ResetPasswordEmail = () => {
  const [email, setEmail] = useState('');
  const [showHelp, setShowHelp] = useState(false)

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
          className="mx-auto mb-6 w-36 h-auto"
        />

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            required
            placeholder="E-mail"
            className="w-full px-4 py-4 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <button
            type="submit"
            className="w-full bg-gradient-to-b from-[#E1A624] to-[#7B5B14] text-white font-bold py-4 rounded-full hover:from-[#D4991F] hover:to-[#A6851A] focus:outline-none transition-all duration-200 shadow-lg">
            Reset Password
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          <Link to="/login" className="text-[#EA508E] underline">
            Back to connection
          </Link>
        </div>
      </div>

      {/* Help Button */}
      <div className="fixed bottom-6 right-6">
        <button
            onClick={() => setShowHelp(!showHelp)}
            className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200"
        >
          <span className="text-xl font-bold">?</span>
        </button>
      </div>

      {/* Help Modal */}
      {showHelp && (
          <div className="fixed bottom-20 right-6 bg-white rounded-lg shadow-xl p-4 w-80 border border-gray-200">
            <div className="flex justify-between items-start mb-3">
              <h3 className="text-lg font-bold text-gray-800">Password Reset Help</h3>
              <button onClick={() => setShowHelp(false)} className="text-gray-500 hover:text-gray-700 text-xl font-bold">
                ×
              </button>
            </div>
            <div className="text-sm text-gray-600 space-y-2">
              <p>
                <strong>How to reset your password:</strong>
              </p>
              <ol className="list-decimal list-inside space-y-1 ml-2">
                <li>Enter your email address in the field above</li>
                <li>Click "Reset Password" button</li>
                <li>Check your email inbox (and spam folder)</li>
                <li>Click the reset link in the email</li>
                <li>Create a new password</li>
              </ol>
            </div>
          </div>
      )}

      {/* Overlay to close help when clicking outside */}
      {showHelp && <div className="fixed inset-0 z-[-1]" onClick={() => setShowHelp(false)} />}
    </div>

  );
};

export default ResetPasswordEmail;

