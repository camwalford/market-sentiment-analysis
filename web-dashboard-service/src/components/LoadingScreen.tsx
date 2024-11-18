import React from "react";

const LoadingScreen: React.FC = () => (
    <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600" />
    </div>
);

export default LoadingScreen;