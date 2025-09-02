import React from "react";
import { render } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";

describe("ProtectedRoute", () => {
    test("renders children when isLoggedIn is true", () => {
        const { getByText } = render(
            <MemoryRouter initialEntries={["/protected"]}>
                <Routes>
                    <Route
                        path="/protected"
                        element={
                            <ProtectedRoute isLoggedIn={true}>
                                <div>Protected Content</div>
                            </ProtectedRoute>
                        }
                    />
                </Routes>
            </MemoryRouter>
        );

        expect(getByText("Protected Content")).toBeInTheDocument();
    });

    test("redirects to /register when isLoggedIn is false", () => {
        const { container } = render(
            <MemoryRouter initialEntries={["/protected"]}>
                <Routes>
                    <Route
                        path="/protected"
                        element={
                            <ProtectedRoute isLoggedIn={false}>
                                <div>Should Not Render</div>
                            </ProtectedRoute>
                        }
                    />
                    <Route path="/register" element={<div>Register Page</div>} />
                </Routes>
            </MemoryRouter>
        );

        expect(container.textContent).toBe("Register Page");
    });
});
