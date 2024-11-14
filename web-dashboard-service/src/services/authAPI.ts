// services/authAPI.ts
import API_URL from "../config/API";
import { LoginResponse, UserData } from "../types/auth";

class AuthAPI {
    private static async handleResponse<T>(response: Response, expectJson: boolean = true): Promise<T> {
        if (!response.ok) {
            // Try to parse error message if available
            const errorData = await response.text().then(text => {
                try {
                    return JSON.parse(text);
                } catch {
                    return { message: text || "Request failed" };
                }
            });
            throw new Error(errorData.message || "Request failed");
        }

        // Handle empty responses
        if (!expectJson) {
            return {} as T;
        }

        // Check if there's actually content to parse
        const text = await response.text();
        if (!text) {
            return {} as T;
        }

        try {
            return JSON.parse(text);
        } catch (error) {
            throw new Error("Invalid JSON response from server");
        }
    }

    private static getHeaders(): HeadersInit {
        return {
            "Content-Type": "application/json",
        };
    }

    static async register(username: string, email: string, password: string): Promise<void> {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: "POST",
            headers: this.getHeaders(),
            body: JSON.stringify({ username, email, password }),
        });
        await this.handleResponse<void>(response, false);
    }

    static async login(username: string, password: string): Promise<LoginResponse> {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: "POST",
            headers: this.getHeaders(),
            body: JSON.stringify({ username, password }),
        });
        return this.handleResponse<LoginResponse>(response);
    }

    static async logout(refreshToken: string): Promise<void> {
        const response = await fetch(`${API_URL}/auth/logout`, {
            method: "POST",
            headers: this.getHeaders(),
            body: JSON.stringify({ token: refreshToken }),
        });
        await this.handleResponse<void>(response, false); // Set expectJson to false
    }

    static async forgotPassword(email: string): Promise<void> {
        const response = await fetch(`${API_URL}/auth/forgot-password`, {
            method: "POST",
            headers: this.getHeaders(),
            body: JSON.stringify({ email }),
        });
        await this.handleResponse<void>(response, false);
    }

    static async refreshToken(refreshToken: string): Promise<string> {
        const response = await fetch(`${API_URL}/auth/refresh-token`, {
            method: "POST",
            headers: this.getHeaders(),
            body: JSON.stringify({ refreshToken }),
        });
        const data = await this.handleResponse<{ accessToken: string }>(response);
        return data.accessToken;
    }
}

export default AuthAPI;