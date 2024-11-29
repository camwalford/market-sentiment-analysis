// types/auth.ts
export type UserRole = 'USER' | 'ADMIN';

export interface User {
    id: number;
    username: string;
    email: string;
    role: UserRole;
    credits: number;
    totalRequests: number;
}