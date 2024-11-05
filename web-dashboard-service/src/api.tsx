// src/api.ts
import {jwtDecode} from "jwt-decode";

const API_URL = "http://localhost:8080/api";

type AuthResponse = {
    accessToken: string;
    refreshToken: string;
};

type UserData = {
    email: string;
    credits: number;
    role: string;
};

// Function to decode JWT and extract user data
export function parseJwt(accessToken: string): UserData | null {
    try {
        const decoded: any = jwtDecode(accessToken);
        console.log("Decoded JWT:", decoded);
        return {
            email: decoded.email,
            credits: decoded.credits,
            role: decoded.role,
        };
    } catch (error) {
        console.error("Failed to parse JWT:", error);
        return null;
    }
}

export async function login(email: string, password: string): Promise<AuthResponse> {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });
    if (!response.ok) throw new Error("Failed to login");
    return response.json();
}

export async function refreshToken(refreshToken: string): Promise<{ accessToken: string }> {
    const response = await fetch(`${API_URL}/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: refreshToken }),
    });
    if (!response.ok) throw new Error("Failed to refresh token");
    return response.json();
}
