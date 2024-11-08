
// Updated PrivateRoute component for v6
import React, {ReactNode} from "react";
import {Navigate, useLocation} from "react-router-dom";
import {useAuth} from "./AuthProvider";

interface PrivateRouteProps {
    children: ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({children}) => {
    const {auth} = useAuth();
    const location = useLocation();

    if (!auth.accessToken) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    return <>{children}</>;
};

export default PrivateRoute;