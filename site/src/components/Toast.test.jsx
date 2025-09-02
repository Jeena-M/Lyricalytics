import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import Toast from "./Toast";

describe("Toast Component", () => {
    test("renders Toast with success message", () => {
        render(<Toast message="Operation successful" type="success" onClose={() => {}} />);
        expect(screen.getByText("Operation successful")).toBeInTheDocument();

        const toastElement = screen.getByText("Operation successful").parentElement;
        expect(toastElement).toHaveClass("bg-green-500");
    });

    test("renders Toast with error message", () => {
        render(<Toast message="Something went wrong" type="error" onClose={() => {}} />);
        expect(screen.getByText("Something went wrong")).toBeInTheDocument();
        const toastElement = screen.getByText("Something went wrong").parentElement;
        expect(toastElement).toHaveClass("bg-red-500");
    });

    test("renders Toast with responseBody", () => {
        const responseBody = { error: "Invalid input" };

        render(<Toast message="Error occurred" type="error" responseBody={responseBody} onClose={() => {}} />);
        expect(screen.getByText(JSON.stringify(responseBody))).toBeInTheDocument();
    });

    test("calls onClose when close button is clicked", () => {
        const mockOnClose = jest.fn();
        render(<Toast message="Closable Toast" type="success" onClose={mockOnClose} />);
        fireEvent.click(screen.getByText("×"));
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    test("renders Toast without responseBody", () => {
        render(<Toast message="No response body" type="success" onClose={() => {}} />);
        expect(screen.getByText("No response body")).toBeInTheDocument();
        expect(screen.queryByText("{")).not.toBeInTheDocument();
    });
});
