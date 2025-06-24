import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const SetNewPassword = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (newPassword !== confirm) {
      setError('Les mots de passe ne correspondent pas.');
      return;
    }

    // Simuler appel API
    console.log('Mot de passe mis à jour :', newPassword);
    alert('Mot de passe réinitialisé !');
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
          <div className="relative">
            <input
              type={showNew ? 'text' : 'password'}
              required
              minLength={6}
              placeholder="Nouveau mot de passe"
              className="bg-[#d3d4dc] w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <button
              type="button"
              onClick={() => setShowNew(!showNew)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
            >
              <i className={`far ${showNew ? 'fa-eye-slash' : 'fa-eye'}`} />
            </button>
          </div>

          <div className="relative">
            <input
              type={showConfirm ? 'text' : 'password'}
              required
              minLength={6}
              placeholder="Confirmer le mot de passe"
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

          {error && <p className="text-sm text-red-600">{error}</p>}

          <button
            type="submit"
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Réinitialiser le mot de passe
          </button>
        </form>
      </div>
    </div>
  );
};

export default SetNewPassword;

