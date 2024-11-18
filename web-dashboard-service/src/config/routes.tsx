// config/routes.tsx
import React from 'react';
import { UserRole } from '../types/auth';
import SuspenseWrapper from '../components/SuspenseWrapper';
import UserProfile from "../components/UserProfile";

// Lazy load components
const StockSentimentDashboard = React.lazy(() => import('../components/StockSentimentDashboard'));
const Register = React.lazy(() => import('../components/Register'));
const Login = React.lazy(() => import('../components/Login'));
const AdminDashboard = React.lazy(() => import('../components/Admin'));

export const RouteConfig = {
    public: [
        {
            path: '/login',
            element: <SuspenseWrapper><Login /></SuspenseWrapper>,
        },
        {
            path: '/register',
            element: <SuspenseWrapper><Register /></SuspenseWrapper>,
        },
    ] as const,

    private: [
        {
            path: '/dashboard',
            element: <SuspenseWrapper><StockSentimentDashboard /></SuspenseWrapper>,
            roles: ['USER', 'ADMIN'] as UserRole[],
        },
        {
            path: '/admin',
            element: <SuspenseWrapper><AdminDashboard /></SuspenseWrapper>,
            roles: ['ADMIN'] as UserRole[],
        },
        {
            path: '/profile',
            element: <SuspenseWrapper><UserProfile /></SuspenseWrapper>,
            roles: ['USER', 'ADMIN'] as UserRole[],
        }
    ] as const,
} as const;

export type RouteConfigType = typeof RouteConfig;