import React, {createContext, ReactNode, useContext, useEffect, useState} from "react";
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
    handleRegister: (username: string, email: string, password: string) => Promise<void>;
    deductCredits: (amount: number) => void;
    refreshAccessToken: () => Promise<string>;
    setCredits: (newCredits: number) => void;
}

// API Functions
async function register(username:string, email: string, password: string): Promise<void> {
    const response = await fetch(`${API_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({username, email, password }),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Registration failed");
    }
}

// API Functions
async function login(username: string, password: string): Promise<LoginResponse> {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
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

async function getNewAccessToken(refreshToken: string): Promise<string> {
    const response = await fetch(`${API_URL}/auth/refresh-token`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
        throw new Error("Failed to refresh access token");
    }

    const data = await response.json();
    return data.accessToken;
}


// AuthProvider with Navigate
const AuthProviderWithNavigate: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [auth, setAuth] = useState<AuthState>({
        accessToken: null,
        refreshToken: null,
        user: null,
        loading: false,
    });
    const navigate = useNavigate();

    // Initialize auth state from localStorage
    useEffect(() => {
        const storedAccessToken = localStorage.getItem('accessToken');
        const storedRefreshToken = localStorage.getItem('refreshToken');
        const storedUser = localStorage.getItem('user');

        if (storedAccessToken && storedRefreshToken && storedUser) {
            setAuth({
                accessToken: storedAccessToken,
                refreshToken: storedRefreshToken,
                user: JSON.parse(storedUser),
                loading: false,
            });
        }
    }, []);

    const saveTokensToStorage = (accessToken: string, refreshToken: string, user: UserData) => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
    };

    const clearTokensFromStorage = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    };

    const deductCredits = (amount: number) => {
        setAuth(prevAuth => {
            const updatedUser = prevAuth.user
                ? { ...prevAuth.user, credits: prevAuth.user.credits - amount }
                : null;
            // Update user in localStorage
            if (updatedUser) {
                localStorage.setItem('user', JSON.stringify(updatedUser));
            }
            return {
                ...prevAuth,
                user: updatedUser,
            };
        });
    };

    const handleLogin = async (username: string, password: string) => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            const response = await login(username, password);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            saveTokensToStorage(response.accessToken, response.refreshToken, response.user);
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
            clearTokensFromStorage();
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

    const handleRegister = async (username: string, email: string, password: string) => {
        setAuth(prev => ({ ...prev, loading: true }));
        try {
            await register(username, email, password);
            // After successful registration, automatically log them in
            const response = await login(email, password);
            setAuth({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                user: response.user,
                loading: false,
            });
            saveTokensToStorage(response.accessToken, response.refreshToken, response.user);
            navigate('/');
        } catch (error) {
            setAuth(prev => ({ ...prev, loading: false }));
            throw error;
        }
    };

    const refreshAccessToken = async () => {
        try {
            if (!auth.refreshToken) throw new Error("No refresh token available");
            const newAccessToken = await getNewAccessToken(auth.refreshToken);
            setAuth((prevAuth) => ({
                ...prevAuth,
                accessToken: newAccessToken,
            }));
            localStorage.setItem("accessToken", newAccessToken);
            return newAccessToken;
        } catch (error) {
            // If refresh fails, log out the user
            await handleLogout();
            throw error;
        }
    };

    const setCredits = (newCredits: number) => {
        setAuth((prevAuth) => {
            const updatedUser = prevAuth.user ? { ...prevAuth.user, credits: newCredits } : null;
            if (updatedUser) {
                localStorage.setItem("user", JSON.stringify(updatedUser));
            }
            return { ...prevAuth, user: updatedUser };
        });
    };


    return (
        <AuthContext.Provider
            value={{
                auth,
                handleLogin,
                handleLogout,
                handleForgotPassword,
                handleRegister,
                deductCredits,
                refreshAccessToken,
                setCredits // Add setCredits to the provider value
            }}
        >
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