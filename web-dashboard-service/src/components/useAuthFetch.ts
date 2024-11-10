// useAuthFetch.ts
import { useAuth } from './AuthProvider';

export const useAuthFetch = () => {
    const { auth, refreshAccessToken } = useAuth();

    const fetchWithAuth = async (url: string, options: RequestInit = {}) => {
        let accessToken = auth.accessToken;

        // Check if the access token is expired
        if (isTokenExpired(accessToken)) {
            try {
                // Refresh the access token
                accessToken = await refreshAccessToken();
            } catch (error) {
                throw new Error('Session expired. Please log in again.');
            }
        }

        // Include the updated access token in headers
        const headers = {
            ...options.headers,
            Authorization: `Bearer ${accessToken}`,
        };

        return fetch(url, { ...options, headers });
    };

    return fetchWithAuth;
};

function isTokenExpired(token: string | null): boolean {
    if (!token) return true;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiry = payload.exp;
        const now = Math.floor(Date.now() / 1000);
        return now >= expiry;
    } catch (error) {
        // If token is malformed or can't be decoded, treat it as expired
        return true;
    }
}
