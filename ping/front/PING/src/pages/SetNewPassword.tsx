import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

const SetNewPassword = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const [newPassword, setNewPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [error, setError] = useState('');
  const [showHelp, setShowHelp] = useState(false)

  const queryParams = new URLSearchParams(location.search);
  const token = queryParams.get('token');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (newPassword !== confirm) {
      setError('The passwords do not match.');
      return;
    }

    try {
      console.log(newPassword);
      const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/api/user/update-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({token, password: newPassword }),
      });
      console.log(`token: ${token}, password: ${newPassword}, response:`, response);

      if (response.status === 400) {
        setError("Bad Token");
        return;
      } else if (response.status === 406) {
        setError("Password should be at least 12 characters long and be composed of digits, letters, capital letters and symbols.");
        return;
      }
    } catch (error) {
      console.error("Login error:", error);
      return;
    }
    console.log("Token récupéré depuis l'URL :", token);
    console.log('Password updated :', newPassword);

    alert('Password updated !');
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
              placeholder="New password"
              className="w-full px-4 py-4 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
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
              placeholder="Confirm password"
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

          {error && <p className="text-sm text-red-600">{error}</p>}

          <button
            type="submit"
            className="w-full bg-gradient-to-b from-[#E1A624] to-[#7B5B14] text-white font-bold py-4 rounded-full hover:from-[#D4991F] hover:to-[#A6851A] focus:outline-none transition-all duration-200 shadow-lg">
            Reset password
          </button>
        </form>
      </div>

      {/* Help Modal */}
      {showHelp && (
          <div className="fixed bottom-20 right-6 bg-white rounded-lg shadow-xl p-4 w-80 border border-gray-200">
            <div className="flex justify-between items-start mb-3">
              <h3 className="text-lg font-bold text-gray-800">Set New Password Help</h3>
              <button onClick={() => setShowHelp(false)} className="text-gray-500 hover:text-gray-700 text-xl font-bold">
                ×
              </button>
            </div>
            <div className="text-sm text-gray-600 space-y-2">
              <p>
                <strong>How to set your new password:</strong>
              </p>
              <ol className="list-decimal list-inside space-y-1 ml-2">
                <li>Enter your new password in the field above</li>
                <li>Confirm it</li>
                <li>Click the on the button to set it</li>
              </ol>
            </div>
          </div>
      )}

      {/* Overlay to close help when clicking outside */}
      {showHelp && <div className="fixed inset-0 z-[-1]" onClick={() => setShowHelp(false)} />}
    </div>
  );
};

export default SetNewPassword;

