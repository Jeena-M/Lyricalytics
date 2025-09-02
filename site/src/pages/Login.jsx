import React, { useState, useEffect } from "react";
import Team from "../components/Team";
import FormFields from "../components/FormFields";
import LoginButton from "../components/LoginButton";
import Toast from "../components/Toast";
import { useNavigate } from "react-router-dom";

function Login({ login }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [toast, setToast] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        try {
            const response = await fetch("/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: username,
                    password: password }),
            });

            if (!response.ok) {
                const errorMessage =
                    response.headers.get("X-Error-Message") || `HTTP error: ${response.status}`;
                throw new Error(errorMessage);
            }

            const data = await response.json();

            if (data.isLoggedIn) {
                login(data.username);
                localStorage.setItem("username", data.username);
                setToast({
                    message: `Welcome back, ${data.username}!`,
                    type: "success",
                    responseBody: null,
                });
                setTimeout(() => navigate("/search"), 1000);
            } else {
                setToast({ message: "Login failed. Please check your credentials.", type: "error" });
            }
        } catch (error) {
            console.error(error);
            if (error.message.includes("Account locked")) {
                setToast({
                    message:
                        "Account locked due to multiple failed login attempts. Please try again in 30 seconds.",
                    type: "error",
                });
                setTimeout(() => {
                    setToast({ message: "Account unlocked. Try again.", type: "success" });
                }, 30 * 1000);
            } else {
                setToast({
                    message: "An error occurred during login. " + error.message,
                    type: "error",
                });
            }
        }
    };

    return (
        <div className="flex flex-col min-h-screen">
            <main
                role="main"
                aria-labelledby="loginHeading"
                className="flex-grow flex items-center justify-center bg-gray-100"
            >
                <section
                    role="region"
                    aria-labelledby="loginHeading"
                    className="bg-blue-100 shadow-lg rounded-lg p-8 w-96 border border-blue-400"
                >
                    <h1
                        id="loginHeading"
                        className="text-center text-2xl font-bold text-purple-600 mb-4"
                    >
                        Login
                    </h1>

                    <form
                        role="form"
                        aria-label="Login form"
                        onSubmit={handleLogin}
                        className="space-y-4"
                    >
                        <FormFields
                            username={username}
                            setUsername={setUsername}
                            password={password}
                            setPassword={setPassword}
                            confirmPassword=""
                            setConfirmPassword={() => {}}
                            showConfirm={false}
                        />

                        <div className="pt-4 flex justify-center">
                            <LoginButton type="submit" aria-label="Submit login" onClick={handleLogin} />
                        </div>
                    </form>
                </section>
            </main>

            <footer role="contentinfo" className="w-full bg-gray-800 text-white py-4 px-6 text-center">
                <Team />
            </footer>

            {toast && (
                <div
                    role="alert"
                    aria-live="assertive"
                    aria-atomic="true"
                    className="fixed top-4 right-4"
                >
                    <Toast
                        message={toast.message}
                        type={toast.type}
                        responseBody={toast.responseBody}
                        onClose={() => setToast(null)}
                    />
                </div>
            )}
        </div>
    );
}

export default Login;