// types/auth.ts
export type UserRole = 'USER' | 'ADMIN' | 'PREMIUM_USER';

export interface User {
    id: number;
    username: string;
    email: string;
    role: UserRole;
    credits: number;
    totalRequests: number;
}

export interface AuthState {
    user: User | null;
    loading: boolean;
    error: string | null;
}

export interface AuthContextType {
    auth: AuthState;
    handleLogin: (username: string, password: string) => Promise<void>;
    handleLogout: () => Promise<void>;
    handleRegister: (username: string, email: string, password: string) => Promise<void>;
    updateUserData: (userData: Partial<User>) => void;
    validateSession: () => Promise<boolean>;
    clearError: () => void;
}

export interface LoginResponse {
    user: User;
}

export interface ApiError {
    message: string;
    status: number;
}