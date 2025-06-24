import { useState } from 'react';
import { Link } from 'react-router-dom';

const ResetPasswordEmail = () => {
  const [email, setEmail] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      alert('Veuillez entrer votre adresse email.');
      return;
    }

    alert('Un lien de réinitialisation a été envoyé à votre adresse email.');
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

          <button
            type="submit"
            className="w-full bg-[#E1A624] text-white font-bold py-2 px-4 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Réinitialiser le mot de passe
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-[#d3d4dc]">
          <Link to="/login" className="text-[#EA508E] hover:underline">
            Retour à la connexion
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordEmail;

