import React, {useEffect} from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useState } from "react";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Search from "./pages/Search";
import Favorites from "./pages/Favorites";
import Friends from "./pages/Friends";
import Match from "./pages/Match";
import SessionTimeout from "./components/SessionTimeout";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedLoginState = localStorage.getItem("isLoggedIn");
        if (storedLoginState === "true") {
            setIsLoggedIn(true);
        }
        setLoading(false);
    }, []);

    const login = (username) => {
        setIsLoggedIn(true);
        localStorage.setItem("isLoggedIn", "true");
        localStorage.setItem("username", username);
    };

    const logout = () => {
        setIsLoggedIn(false);
        localStorage.removeItem("isLoggedIn");
        localStorage.removeItem("username");
    };
    if (loading) return null;

    return (
        <div className="flex flex-col min-h-screen">
            <Navbar isLoggedIn={isLoggedIn} login={login} logout={logout} /> {/* Pass auth state */}
            {/* Only activate the session timeout monitor when logged in */}
            {isLoggedIn && <SessionTimeout logout={logout} timeout={60000} />}
            <main className="flex-grow">
                <Routes>
                    <Route path="/login" element={<Login login={login} />} />
                    <Route path="/register" element={<Register login={login} />} />
                    <Route path="/search" element={
                        <ProtectedRoute isLoggedIn={isLoggedIn}>
                            <Search />
                        </ProtectedRoute>
                    } />
                    <Route path="/favorites" element={
                        <ProtectedRoute isLoggedIn={isLoggedIn}>
                            <Favorites />
                        </ProtectedRoute>
                    } />
                    <Route path="/friends" element={
                        <ProtectedRoute isLoggedIn={isLoggedIn}>
                            <Friends />
                        </ProtectedRoute>
                    } />
                    <Route path="/match" element={
                        <ProtectedRoute isLoggedIn={isLoggedIn}>
                            <Match />
                        </ProtectedRoute>
                    } />

                    <Route path="*" element={<Navigate to="/login" />} /> {/* Redirect to login */}
                </Routes>
            </main>
        </div>
    );
}

export default App;