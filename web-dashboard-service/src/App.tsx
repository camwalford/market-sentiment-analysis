// App.tsx
import React from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { RouteConfig } from './config/routes';
import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';
import NotFound from './components/NotFound';
import LoadingScreen from './components/LoadingScreen';
import ErrorBoundary from './components/ErrorBoundary';

const App: React.FC = () => {
    return (
        <ErrorBoundary>
            <AuthProvider>
                <Routes>
                    {/* Public Routes */}
                    {RouteConfig.public.map(route => (
                        <Route
                            key={route.path}
                            path={route.path}
                            element={
                                <PublicRoute>
                                    {route.element}
                                </PublicRoute>
                            }
                        />
                    ))}

                    {/* Protected Routes */}
                    {RouteConfig.private.map(route => (
                        <Route
                            key={route.path}
                            path={route.path}
                            element={
                                <PrivateRoute allowedRoles={route.roles}>
                                    {route.element}
                                </PrivateRoute>
                            }
                        />
                    ))}

                    {/* Default Routes */}
                    <Route
                        path="/"
                        element={<Navigate to="/dashboard" replace />}
                    />
                    <Route
                        path="*"
                        element={
                            <NotFound />
                        }
                    />
                </Routes>
            </AuthProvider>
        </ErrorBoundary>
    );
};

export default App;