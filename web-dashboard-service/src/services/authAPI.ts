// services/authAPI.ts
import API_URL from "../config/API";
import { LoginResponse, User, ApiError } from "../types/auth";
import { Messages } from "../messages/eng";

class AuthAPI {
    private static async handleResponse<T>(response: Response): Promise<T> {
        const text = await response.text();

        if (!response.ok) {
            const errorData: ApiError = text ? JSON.parse(text) : {
                message: Messages.API_DEFAULT_ERROR,
                status: response.status
            };
            throw new Error(errorData.message);
        }

        return text ? JSON.parse(text) : {};
    }


    private static getHeaders(): HeadersInit {
        return {
            "Content-Type": "application/json",
        };
    }

    private static async fetchWithCredentials<T>(
        endpoint: string,
        options: RequestInit = {}
    ): Promise<T> {
        const response = await fetch(`${API_URL}${endpoint}`, {
            ...options,
            headers: this.getHeaders(),
            credentials: 'include',
        });
        return this.handleResponse<T>(response);
    }

    static async register(
        username: string,
        email: string,
        password: string
    ): Promise<User> {
        return this.fetchWithCredentials<User>('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password }),
        });
    }

    static async login(
        username: string,
        password: string
    ): Promise<LoginResponse> {
        return this.fetchWithCredentials<LoginResponse>('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
        });
    }

    static async logout(): Promise<void> {
        return this.fetchWithCredentials<void>('/auth/logout', {
            method: 'POST',
        });
    }

    static async validateSession(): Promise<User> {
        return this.fetchWithCredentials<User>('/auth/validate', {
            method: 'POST',
        });
    }

    static async refreshToken(): Promise<void> {
        return this.fetchWithCredentials<void>('/auth/refresh', {
            method: 'POST',
        });
    }
}

export default AuthAPI;