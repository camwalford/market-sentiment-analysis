// components/SuspenseWrapper.tsx
import React, { Suspense, ReactNode } from 'react';
import LoadingScreen from './LoadingScreen';

interface SuspenseWrapperProps {
    children: ReactNode;
}

const SuspenseWrapper: React.FC<SuspenseWrapperProps> = ({ children }) => {
    return (
        <Suspense fallback={<LoadingScreen />}>
            {children}
        </Suspense>
    );
};

export default SuspenseWrapper;