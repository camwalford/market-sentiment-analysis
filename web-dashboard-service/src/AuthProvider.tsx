// src/AuthProvider.tsx
import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { login, refreshToken, parseJwt } from "./api"; // Import parseJwt

type UserData = {
    email: string;
    credits: number;
    role: string;
};

type AuthState = {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserData | null;
};

type AuthContextType = {
    auth: AuthState;
    handleLogin: (email: string, password: string) => Promise<void>;
    handleLogout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};

export function AuthProvider({ children }: { children: ReactNode }) {
    const [auth, setAuth] = useState<AuthState>({ accessToken: null, refreshToken: null, user: null });

    const handleLogin = async (email: string, password: string) => {
        const { accessToken, refreshToken } = await login(email, password);

        // Decode JWT to extract user data
        const user = parseJwt(accessToken);

        setAuth({ accessToken, refreshToken, user });
    };

    const handleLogout = () => {
        setAuth({ accessToken: null, refreshToken: null, user: null });
    };

    const refreshAccessToken = async () => {
        if (!auth.refreshToken) return;
        const { accessToken } = await refreshToken(auth.refreshToken);

        // Update state with the new access token
        setAuth((prev) => ({ ...prev, accessToken }));
    };

    useEffect(() => {
        // Automatically refresh the access token every 15 minutes
        const interval = setInterval(() => {
            refreshAccessToken();
        }, 15 * 60 * 1000);

        return () => clearInterval(interval);
    }, [auth.refreshToken]);

    return (
        <AuthContext.Provider value={{ auth, handleLogin, handleLogout }}>
            {children}
        </AuthContext.Provider>
    );
}
