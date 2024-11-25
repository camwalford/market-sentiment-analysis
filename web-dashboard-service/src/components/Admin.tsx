import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useAuthFetch } from '../hooks/useAuthFetch';
import API_URL from '../config/API';
import {UserRole} from "../types/auth";

const USER_API_URL = API_URL + '/user';
const USER_REQUESTS_API_URL = USER_API_URL + '/user-requests';
const ENDPOINT_API_URL = USER_API_URL + '/endpoint-requests';

interface Endpoint {
    method: string;
    uri: string;
    totalRequests: number;
}

interface User {
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
    const [endpoints, setEndpoints] = useState([]);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(USER_REQUESTS_API_URL);
            const data = await response.json();
            setUsers(data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEndpoints();
    }, []);

    const fetchEndpoints = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(ENDPOINT_API_URL);
            const data = await response.json();
            setEndpoints(data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch endpoints');
        } finally {
            setLoading(false);
        }
    };

    const deleteUser = async (userId: number) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await fetchWithAuth(`${USER_API_URL}/${userId}`, {
                    method: 'DELETE',
                });
                setUsers(users.filter(user => user.userId !== userId));
            } catch (err) {
                setError('Failed to delete user');
            }
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Admin Dashboard</h1>
            <div className="mb-4">
                <h2 className="text-xl font-semibold">User Management</h2>
                <table className="min-w-full bg-white">
                    <thead>
                    <tr>
                        <th className="py-2 px-4 border-b">ID</th>
                        <th className="py-2 px-4 border-b">Username</th>
                        <th className="py-2 px-4 border-b">Email</th>
                        <th className="py-2 px-4 border-b">Role</th>
                        <th className="py-2 px-4 border-b">Credits</th>
                        <th className="py-2 px-4 border-b">Requests</th>
                        <th className="py-2 px-4 border-b">Actions</th>
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
                                <button
                                    onClick={() => deleteUser(user.userId)}
                                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-2 rounded"
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-semibold">Endpoint Management</h2>
                <table className="min-w-full bg-white">
                    <thead>
                    <tr>
                        <th className="py-2 px-4 border-b">METHOD</th>
                        <th className="py-2 px-4 border-b">URI</th>
                        <th className="py-2 px-4 border-b">Requests</th>
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
};

export default AdminDashboard;