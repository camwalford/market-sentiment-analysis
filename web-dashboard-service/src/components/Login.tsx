// components/Login.tsx
import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import FormField from "./common/FormField";
import { ErrorMessage } from "./common/ErrorMessage";
import { LoadingSpinner } from "./common/LoadingSpinner";

interface LoginFormData {
    username: string;
    password: string;
}

const Login: React.FC = () => {
    const { handleLogin, auth } = useAuth();
    const [formData, setFormData] = useState<LoginFormData>({
        username: "",
        password: "",
    });
    const [error, setError] = useState<string>("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        try {
            await handleLogin(formData.username, formData.password);
        } catch (error) {
            setError(error instanceof Error ? error.message : "An unexpected error occurred");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Sign in to your account
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
                            id="password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Password"
                            autoComplete="current-password"
                            isLast
                        />
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={auth.loading}
                            className={`
                                group relative w-full flex justify-center 
                                py-2 px-4 border border-transparent 
                                text-sm font-medium rounded-md text-white
                                transition-colors duration-150
                                ${auth.loading
                                ? "bg-blue-400 cursor-not-allowed"
                                : "bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                            }
                            `}
                        >
                            {auth.loading ? <LoadingSpinner /> : "Sign in"}
                        </button>
                    </div>
                </form>

                <div className="flex flex-col space-y-4 text-sm text-center">
                    <Link
                        to="/register"
                        className="font-medium text-blue-600 hover:text-blue-500 transition-colors duration-150"
                    >
                        Don't have an account? Sign up
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Login;