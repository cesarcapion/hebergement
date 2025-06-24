import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup'
import ResetPasswordEmail from './pages/ResetPasswordEmail';
import SetNewPassword from './pages/SetNewPassword';
import QA from './pages/QA';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
  	<Route path="/signup" element={<Signup />} />
  	<Route path="/reset-password" element={<ResetPasswordEmail />} />
        <Route path="/set-new-password" element={<SetNewPassword />} />
	<Route path="/qa" element={<QA />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

