// routes/PublicRoute.tsx
import React, { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import MainLayout from "../layouts/MainLayout";

interface PublicRouteProps {
    children: ReactNode;
}

const PublicRoute: React.FC<PublicRouteProps> = ({ children }) => {
    const { auth } = useAuth();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    if (auth.accessToken) {
        return <Navigate to={from} replace />;
    }

    return <MainLayout>{children}</MainLayout>;
};

export default PublicRoute;