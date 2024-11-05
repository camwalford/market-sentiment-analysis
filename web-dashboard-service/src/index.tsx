// src/index.tsx
import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthProvider } from './AuthProvider';
import App from './App';

const container = document.getElementById('root');
if (!container) throw new Error('Root container missing in index.html');

createRoot(container).render(
    <React.StrictMode>
        <AuthProvider>
            <App />
        </AuthProvider>
    </React.StrictMode>
);
