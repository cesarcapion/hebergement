import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signUp } from '../api/signUp.tsx';

const Signup = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [error, setError] = useState('');
  const [showHelp, setShowHelp] = useState(false)

  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirm) {
      setError('The passwords do not match.');
      return;
    }
    try {
      await signUp(email, password)
    }
    catch (error: unknown) {
      if (error instanceof Error) {
        setError(error.message);
        return;
      } else {
        setError("An unknown error occurred.");
        return;
      }
    }


    navigate('/login');
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

          <div className="relative">
            <input
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="Password"
              minLength={12}
              className="w-full px-4 py-4 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
            >
              <i className={`far ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`} />
            </button>
          </div>

          <div className="relative">
            <input
              type={showConfirm ? 'text' : 'password'}
              required
              placeholder="Confirm password"
              minLength={6}
              className="w-full px-4 py-4 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
            />
            <button
              type="button"
              onClick={() => setShowConfirm(!showConfirm)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
            >
              <i className={`far ${showConfirm ? 'fa-eye-slash' : 'fa-eye'}`} />
            </button>
          </div>

          {error && (
            <p className="text-sm text-red-600">{error}</p>
          )}

          <button
            type="submit"
            className="w-full bg-gradient-to-b from-[#E1A624] to-[#7B5B14] text-white font-bold py-4 rounded-full hover:from-[#D4991F] hover:to-[#A6851A] focus:outline-none transition-all duration-200 shadow-lg">
            Sign-in
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-gray-400">
          Already an account ?{' '}
          <Link to="/login" className="text-[#EA508E] underline">
            Log-in
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
              <h3 className="text-lg font-bold text-gray-800">Sign-up Help</h3>
              <button onClick={() => setShowHelp(false)} className="text-gray-500 hover:text-gray-700 text-xl font-bold">
                ×
              </button>
            </div>
            <div className="text-sm text-gray-600 space-y-2">
              <p>
                <strong>How to sign up:</strong>
              </p>
              <ol className="list-decimal list-inside space-y-1 ml-2">
                <li>Enter your email address in the field above</li>
                <li>Enter your password in the field above</li>
                <li>Confirm your password in the field above</li>
                <li>If you already have an account : click on "Log-in"</li>
              </ol>
            </div>
          </div>
      )}

      {/* Overlay to close help when clicking outside */}
      {showHelp && <div className="fixed inset-0 z-[-1]" onClick={() => setShowHelp(false)} />}
    </div>
  );
};

export default Signup;

