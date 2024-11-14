// routes/PrivateRoute.tsx
import React, { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import MainLayout from "../layouts/MainLayout";
import { UserRole } from "../types/auth";

interface PrivateRouteProps {
    children: ReactNode;
    allowedRoles?: UserRole[];  // Make it optional and use our UserRole type
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children, allowedRoles }) => {
    const { auth } = useAuth();
    const location = useLocation();

    if (!auth.accessToken) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Role-based access check
    if (allowedRoles && allowedRoles.length > 0 && auth.user) {
        if (!allowedRoles.includes(auth.user.role)) {
            // Redirect to dashboard if user doesn't have required role
            return <Navigate to="/dashboard" replace />;
        }
    }

    return <MainLayout>{children}</MainLayout>;
};

export default PrivateRoute;