// components/UserProfile.tsx
import React, { useState, useEffect } from 'react';
import { useAuthFetch } from '../hooks/useAuthFetch';
import API_URL from '../config/API';
import { Messages } from '../messages/eng';

const USER_PROFILE_API_URL = API_URL + '/user/me';

interface UserProfileData {
    id: number;
    username: string;
    email: string;
    role: string;
    credits: number;
    totalRequests: number;
}

const UserProfile: React.FC = () => {
    const [profile, setProfile] = useState<UserProfileData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { fetchWithAuth } = useAuthFetch();

    const fetchProfile = async () => {
        try {
            setLoading(true);
            const response = await fetchWithAuth(`${USER_PROFILE_API_URL}`);
            const data = await response.json();
            setProfile(data);
            setError(null);
        } catch (err) {
            setError(Messages.USER_PROFILE_FETCH_ERROR);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProfile().catch(err => {
            console.error('Error fetching user profile:', err);
            setError(Messages.USER_PROFILE_FETCH_ERROR);
        });
    }, []);

    if (loading) return <div>{Messages.USER_PROFILE_LOADING}</div>;
    if (error) return <div>{Messages.ERROR_PREFIX}{error}</div>;
    if (!profile) return <div>{Messages.USER_PROFILE_NO_DATA}</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">{Messages.USER_PROFILE_TITLE}</h1>
            <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {Messages.USER_PROFILE_USERNAME_LABEL}
                    </label>
                    <p className="text-gray-700">{profile.username}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {Messages.USER_PROFILE_EMAIL_LABEL}
                    </label>
                    <p className="text-gray-700">{profile.email}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {Messages.USER_PROFILE_ROLE_LABEL}
                    </label>
                    <p className="text-gray-700">{profile.role}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {Messages.USER_PROFILE_CREDITS_LABEL}
                    </label>
                    <p className="text-gray-700">{profile.credits}</p>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {Messages.USER_PROFILE_TOTAL_REQUESTS_LABEL}
                    </label>
                    <p className="text-gray-700">{profile.totalRequests}</p>
                </div>
            </div>
        </div>
    );
};

export default UserProfile;