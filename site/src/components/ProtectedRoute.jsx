import React from "react";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ isLoggedIn, children }) {
    if (!isLoggedIn) {
        return <Navigate to="/register" replace />;
    }
    return children;
}