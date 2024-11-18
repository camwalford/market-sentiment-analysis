// components/UserProfile.tsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useAuthFetch } from '../hooks/useAuthFetch';
import API_URL from '../config/API';

const USER_PROFILE_API_URL = API_URL + '/user/me';

interface UserProfileData {
    id: number;
    username: string;
    email: string;
    role: string;
    credits: number;
    requests: number;
}

const UserProfile: React.FC = () => {
    const [profile, setProfile] = useState<UserProfileData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { auth } = useAuth();
    const { fetchWithAuth } = useAuthFetch();

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(`${USER_PROFILE_API_URL}`);
            const data = await response.json();
            setProfile(data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch user profile');
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!profile) return <div>No profile data available</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">User Profile</h1>
            <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        Username
                    </label>
                    <p className="text-gray-700">{profile.username}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        Email
                    </label>
                    <p className="text-gray-700">{profile.email}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        Role
                    </label>
                    <p className="text-gray-700">{profile.role}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        Credits
                    </label>
                    <p className="text-gray-700">{profile.credits}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        Total Requests
                    </label>
                    <p className="text-gray-700">{profile.requests}</p>
                </div>
            </div>
        </div>
    );
};

export default UserProfile;