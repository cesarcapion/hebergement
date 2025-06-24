import { useState } from 'react';
import { Link } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      alert('Veuillez remplir tous les champs.');
      return;
    }

    alert('Connexion simulée !');
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
            placeholder="votre@email.com"
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
            Mot de passe oublié ?
          </Link>

          <button
            type="submit"
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Se connecter
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          Pas de compte ?{' '}
          <Link to="/signup" className="text-[#EA508E] hover:underline">
            Créer un compte
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;

