import React, { useState, useEffect } from 'react';
import { Messages } from '../messages/eng';
import { useAuthFetch } from "../hooks/useAuthFetch";
import { ENDPOINTS_API_URL, USER_API_URL, USER_REQUESTS_API_URL } from "../config/API";
import { useAuth } from "../contexts/AuthContext";
import {UserRole} from "../types/user";

export interface User {
    userId: number;
    username: string;
    email: string;
    role: UserRole;
    credits: number;
    totalRequests: number;
}

const AdminDashboard: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { auth } = useAuth();
    const { fetchWithAuth } = useAuthFetch();
    const [endpoints, setEndpoints] = useState<any[]>([]);
    const [creditInputs, setCreditInputs] = useState<{ [key: number]: string }>({});
    const [creditErrors, setCreditErrors] = useState<{ [key: number]: string }>({});
    const [settingCredits, setSettingCredits] = useState<{ [key: number]: boolean }>({});

    useEffect(() => {
        fetchUsers();
        fetchEndpoints();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(USER_REQUESTS_API_URL);
            if (!response.ok) {
                throw new Error(Messages.FETCH_USERS_ERROR);
            }
            const data: User[] = await response.json();
            setUsers(data);
            setError(null);
        } catch (err) {
            setError(Messages.FETCH_USERS_ERROR);
        } finally {
            setLoading(false);
        }
    };

    const fetchEndpoints = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(ENDPOINTS_API_URL);
            if (!response.ok) {
                throw new Error(Messages.FETCH_ENDPOINTS_ERROR);
            }
            const data = await response.json();
            setEndpoints(data);
            setError(null);
        } catch (err) {
            setError(Messages.FETCH_ENDPOINTS_ERROR);
        } finally {
            setLoading(false);
        }
    };

    const deleteUser = async (userId: number) => {
        if (window.confirm(Messages.DELETE_USER_CONFIRMATION)) {
            try {
                await fetchWithAuth(`${USER_API_URL}/${userId}`, {
                    method: 'DELETE',
                });
                setUsers(users.filter(user => user.userId !== userId));
            } catch (err) {
                setError(Messages.DELETE_USER_ERROR);
            }
        }
    };

    const handleCreditInputChange = (userId: number, value: string) => {
        setCreditInputs(prev => ({
            ...prev,
            [userId]: value
        }));
        setCreditErrors(prev => ({
            ...prev,
            [userId]: ''
        }));
    };

    const setCredits = async (userId: number) => {
        const inputValue = creditInputs[userId];
        const amount = parseInt(inputValue, 10);

        console.log('amount:', amount);
        console.log('inputValue:', inputValue);
        console.log('creditInputs:', creditInputs);

        if (isNaN(amount) || amount < 0) {
            setCreditErrors(prev => ({
                ...prev,
                [userId]: Messages.INVALID_CREDIT_AMOUNT
            }));
            return;
        }

        // Confirm action
        if (!window.confirm(Messages.SET_CREDITS_CONFIRMATION)) {
            return;
        }

        try {
            setSettingCredits(prev => ({
                ...prev,
                [userId]: true
            }));

            console.log( 'userId:', userId );
            console.log( 'amount:', amount );
            console.log( 'USER_API_URL:', USER_API_URL );
            // Construct the endpoint with actual userId and amount
            const endpoint = `${USER_API_URL}/${userId}/credits/${amount}`;

            const response = await fetchWithAuth(endpoint, {
                method: 'PATCH',
            });

            if (!response.ok) {
                throw new Error(Messages.SET_CREDITS_ERROR);
            }

            // Optionally, you can get the updated user data from the response
            const updatedUser: User = await response.json();

            // Update the user's credits in the local state
            setUsers(prevUsers =>
                prevUsers.map(user =>
                    user.userId === userId ? { ...user, credits: updatedUser.credits} : user
                )
            );

            // Clear the input
            setCreditInputs(prev => ({
                ...prev,
                [userId]: ''
            }));

            setCreditErrors(prev => ({
                ...prev,
                [userId]: ''
            }));

        } catch (err) {
            setCreditErrors(prev => ({
                ...prev,
                [userId]: Messages.SET_CREDITS_ERROR
            }));
        } finally {
            setSettingCredits(prev => ({
                ...prev,
                [userId]: false
            }));
        }
    };

    if (loading) return <div>{Messages.LOADING_MESSAGE}</div>;
    if (error) return <div>{Messages.ERROR_PREFIX}{error}</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">{Messages.ADMIN_DASHBOARD_TITLE}</h1>
            <div className="mb-4">
                <h2 className="text-xl font-semibold">{Messages.USER_MANAGEMENT_TITLE}</h2>
                <table className="min-w-full bg-white">
                    <thead>
                    <tr>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_ID}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_USERNAME}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_EMAIL}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_ROLE}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_CREDITS}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_REQUESTS}</th>
                        <th className="py-2 px-4 border-b">{Messages.USER_TABLE_ACTIONS}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map(user => (
                        <tr key={user.userId}>
                            <td className="py-2 px-4 border-b">{user.userId}</td>
                            <td className="py-2 px-4 border-b">{user.username}</td>
                            <td className="py-2 px-4 border-b">{user.email}</td>
                            <td className="py-2 px-4 border-b">{user.role}</td>
                            <td className="py-2 px-4 border-b">{user.credits}</td>
                            <td className="py-2 px-4 border-b">{user.totalRequests}</td>
                            <td className="py-2 px-4 border-b">
                                <div className="flex flex-col space-y-2">
                                    <div className="flex items-center space-x-2">
                                        <button
                                            onClick={() => deleteUser(user.userId)}
                                            className="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-2 rounded"
                                        >
                                            {Messages.DELETE_BUTTON}
                                        </button>
                                        <input
                                            type="number"
                                            name={`set-credits-${user.userId}`}
                                            value={creditInputs[user.userId] || ''}
                                            placeholder={user.credits.toString()}
                                            className="border border-gray-400 p-1 w-24"
                                            min="0"
                                            onChange={(e) => handleCreditInputChange(user.userId, e.target.value)}
                                        />
                                        <button
                                            onClick={() => setCredits(user.userId)}
                                            className={`bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-2 rounded ${
                                                settingCredits[user.userId] ? 'opacity-50 cursor-not-allowed' : ''
                                            }`}
                                            disabled={settingCredits[user.userId]}
                                        >
                                            {settingCredits[user.userId] ? Messages.SETTING_CREDITS : Messages.SET_CREDITS}
                                        </button>
                                    </div>
                                    {creditErrors[user.userId] && (
                                        <div className="text-red-500 text-sm">
                                            {creditErrors[user.userId]}
                                        </div>
                                    )}
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-semibold">{Messages.ENDPOINT_MANAGEMENT_TITLE}</h2>
                <table className="min-w-full bg-white">
                    <thead>
                    <tr>
                        <th className="py-2 px-4 border-b">{Messages.ENDPOINT_TABLE_METHOD}</th>
                        <th className="py-2 px-4 border-b">{Messages.ENDPOINT_TABLE_URI}</th>
                        <th className="py-2 px-4 border-b">{Messages.ENDPOINT_TABLE_REQUESTS}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {endpoints.map((endpoint: any) => (
                        <tr key={endpoint.id}>
                            <td className="py-2 px-4 border-b">{endpoint.method}</td>
                            <td className="py-2 px-4 border-b">{endpoint.uri}</td>
                            <td className="py-2 px-4 border-b">{endpoint.totalRequests}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default AdminDashboard;
