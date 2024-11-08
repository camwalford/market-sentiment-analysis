import React, {createContext, ReactNode, useContext, useState} from "react";
import {BrowserRouter as Router, useNavigate} from "react-router-dom";



const API_URL = "http://localhost:8080/api";

// Types
interface UserData {
    email: string;
    credits: number;
    role: string;
}

interface LoginResponse {
    accessToken: string;
    refreshToken: string;
    user: UserData;
}

interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserData | null;
    loading: boolean;
}

interface AuthContextType {
    auth: AuthState;
    handleLogin: (email: string, password: string) => Promise<void>;
    handleLogout: () => Promise<void>;
    handleForgotPassword: (email: string) => Promise<void>;
    handleRegister: (email: string, password: string) => Promise<void>;
}

// API Functions
async function register(email: string, password: string): Promise<void> {
    const response = await fetch(`${API_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Registration failed");
    }
}

// API Functions
async function login(email: string, password: string): Promise<LoginResponse> {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Login failed");
    }

    return await response.json();
}

async function logout(refreshToken: string): Promise<void> {
    const response = await fetch(`${API_URL}/auth/logout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: refreshToken }),
    });

    if (!response.ok) {
        throw new Error("Logout failed");
    }
}

async function forgotPassword(email: string): Promise<void> {
    const response = await fetch(`${API_URL}/auth/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Failed to process forgot password request");
    }
}

// Auth Context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};

// AuthProvider with Navigate
const AuthProviderWithNavigate: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [auth, setAuth] = useState<AuthState>({
        accessToken: null,
        refreshToken: null,
        user: null,
        loading: false,
    });
    const navigate = useNavigate();

    const handleLogin = async (email: string, password: string) => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            const response = await login(email, password);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            navigate('/');
        } catch (error) {
            setAuth(prev => ({ ...prev, loading: false }));
            throw error;
        }
    };

    const handleLogout = async () => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            if (auth.refreshToken) {
                await logout(auth.refreshToken);
            }
            setAuth({
                accessToken: null,
                refreshToken: null,
                user: null,
                loading: false,
            });
            navigate('/login');
        } catch (error) {
            setAuth(prev => ({ ...prev, loading: false }));
            throw error;
        }
    };

    const handleForgotPassword = async (email: string) => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            await forgotPassword(email);
            setAuth(prev => ({ ...prev, loading: false }));
        } catch (error) {
            setAuth(prev => ({ ...prev, loading: false }));
            throw error;
        }
    };

    const handleRegister = async (email: string, password: string) => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            await register(email, password);
            // After successful registration, automatically log them in
            const response = await login(email, password);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            navigate('/');
        } catch (error) {
            setAuth(prev => ({ ...prev, loading: false }));
            throw error;
        }
    };

    return (
        <AuthContext.Provider value={{ auth, handleLogin, handleLogout, handleForgotPassword, handleRegister }}>
            {children}
        </AuthContext.Provider>
    );
};

// Auth Provider
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    return (
            <AuthProviderWithNavigate>{children}</AuthProviderWithNavigate>
    );
};