// src/App.tsx
import React from 'react';
import { useAuth } from './AuthProvider';
import Login from './Login';
import Dashboard from './Dashboard';

const App: React.FC = () => {
    const { auth } = useAuth(); // Will not throw error if wrapped in AuthProvider

    return (
        <div>
            {auth.accessToken ? <Dashboard /> : <Login />}
        </div>
    );
};

export default App;