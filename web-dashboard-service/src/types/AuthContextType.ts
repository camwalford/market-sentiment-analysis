import AuthState from "./AuthState";

type AuthContextType = {
    auth: AuthState;
    handleLogin: (email: string, password: string) => Promise<void>;
    handleLogout: () => void;
};

export default AuthContextType;
