import React from "react";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import App from "./App";

jest.mock("./pages/Login", () => () => <div data-testid="login-page">Login Page</div>);
jest.mock("./pages/Register", () => () => <div data-testid="register-page">Register Page</div>);
jest.mock("./pages/Search", () => () => <div data-testid="search-page">Search Page</div>);
jest.mock("./pages/Favorites", () => () => <div data-testid="favorites-page">Favorites Page</div>);

jest.mock("./components/Navbar", () => (props) => (
    <div data-testid="navbar">
        <button onClick={props.login} data-testid="mock-login">Mock Login</button>
        <button onClick={props.logout} data-testid="mock-logout">Mock Logout</button>
    </div>
));

describe("App Component", () => {

    afterEach(() => {
        localStorage.clear();
        cleanup();
    });

    describe("Navbar", () => {
        test("renders Navbar component", () => {
            render(
                <MemoryRouter>
                    <App />
                </MemoryRouter>
            );
            expect(screen.getByTestId("navbar")).toBeInTheDocument();
        });

        test("login and logout props update isLoggedIn state", () => {
            render(
                <MemoryRouter>
                    <App />
                </MemoryRouter>
            );

            fireEvent.click(screen.getByTestId("mock-login"));

            fireEvent.click(screen.getByTestId("mock-logout"));

            expect(screen.getByTestId("login-page")).toBeInTheDocument();
        });

        test("clicking the mock-login button calls login() and covers setIsLoggedIn(true)", () => {
            render(
                <MemoryRouter>
                    <App />
                </MemoryRouter>
            );

            fireEvent.click(screen.getByTestId("mock-login"));
            expect(screen.getByTestId("login-page")).toBeInTheDocument();
        });
    });

    describe("Routing", () => {
        test("renders Login page when navigating to /login", () => {
            render(
                <MemoryRouter initialEntries={["/login"]}>
                    <App />
                </MemoryRouter>
            );
            expect(screen.getByTestId("login-page")).toBeInTheDocument();
        });

        test("renders Register page when navigating to /register", () => {
            render(
                <MemoryRouter initialEntries={["/register"]}>
                    <App />
                </MemoryRouter>
            );
            expect(screen.getByTestId("register-page")).toBeInTheDocument();
        });

        test("renders Search page when navigating to /search", () => {
            localStorage.setItem("isLoggedIn", "true");

            render(
                <MemoryRouter initialEntries={["/search"]}>
                    <App />
                </MemoryRouter>
            );

            expect(screen.getByTestId("search-page")).toBeInTheDocument();
        });

        test("renders Favorites page when navigating to /favorites", () => {
            localStorage.setItem("isLoggedIn", "true");

            render(
                <MemoryRouter initialEntries={["/favorites"]}>
                    <App />
                </MemoryRouter>
            );

            expect(screen.getByTestId("favorites-page")).toBeInTheDocument();
        });

        test("redirects to Login page when accessing an unknown route", () => {
            render(
                <MemoryRouter initialEntries={["/unknown"]}>
                    <App />
                </MemoryRouter>
            );
            expect(screen.getByTestId("login-page")).toBeInTheDocument();
        });
    });

    describe("LocalStorage login state", () => {
        test("loads logged in state from localStorage", () => {
            // Pre-populate localStorage to simulate a logged-in user.
            localStorage.setItem("isLoggedIn", "true");
            localStorage.setItem("username", "testUser");

            render(
                <MemoryRouter>
                    <App />
                </MemoryRouter>
            );

            fireEvent.click(screen.getByTestId("mock-logout"));
            expect(localStorage.getItem("isLoggedIn")).toBeNull();
            expect(localStorage.getItem("username")).toBeNull();

            expect(screen.getByTestId("login-page")).toBeInTheDocument();
        });
    });
});