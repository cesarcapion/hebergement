import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup'
import ResetPasswordEmail from './pages/ResetPasswordEmail';
import SetNewPassword from './pages/SetNewPassword';
import QA from './pages/QA';
import MyTickets from "./pages/MyTickets.tsx";
import MyTicketsAdmin from "./pages/MyTicketsAdmin.tsx";
import TicketDiscussion from "./pages/TicketDiscussion.tsx";
import TicketDiscussionAdmin from "./pages/TicketDiscussionAdmin.tsx";
import CreateTicket from "./pages/CreateTicket.tsx";

import PrivateRoute from './PrivateRoute';
import AdminRoute from './AdminRoute.tsx';

import StatsPage from "./pages/Stats.tsx";
import AnswerTicket from "./pages/AnswerTicket.tsx";
import AnswerTicketAdmin from "./pages/AnswerTicketAdmin.tsx";
import Profil from "./pages/Profile.tsx"

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
                    path="/my-tickets/admin"
                    element={
                        <PrivateRoute>
                            <MyTicketsAdmin />
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
                    path="/my-tickets/admin/:id"
                    element={
                        <PrivateRoute>
                            <TicketDiscussionAdmin />
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
                <Route
                    path="/stats"
                    element={
                        <AdminRoute>
                            <StatsPage />
                        </AdminRoute>
                    }
                />
                <Route
                    path="/answer/:id"
                    element={
                    <PrivateRoute>
                        <AnswerTicket />
                    </PrivateRoute>
                    }
                />
                <Route
                    path="/answer/admin/:id"
                    element={
                        <PrivateRoute>
                            <AnswerTicketAdmin />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/profil"
                    element={
                        <PrivateRoute>
                            <Profil />
                        </PrivateRoute>
                    }
                />


            </Routes>
        </BrowserRouter>
    );
}

export default App;

