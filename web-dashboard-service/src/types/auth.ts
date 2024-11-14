// types/auth.ts
export type UserRole = 'user' | 'admin'; // Define possible roles

export interface UserData {
    email: string;
    credits: number;
    role: UserRole;  // Update to use specific role type
}


export interface LoginResponse {
    accessToken: string;
    refreshToken: string;
    user: UserData;
}

export interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserData | null;
    loading: boolean;
}

export interface AuthContextType {
    auth: AuthState;
    handleLogin: (email: string, password: string) => Promise<void>;
    handleLogout: () => Promise<void>;
    handleForgotPassword: (email: string) => Promise<void>;
    handleRegister: (username: string, email: string, password: string) => Promise<void>;
    deductCredits: (amount: number) => void;
    refreshAccessToken: () => Promise<string>;
    setCredits: (newCredits: number) => void;
}