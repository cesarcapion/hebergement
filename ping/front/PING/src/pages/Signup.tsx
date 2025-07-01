import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signUp } from '../api/signUp.tsx';

const Signup = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [error, setError] = useState('');
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
      } else {
        setError("An unknown error occurred.");
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

          <div className="relative">
            <input
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="password"
              minLength={12}
              className="bg-[#d3d4dc] w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
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
              placeholder="confirm password"
              minLength={6}
              className="bg-[#d3d4dc] w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
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
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Sign up
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          Already an account ?{' '}
          <Link to="/login" className="text-[#EA508E] hover:underline">
            Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Signup;

