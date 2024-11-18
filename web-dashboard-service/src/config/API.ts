// config/API.ts
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

export const API_CONFIG = {
    baseURL: API_URL,
    defaultOptions: {
        credentials: 'include' as RequestCredentials,
        headers: {
            'Content-Type': 'application/json',
        },
    },
};

export default API_URL;