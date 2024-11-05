// src/Login.tsx
import React, { useState } from "react";
import { useAuth } from "./AuthProvider";

const Login: React.FC = () => {
    const { handleLogin } = useAuth();
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await handleLogin(email, password);
        } catch (error) {
            if (error instanceof Error) { // Check if error is an instance of Error
                alert("Login failed: " + error.message);
            } else {
                alert("Login failed due to an unknown error.");
            }
        }
    };



    return (
        <form onSubmit={onSubmit}>
            <h2>Login</h2>
            <label>Email:</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <label>Password:</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            <button type="submit">Login</button>
        </form>
    );
};

export default Login;
