// components/Register.tsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import FormField from './common/FormField';
import {ErrorMessage} from './common/ErrorMessage';
import {LoadingSpinner} from './common/LoadingSpinner';

interface RegisterFormData {
    username: string;
    email: string;
    password: string;
}

const Register: React.FC = () => {
    const { handleRegister, auth } = useAuth();
    const [formData, setFormData] = useState<RegisterFormData>({
        username: '',
        email: '',
        password: '',
    });
    const [error, setError] = useState<string>('');
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        try {
            await handleRegister(
                formData.username,
                formData.email,
                formData.password
            );
            // Navigation is handled in AuthContext after successful registration
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Registration failed');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Sign up for an account
                    </h2>
                </div>

                <form onSubmit={onSubmit} className="mt-8 space-y-6">
                    {error && <ErrorMessage message={error} />}

                    <div className="rounded-md shadow-sm -space-y-px">
                        <FormField
                            id="username"
                            name="username"
                            type="text"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Username"
                            autoComplete="username"
                            isFirst
                        />

                        <FormField
                            id="email"
                            name="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Email address"
                            autoComplete="email"
                        />

                        <FormField
                            id="password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Password"
                            autoComplete="new-password"
                            isLast
                        />
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={auth.loading}
                            className={`
                                group relative w-full flex justify-center
                                py-2 px-4 border border-transparent text-sm font-medium
                                rounded-md text-white
                                ${auth.loading
                                ? "bg-blue-400 cursor-not-allowed"
                                : "bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                            }
                            `}
                        >
                            {auth.loading ? <LoadingSpinner /> : "Sign up"}
                        </button>
                    </div>
                </form>

                <div className="text-sm text-center">
                    <Link
                        to="/login"
                        className="font-medium text-blue-600 hover:text-blue-500"
                    >
                        Already have an account? Sign in
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Register;