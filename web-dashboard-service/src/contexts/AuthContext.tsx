// contexts/AuthContext.tsx
import React, { createContext, ReactNode, useContext, useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { AuthState, AuthContextType } from "../types/auth";
import AuthAPI from "../services/authAPI";
import { Messages } from "../messages/eng";
import {User} from "../types/user";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error(Messages.AUTH_CONTEXT_ERROR);
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
            console.log(Messages.SESSION_VALIDATION_DEBUG, error);
            setAuth({
                user: null,
                loading: false,
                error: error instanceof Error ? error.message : Messages.SESSION_VALIDATION_ERROR,
            });
            return false;
        }
    }, []);

    useEffect(() => {
        validateSession().catch(error => {
            console.error(Messages.SESSION_VALIDATION_ERROR_DEBUG, error);
        });
    }, [validateSession]);

    const handleLogin = async (username: string, password: string) => {
        console.log(Messages.LOGIN_DEBUG);
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
                error: error instanceof Error ? error.message : Messages.LOGIN_ERROR,
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
                error: error instanceof Error ? error.message : Messages.REGISTRATION_ERROR,
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