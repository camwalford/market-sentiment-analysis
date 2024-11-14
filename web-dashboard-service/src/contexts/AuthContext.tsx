import React, { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthState, AuthContextType } from "../types/auth";
import { AuthStorage } from "../utils/storage";
import AuthAPI from "../services/authAPI";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthContext");
    return context;
};

const AuthProviderWithNavigate: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [auth, setAuth] = useState<AuthState>({
        accessToken: null,
        refreshToken: null,
        user: null,
        loading: false,
    });
    const navigate = useNavigate();

    useEffect(() => {
        const storedAuth = AuthStorage.getStoredAuth();
        if (storedAuth) {
            setAuth({
                ...storedAuth,
                loading: false,
            });
        }
    }, []);

    const setLoading = (loading: boolean) => {
        setAuth(prev => ({ ...prev, loading }));
    };

    const deductCredits = (amount: number) => {
        setAuth(prevAuth => {
            if (!prevAuth.user) return prevAuth;

            const updatedUser = {
                ...prevAuth.user,
                credits: prevAuth.user.credits - amount
            };
            AuthStorage.updateUser(updatedUser);

            return {
                ...prevAuth,
                user: updatedUser,
            };
        });
    };

    const handleLogin = async (username: string, password: string) => {
        setLoading(true);
        try {
            const response = await AuthAPI.login(username, password);
            AuthStorage.saveTokens(response.accessToken, response.refreshToken, response.user);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            navigate('/');
        } catch (error) {
            setLoading(false);
            throw error;
        }
    };

    const handleLogout = async () => {
        setLoading(true);
        try {
            if (auth.refreshToken) {
                await AuthAPI.logout(auth.refreshToken);
            }
            // Clear local storage and state even if the API call fails
            AuthStorage.clearTokens();
            setAuth({
                accessToken: null,
                refreshToken: null,
                user: null,
                loading: false,
            });
            navigate('/login');
        } catch (error) {
            console.error('Logout error:', error);
            // Still clear everything locally even if the API call fails
            AuthStorage.clearTokens();
            setAuth({
                accessToken: null,
                refreshToken: null,
                user: null,
                loading: false,
            });
            navigate('/login');
        }
    };

    const handleForgotPassword = async (email: string) => {
        setLoading(true);
        try {
            await AuthAPI.forgotPassword(email);
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async (username: string, email: string, password: string) => {
        setLoading(true);
        try {
            await AuthAPI.register(username, email, password);
            const response = await AuthAPI.login(email, password);
            AuthStorage.saveTokens(response.accessToken, response.refreshToken, response.user);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            navigate('/');
        } catch (error) {
            setLoading(false);
            throw error;
        }
    };

    const refreshAccessToken = async () => {
        try {
            if (!auth.refreshToken) throw new Error("No refresh token available");
            const newAccessToken = await AuthAPI.refreshToken(auth.refreshToken);
            AuthStorage.updateAccessToken(newAccessToken);
            setAuth(prev => ({ ...prev, accessToken: newAccessToken }));
            return newAccessToken;
        } catch (error) {
            await handleLogout();
            throw error;
        }
    };

    const setCredits = (newCredits: number) => {
        setAuth(prevAuth => {
            if (!prevAuth.user) return prevAuth;

            const updatedUser = { ...prevAuth.user, credits: newCredits };
            AuthStorage.updateUser(updatedUser);
            return { ...prevAuth, user: updatedUser };
        });
    };

    const contextValue: AuthContextType = {
        auth,
        handleLogin,
        handleLogout,
        handleForgotPassword,
        handleRegister,
        deductCredits,
        refreshAccessToken,
        setCredits
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