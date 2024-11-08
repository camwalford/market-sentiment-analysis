// App.tsx
import React from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import StockSentimentDashboard from './components/StockSentimentDashboard';
import { AuthProvider } from './components/AuthProvider';
import Register from './components/Register';
import Login from './components/Login';
import Header from './components/Header';
import PrivateRoute from './components/PrivateRoute';

const App: React.FC = () => {
    return (
        <AuthProvider>
            <Header />
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                {/* Place the wildcard route last */}
                <Route
                    path="/"
                    element={
                        <PrivateRoute>
                            <StockSentimentDashboard />
                        </PrivateRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </AuthProvider>
    );
};

export default App;
