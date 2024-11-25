// contexts/AuthContext.tsx
import React, { createContext, ReactNode, useContext, useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { AuthState, AuthContextType, User } from "../types/auth";
import AuthAPI from "../services/authAPI";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthContext");
    return context;
};

const AuthProviderWithNavigate: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [auth, setAuth] = useState<AuthState>({
        user: null,
        loading: true,
        error: null,
    });
    const navigate = useNavigate();

    const validateSession = useCallback(async () => {
        try {
            setAuth(prev => ({ ...prev, loading: true }));
            const userData = await AuthAPI.validateSession();
            setAuth({
                user: userData,
                loading: false,
                error: null,
            });
            return true;
        } catch (error) {
            console.log("Session validation failed:", error);
            setAuth({
                user: null,
                loading: false,
                error: error instanceof Error ? error.message : "Session validation failed",
            });
            return false;
        }
    }, []);

    useEffect(() => {
        validateSession().catch(error => {
            console.error('Error during session validation:', error);
        });
    }, [validateSession]);




    const handleLogin = async (username: string, password: string) => {

        console.log("handleLogin");
        setAuth(prev => ({ ...prev, loading: true, error: null }));
        try {
            const response = await AuthAPI.login(username, password);
            setAuth({
                user: response.user,
                loading: false,
                error: null,
            });
            navigate('/dashboard');
        } catch (error) {
            setAuth(prev => ({
                ...prev,
                loading: false,
                error: error instanceof Error ? error.message : "Login failed",
            }));
            throw error;
        }
    };

    const handleLogout = async () => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            await AuthAPI.logout();
        } finally {
            setAuth({
                user: null,
                loading: false,
                error: null,
            });
            navigate('/login');
        }
    };

    const handleRegister = async (username: string, email: string, password: string) => {
        setAuth(prev => ({ ...prev, loading: true, error: null }));
        try {
            await AuthAPI.register(username, email, password);
            setAuth(prev => ({
                ...prev,
                loading: false,
                error: null,
            }));
            navigate('/login');
        } catch (error) {
            setAuth(prev => ({
                ...prev,
                loading: false,
                error: error instanceof Error ? error.message : "Registration failed",
            }));
            throw error;
        }
    };


    const updateUserData = useCallback((userData: Partial<User>) => {
        setAuth(prev => ({
            ...prev,
            user: prev.user ? { ...prev.user, ...userData } : null,
        }));
    }, []);

    const clearError = useCallback(() => {
        setAuth(prev => ({ ...prev, error: null }));
    }, []);

    const contextValue: AuthContextType = {
        auth,
        handleLogin,
        handleLogout,
        handleRegister,
        updateUserData,
        validateSession,
        clearError,
    };

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    return <AuthProviderWithNavigate>{children}</AuthProviderWithNavigate>;
};