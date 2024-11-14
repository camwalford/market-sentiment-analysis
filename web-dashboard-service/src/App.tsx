// App.tsx
import React from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import StockSentimentDashboard from './components/StockSentimentDashboard';
import Register from './components/Register';
import Login from './components/Login';
import AdminDashboard from "./components/Admin";
import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';
import NotFound from './components/NotFound';
import { UserRole } from './types/auth';

const App: React.FC = () => {
    const adminRole: UserRole = 'admin';

    return (
        <AuthProvider>
            <Routes>
                {/* Public Routes */}
                <Route
                    path="/login"
                    element={
                        <PublicRoute>
                            <Login />
                        </PublicRoute>
                    }
                />
                <Route
                    path="/register"
                    element={
                        <PublicRoute>
                            <Register />
                        </PublicRoute>
                    }
                />

                {/* Protected Routes */}
                <Route
                    path="/admin"
                    element={
                        <PrivateRoute allowedRoles={[adminRole]}>
                            <AdminDashboard />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <StockSentimentDashboard />
                        </PrivateRoute>
                    }
                />

                {/* Redirect root to dashboard */}
                <Route
                    path="/"
                    element={<Navigate to="/dashboard" replace />}
                />

                {/* 404 Route */}
                <Route
                    path="*"
                    element={
                        <PrivateRoute>
                            <NotFound />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </AuthProvider>
    );
};

export default App;