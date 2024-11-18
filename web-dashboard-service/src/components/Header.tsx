// components/Header.tsx
import React from "react";
import { FaUserCircle } from "react-icons/fa";
import { useAuth } from "../contexts/AuthContext";

const UserInfo: React.FC<{ email: string; credits: number }> = ({ email, credits }) => (
    <div className="flex items-center space-x-4">
        <FaUserCircle className="text-3xl" />
        <div className="flex flex-col items-start">
            <span className="text-sm font-semibold">{email}</span>
            <span className="text-sm">Credits: {credits}</span>
        </div>
    </div>
);

const LogoutButton: React.FC<{ onClick: () => void }> = ({ onClick }) => (
    <button
        onClick={onClick}
        className="px-4 py-2 bg-red-600 rounded-md hover:bg-red-700 
                   focus:outline-none focus:ring-2 focus:ring-red-300 
                   focus:ring-offset-2 flex items-center space-x-2"
    >
        <span>Logout</span>
    </button>
);

const Logo: React.FC = () => (
    <div className="flex items-center space-x-3">
        <div className="h-10 w-10 bg-white rounded-full flex items-center justify-center">
            <span className="text-blue-800 font-bold text-xl">S</span>
        </div>
        <h1 className="text-2xl font-bold tracking-wide">
            Stock Sentiment Dashboard
        </h1>
    </div>
);

const Header: React.FC = () => {
    const { auth, handleLogout } = useAuth();

    return (
        <header className="p-4 bg-gradient-to-r from-blue-800 to-indigo-800 
                          text-white flex justify-between items-center shadow-md">
            <Logo />

            {auth.user && (
                <div className="flex items-center space-x-4">
                    <UserInfo
                        email={auth.user.email}
                        credits={auth.user.credits}
                    />
                    <LogoutButton onClick={handleLogout} />
                </div>
            )}
        </header>
    );
};

export default Header;