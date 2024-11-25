import React, { ReactNode} from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import MainLayout from "../layouts/MainLayout";
import { UserRole } from "../types/auth";
import LoadingScreen from "../components/LoadingScreen";

interface PrivateRouteProps {
    children: ReactNode;
    allowedRoles?: UserRole[];
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children, allowedRoles }) => {
    const { auth } = useAuth();
    const location = useLocation();

    if (auth.loading) {
        console.log("Showing loading screen");
        return <LoadingScreen />;
    }

    if (!auth.user) {
        console.log("No authenticated user, redirecting to login");
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (allowedRoles?.length && !allowedRoles.includes(auth.user.role)) {
        console.log("User doesn't have required role, redirecting to dashboard", auth.user.role, allowedRoles);
        return <Navigate to="/dashboard" replace />;
    }

    console.log("Rendering private route content");
    return <MainLayout>{children}</MainLayout>;
};

export default PrivateRoute;