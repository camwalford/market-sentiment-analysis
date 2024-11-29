// services/userService.ts
import {USER_API_URL} from "../config/API";

export const deleteUser = async (userId: number, fetchWithAuth: any) => {
    const response = await fetchWithAuth(`${USER_API_URL}/${userId}`, {
        method: 'DELETE',
    });
    return response;
};

export const setCredits = async (userId: number, amount: number, fetchWithAuth: any) => {
    const response = await fetchWithAuth(`${USER_API_URL}/${userId}/credits/${amount}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
    });
    return response;
};