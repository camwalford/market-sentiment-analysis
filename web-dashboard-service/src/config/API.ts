// config/API.ts
export const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";
export const USER_API_URL = API_URL + '/user'
export const USER_REQUESTS_API_URL = USER_API_URL + '/user-requests'
export const ENDPOINTS_API_URL = USER_API_URL + '/endpoint-requests'
export const USER_PROFILE_API_URL = USER_API_URL + '/me';
