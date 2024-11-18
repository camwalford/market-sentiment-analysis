// hooks/useAuthFetch.ts
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import AuthAPI from '../services/authAPI';

export const useAuthFetch = () => {
    const { auth, handleLogout } = useAuth();
    const navigate = useNavigate();

    const fetchWithAuth = async (
        url: string,
        options: RequestInit = {}
    ) => {
        const headers = {
            ...options.headers,
            'Content-Type': 'application/json',
        };

        try {
            const response = await fetch(url, {
                ...options,
                headers,
                credentials: 'include', // Important for cookies
            });

            if (response.status === 401) {
                // Try to refresh the token
                try {
                    await AuthAPI.refreshToken();
                    // Retry the original request
                    const retryResponse = await fetch(url, {
                        ...options,
                        headers,
                        credentials: 'include',
                    });

                    if (!retryResponse.ok) {
                        throw new Error('Request failed after token refresh');
                    }

                    return retryResponse;
                } catch (error) {
                    // If refresh fails, logout
                    await handleLogout();
                    navigate('/login');
                    throw new Error('Session expired');
                }
            }

            if (!response.ok) {
                throw new Error('Request failed');
            }

            return response;
        } catch (error) {
            // Handle network errors
            console.error('Fetch error:', error);
            throw error;
        }
    };

    return { fetchWithAuth };
};