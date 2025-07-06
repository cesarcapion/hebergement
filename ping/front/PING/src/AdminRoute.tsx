import type {JSX} from 'react';
import { Navigate } from 'react-router-dom';
// eslint-disable-next-line react-refresh/only-export-components
export const getUserGroupFromToken = (): string | null => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log("payload: " + payload.groups);
        return payload.groups || null;
    } catch {
        console.log("bon la g juste pas reussi a parser")
        return null;
    }
};

export const AdminRoute = ({ children }: { children: JSX.Element }) => {
    const token = localStorage.getItem('token');
    const group = getUserGroupFromToken();
    if (!token) return <Navigate to="/login" replace />;
    console.log("LE GROUPE EST \'" + group + "\'");
    if (group === null || group.toString() === "user") {
        console.log("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa")
        return <Navigate to="/" replace/>;
    }

    return children;
};
export default AdminRoute;
