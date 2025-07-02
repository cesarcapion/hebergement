import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { login } from '../api/login.tsx';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log(email);
    if (!email || !password) {
      //alert('Please fill in all fields.');
      return;
    }

    try {
      console.log("email : " +  email + "password : " + password);
      const a = await login(email, password);
      setError(a);
    } catch (error) {
      console.error('Erreur de connexion', error);
      //alert("Login failed.");
    }
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
              placeholder="••••••••"
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

          <Link
            to="/reset-password"
            className="text-sm text-[#EA508E] hover:underline block"
          >
            Forgot password ?
          </Link>

          <button
            type="submit"
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Login
          </button>
        </form>
        {error && (
            <p className="text-sm text-red-600">{error}</p>
        )}

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          No account ?{' '}
          <Link to="/signup" className="text-[#EA508E] hover:underline">
            Create an account
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;

