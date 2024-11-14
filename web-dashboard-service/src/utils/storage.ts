// utils/storage.ts
import {UserData} from "../types/auth";

export class AuthStorage {
    private static readonly ACCESS_TOKEN_KEY = 'accessToken';
    private static readonly REFRESH_TOKEN_KEY = 'refreshToken';
    private static readonly USER_KEY = 'user';

    static saveTokens(accessToken: string, refreshToken: string, user: UserData): void {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
        localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }

    static clearTokens(): void {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
    }

    static getStoredAuth(): { accessToken: string; refreshToken: string; user: UserData } | null {
        const accessToken = localStorage.getItem(this.ACCESS_TOKEN_KEY);
        const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
        const userStr = localStorage.getItem(this.USER_KEY);

        if (accessToken && refreshToken && userStr) {
            return {
                accessToken,
                refreshToken,
                user: JSON.parse(userStr)
            };
        }
        return null;
    }

    static updateUser(user: UserData): void {
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }

    static updateAccessToken(accessToken: string): void {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    }
}