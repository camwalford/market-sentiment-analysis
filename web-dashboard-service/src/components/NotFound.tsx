// components/NotFound.tsx
import React from 'react';
import { Link } from 'react-router-dom';

const NotFound: React.FC = () => {
    return (
        <div className="text-center py-16">
            <h1 className="text-4xl font-bold mb-4">404 - Page Not Found</h1>
            <p className="text-gray-600 mb-8">
                The page you're looking for doesn't exist or has been moved.
            </p>
            <Link
                to="/dashboard"
                className="text-blue-500 hover:text-blue-600 underline"
            >
                Return to Dashboard
            </Link>
        </div>
    );
};

export default NotFound;