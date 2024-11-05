// src/api.ts
import {jwtDecode} from "jwt-decode";
import LoginResponse from "./types/LoginResponse";

const API_URL = "http://localhost:8080/api";


// // Function to decode JWT and extract user data
// export function parseJwt(accessToken: string): UserData | null {
//     try {
//         const decoded: any = jwtDecode(accessToken);
//         console.log("Decoded JWT:", decoded);
//         return {
//             email: decoded.email,
//             credits: decoded.credits,
//             role: decoded.role,
//         };
//     } catch (error) {
//         console.error("Failed to parse JWT:", error);
//         return null;
//     }
// }

export async function login(email: string, password: string): Promise<LoginResponse> {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        throw new Error("Login failed");
    }

    const data: LoginResponse = await response.json();
    console.log("Login Response Data: " + data)

    // Now data includes accessToken, refreshToken, and user
    return data;
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
