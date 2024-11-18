import React, { ReactNode, useEffect, useState } from "react";
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
    const { auth, validateSession } = useAuth();
    const location = useLocation();
    const [isValidating, setIsValidating] = useState(true);

    useEffect(() => {
        let isMounted = true;

        const validate = async () => {
            try {
                await validateSession();
                console.log("Session validated successfully");
            } catch (error) {
                console.error("Session validation failed:", error);
            } finally {
                if (isMounted) {
                    setIsValidating(false);
                }
            }
        };

        validate();

        return () => {
            isMounted = false;
        };
    }, [validateSession]);

    console.log("PrivateRoute render", { isValidating, user: auth.user, allowedRoles });

    if (isValidating) {
        console.log("Showing loading screen");
        return <LoadingScreen />;
    }

    if (!auth.user) {
        console.log("No authenticated user, redirecting to login");
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (allowedRoles?.length && !allowedRoles.includes(auth.user.role)) {
        console.log("User doesn't have required role, redirecting to dashboard");
        return <Navigate to="/dashboard" replace />;
    }

    console.log("Rendering private route content");
    return <MainLayout>{children}</MainLayout>;
};

export default PrivateRoute;