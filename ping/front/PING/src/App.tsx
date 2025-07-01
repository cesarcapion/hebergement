import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup'
import ResetPasswordEmail from './pages/ResetPasswordEmail';
import SetNewPassword from './pages/SetNewPassword';
import QA from './pages/QA';
import MyTickets from "./pages/MyTickets.tsx";
import TicketDiscussion from "./pages/TicketDiscussion.tsx";
import CreateTicket from "./pages/CreateTicket.tsx";

import PrivateRoute from './PrivateRoute';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/reset-password" element={<ResetPasswordEmail />} />
                <Route path="/set-new-password" element={<SetNewPassword />} />

                <Route
                    path="/"
                    element={
                        <PrivateRoute>
                            <Home />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/my-tickets"
                    element={
                        <PrivateRoute>
                            <MyTickets />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/my-tickets/:id"
                    element={
                        <PrivateRoute>
                            <TicketDiscussion />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/qa"
                    element={
                        <PrivateRoute>
                            <QA />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/create-ticket"
                    element={
                        <PrivateRoute>
                            <CreateTicket />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;

