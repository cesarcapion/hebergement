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
import AnswerTicketAdmin from './pages/AnswerTicketAdmin.tsx';
import MyTicketsDev from "./pages/MyTicketsDev.tsx";

import PrivateRoute from './PrivateRoute';
import AdminRoute from './AdminRoute.tsx';
import StatsPage from "./pages/Stats.tsx";
import AnswerTicket from "./pages/AnswerTicket.tsx";
import Profile from "./pages/Profile.tsx"
import ProfileAdmin from "./pages/ProfileAdmin.tsx"
import HomeAdmin from "./pages/HomeAdmin.tsx";
import QAAdmin from "./pages/QAAdmin.tsx";
import ManagePage from "./pages/Manage.tsx";
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
                    path="/my-tickets/dev"
                    element={
                        <PrivateRoute>
                            <MyTicketsDev />
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
                    path="/answer/:id/:count"
                    element={
                    <PrivateRoute>
                        <AnswerTicket />
                    </PrivateRoute>
                    }
                />
                <Route
                    path="/answer/admin/:id/:count"
                    element={
                    <PrivateRoute>
                        <AnswerTicketAdmin />
                    </PrivateRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <PrivateRoute>
                            <Profile />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/profile/admin"
                    element={
                        <AdminRoute>
                            <ProfileAdmin />
                        </AdminRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <AdminRoute>
                            <HomeAdmin />
                        </AdminRoute>
                    }
                />
                <Route
                    path="/manage"
                    element={
                        <AdminRoute>
                            <ManagePage />
                        </AdminRoute>
                    }
                />
                <Route
                    path="/qa/admin"
                    element={
                        <AdminRoute>
                            <QAAdmin />
                        </AdminRoute>
                    }
                />


            </Routes>
        </BrowserRouter>
    );
}

export default App;

