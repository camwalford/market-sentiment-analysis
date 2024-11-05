// src/Dashboard.tsx
import React from "react";
import { useAuth } from "./AuthProvider";

const Dashboard: React.FC = () => {
    const { auth, handleLogout } = useAuth();
    if (!auth.user) return <div>Loading...</div>;

    return (
        <div>
            <h2>Welcome, {auth.user.email}</h2>
            <p>Credits: {auth.user.credits}</p>
            <p>Role: {auth.user.role}</p>
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
