import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import Match from "./Match";

jest.mock("../components/Team", () => () => <div data-testid="team">Team</div>);
jest.mock("../components/Animations", () => ({ isSoulmate }) => (
    <div data-testid="particle-burst">{isSoulmate ? "Soulmate Particle" : "Enemy Particle"}</div>
));

describe("Match Component", () => {
    beforeEach(() => {
        localStorage.setItem("username", "testuser");
    });

    afterEach(() => {
        localStorage.clear();
        jest.resetAllMocks();
    });

    test("renders heading and buttons", () => {
        render(<Match />);
        expect(screen.getByText(/Find My Lyrical Soulmate or Enemy/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Find Soulmate/i })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Find Enemy/i })).toBeInTheDocument();
    });



    test("handles API error correctly", async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: false,
                headers: {
                    get: () => "Custom Error Message"
                }
            })
        );

        render(<Match />);
        fireEvent.click(screen.getByRole("button", { name: /Find Soulmate/i }));

        await waitFor(() => {
            expect(screen.getByText(/Custom Error Message/i)).toBeInTheDocument();
        });
    });

    test("handles same-user error correctly", async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
                    username: "testuser", // same as logged in user
                    favorites: [],
                }),
            })
        );

        render(<Match />);
        fireEvent.click(screen.getByRole("button", { name: /Find Enemy/i }));

        await waitFor(() => {
            expect(screen.getByText(/No match found. Please try again later./i)).toBeInTheDocument();
        });
    });

    test("handles successful match and displays matched user and songs", async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
                    username: "matcheduser",
                    favorites: [{ title: "Song 1" }, { title: "Song 2" }],
                }),
            })
        );

        render(<Match />);
        fireEvent.click(screen.getByRole("button", { name: /Find Soulmate/i }));

        await waitFor(() => {
            const paragraph = screen.getByText((content, element) =>
                content.includes("Your soulmate is:") &&
                element.tagName.toLowerCase() === "p"
            );
            expect(paragraph).toBeInTheDocument();
            expect(screen.getByText("matcheduser")).toBeInTheDocument();
            expect(screen.getByTestId("particle-burst")).toHaveTextContent("Soulmate Particle");
            expect(screen.getByText("Song 1")).toBeInTheDocument();
            expect(screen.getByText("Song 2")).toBeInTheDocument();
            expect(screen.getByRole("table")).toBeInTheDocument();
        });
    });


    test("shows no favorites message if matched user has no favorites", async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
                    username: "matcheduser",
                    favorites: [],
                }),
            })
        );

        render(<Match />);
        fireEvent.click(screen.getByRole("button", { name: /Find Enemy/i }));

        await waitFor(() => {
            const paragraph = screen.getByText((content, element) =>
                content.includes("Your enemy is:") &&
                element.tagName.toLowerCase() === "p"
            );
            expect(paragraph).toBeInTheDocument();
            expect(screen.getByText("matcheduser")).toBeInTheDocument();
            expect(screen.getByText(/No favorite songs found for this user./i)).toBeInTheDocument();
            expect(screen.getByTestId("particle-burst")).toHaveTextContent("Enemy Particle");
        });
    });

    test("handles API error with default message when X-Error-Message header is missing", async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: false,
                headers: {
                    get: () => null, // simulate missing X-Error-Message header
                },
            })
        );

        render(<Match />);
        fireEvent.click(screen.getByRole("button", { name: /Find Enemy/i }));

        await waitFor(() => {
            expect(screen.getByText(/Something went wrong/i)).toBeInTheDocument();
        });
    });



});