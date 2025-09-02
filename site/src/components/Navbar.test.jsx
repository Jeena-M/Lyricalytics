import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Navbar from "./Navbar";

const mockNavigate = jest.fn();
let mockLocation = { pathname: "/" };

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
    useLocation: () => mockLocation,
}));

describe("Navbar Component", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        // Reset to default location after each test
        mockLocation = { pathname: "/" };
    });

    test("renders Navbar with all elements for logged out state", () => {
        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        expect(screen.getByText("Let’s Get Lyrical")).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /register/i })).toBeInTheDocument();
    });

    test("navigates to /login when Login button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /login/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/login");
    });

    test("navigates to /register when Register button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /register/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/register");
    });

    test("applies active styling for login button when on /login", () => {
        mockLocation = { pathname: "/login" };

        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        const loginButton = screen.getByRole("button", { name: /login/i });
        expect(loginButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const registerButton = screen.getByRole("button", { name: /register/i });
        expect(registerButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("applies active styling for register button when on /register", () => {
        mockLocation = { pathname: "/register" };

        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        const registerButton = screen.getByRole("button", { name: /register/i });
        expect(registerButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const loginButton = screen.getByRole("button", { name: /login/i });
        expect(loginButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("applies default inactive styling for buttons when not on login or register", () => {
        mockLocation = { pathname: "/other-page" };

        render(
            <MemoryRouter>
                <Navbar />
            </MemoryRouter>
        );

        const loginButton = screen.getByRole("button", { name: /login/i });
        expect(loginButton).toHaveClass("text-gray-600 hover:text-purple-600");
        expect(loginButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const registerButton = screen.getByRole("button", { name: /register/i });
        expect(registerButton).toHaveClass("text-gray-600 hover:text-purple-600");
        expect(registerButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("renders Search, Favorites and Logout buttons when logged in", () => {
        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        expect(screen.getByRole("button", { name: /search/i })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /favorites/i })).toBeInTheDocument();
    });

    test("navigates to /search when Search button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /search/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/search");
    });

    test("calls logout and navigates to /login when Logout button is clicked", () => {
        const mockLogout = jest.fn();

        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={mockLogout} />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /logout/i }));
        expect(mockLogout).toHaveBeenCalled();
        expect(mockNavigate).toHaveBeenCalledWith("/login");
    });

    test("navigates to /favorites when Favorites button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /favorites/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/favorites");
    });

    test("applies active styling for Search button when on /search", () => {
        mockLocation = { pathname: "/search" };

        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        const searchButton = screen.getByRole("button", { name: /search/i });
        expect(searchButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const logoutButton = screen.getByRole("button", { name: /logout/i });
        expect(logoutButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("applies active styling for Favorites button when on /favorites", () => {
        mockLocation = { pathname: "/favorites" };

        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        const favoritesButton = screen.getByRole("button", { name: /favorites/i });
        expect(favoritesButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const searchButton = screen.getByRole("button", { name: /search/i });
        expect(searchButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("applies active styling for Lyrical Matching button when on /match", () => {
        mockLocation = { pathname: "/match" };

        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        const lyricalButton = screen.getByRole("button", { name: /lyrical matching/i });
        expect(lyricalButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const friendsButton = screen.getByRole("button", { name: /friends/i });
        expect(friendsButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("applies active styling for Friends button when on /friends", () => {
        mockLocation = { pathname: "/friends" };

        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        const friendsButton = screen.getByRole("button", { name: /friends/i });
        expect(friendsButton).toHaveClass("text-purple-600 font-bold shadow-md bg-white");

        const lyricalButton = screen.getByRole("button", { name: /lyrical matching/i });
        expect(lyricalButton).not.toHaveClass("text-purple-600 font-bold shadow-md bg-white");
    });

    test("navigates to /match when Lyrical Matching button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /lyrical matching/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/match");
    });

    test("navigates to /friends when Friends button is clicked", () => {
        render(
            <MemoryRouter>
                <Navbar isLoggedIn={true} logout={jest.fn()} />
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole("button", { name: /friends/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/friends");
    });



});