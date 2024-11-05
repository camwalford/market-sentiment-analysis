// src/AuthProvider.tsx
import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { login, refreshToken } from "./api";
import AuthState from "./types/AuthState";
import AuthContextType from "./types/AuthContextType"; // Import parseJwt




const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};

export function AuthProvider({ children }: { children: ReactNode }) {
    const [auth, setAuth] = useState<AuthState>({ accessToken: null, refreshToken: null, user: null });

    // AuthProvider.tsx

    const handleLogin = async (email: string, password: string) => {
        try {
            const { accessToken, refreshToken, user } = await login(email, password);
            setAuth({ accessToken, refreshToken, user });
        } catch (error) {
            console.error("Login failed:", error);
            // Handle login error (e.g., show an error message to the user)
        }
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
