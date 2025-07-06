import React, { useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import { login } from '../api/login.tsx';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [captchaInput, setCaptchaInput] = useState('');
  const [captchaCode, setCaptchaCode] = useState('');
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const generateCaptcha = () => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < 6; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    setCaptchaCode(result);
    drawCaptcha(result);
  };

  const drawCaptcha = (code: string) => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = '#f0f0f0';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    for (let i = 0; i < 8; i++) {
      ctx.strokeStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.3)`;
      ctx.beginPath();
      ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height);
      ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height);
      ctx.stroke();
    }

    ctx.font = '24px Arial';
    ctx.fillStyle = '#333';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    for (let i = 0; i < code.length; i++) {
      ctx.save();
      const x = 20 + i * 20;
      const y = canvas.height / 2;
      ctx.translate(x, y);
      ctx.rotate((Math.random() - 0.5) * 0.3);
      ctx.fillText(code[i], 0, 0);
      ctx.restore();
    }

    for (let i = 0; i < 20; i++) {
      ctx.fillStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.5)`;
      ctx.beginPath();
      ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, 2, 0, 2 * Math.PI);
      ctx.fill();
    }
  };

  React.useEffect(() => {
    generateCaptcha();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      setError('Please fill in all fields.');
      return;
    }

    if (!captchaInput) {
      setError('Please enter the CAPTCHA code.');
      return;
    }

    if (captchaInput.toLowerCase() !== captchaCode.toLowerCase()) {
      setError('Invalid CAPTCHA code. Please try again.');
      setCaptchaInput('');
      generateCaptcha();
      return;
    }

    try {
      console.log("email : " +  email + "password : " + password);
      const a = await login(email, password);
      setError(a);

      setCaptchaInput('');
      generateCaptcha();
    } catch (error) {
      console.error('Erreur de connexion', error);
      setError('Login failed. Please try again.');

      setCaptchaInput('');
      generateCaptcha();
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

            {/* CAPTCHA Section */}
            <div className="space-y-2">
              <label className="text-sm text-[#d3d4dc]">Verification Code:</label>
              <div className="flex items-center space-x-2">
                <canvas
                    ref={canvasRef}
                    width={140}
                    height={50}
                    className="border border-gray-300 rounded bg-white"
                />
                <button
                    type="button"
                    onClick={generateCaptcha}
                    className="px-3 py-2 bg-[#EA508E] text-white rounded hover:bg-pink-600 focus:outline-none focus:ring-2 focus:ring-pink-500"
                    title="Generate new CAPTCHA"
                >
                  🔄
                </button>
              </div>
              <input
                  type="text"
                  placeholder="Enter the code above"
                  className="bg-[#d3d4dc] w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={captchaInput}
                  onChange={(e) => setCaptchaInput(e.target.value)}
                  maxLength={6}
              />
            </div>

            <button
                type="submit"
                className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              Login
            </button>
          </form>

          {error && (
              <p className="text-sm text-red-600 mt-2">{error}</p>
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