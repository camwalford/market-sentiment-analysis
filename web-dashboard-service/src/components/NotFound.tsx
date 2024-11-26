// components/NotFound.tsx
import React from 'react';
import { Link } from 'react-router-dom';
import { Messages } from '../messages/eng';

const NotFound: React.FC = () => {
    return (
        <div className="text-center py-16">
            <h1 className="text-4xl font-bold mb-4">
                {Messages.NOT_FOUND_TITLE}
            </h1>
            <p className="text-gray-600 mb-8">
                {Messages.NOT_FOUND_MESSAGE}
            </p>
            <Link
                to="/dashboard"
                className="text-blue-500 hover:text-blue-600 underline"
            >
                {Messages.RETURN_TO_DASHBOARD}
            </Link>
        </div>
    );
};

export default NotFound;