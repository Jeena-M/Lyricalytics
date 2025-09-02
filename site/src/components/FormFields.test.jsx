import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import FormFields from "./FormFields";

describe("FormFields Component", () => {
    test("renders username and password fields correctly", () => {
        render(
            <FormFields
                username=""
                setUsername={jest.fn()}
                password=""
                setPassword={jest.fn()}
                confirmPassword=""
                setConfirmPassword={jest.fn()}
            />
        );

        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
        expect(screen.getByLabelText("Password", { selector: "input" })).toBeInTheDocument();
        expect(screen.queryByLabelText(/confirm password/i)).not.toBeInTheDocument();
    });

    test("updates username field on change", () => {
        const mockSetUsername = jest.fn();

        render(
            <FormFields
                username=""
                setUsername={mockSetUsername}
                password=""
                setPassword={jest.fn()}
                confirmPassword=""
                setConfirmPassword={jest.fn()}
                showConfirm={true}
            />
        );

        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: "testuser" } });
        expect(mockSetUsername).toHaveBeenCalledWith("testuser");
    });

    test("updates password field on change", () => {
        const mockSetPassword = jest.fn();

        render(
            <FormFields
                username=""
                setUsername={jest.fn()}
                password=""
                setPassword={mockSetPassword}
                confirmPassword=""
                setConfirmPassword={jest.fn()}
                showConfirm={true}
            />
        );

        const passwordInput = screen.getByLabelText("Password", { selector: "input" });
        fireEvent.change(passwordInput, { target: { value: "securepassword" } });

        expect(mockSetPassword).toHaveBeenCalledWith("securepassword");
    });

    test("updates confirm password field on change when showConfirm is true", () => {
        const mockSetConfirmPassword = jest.fn();

        render(
            <FormFields
                username=""
                setUsername={jest.fn()}
                password=""
                setPassword={jest.fn()}
                confirmPassword=""
                setConfirmPassword={mockSetConfirmPassword}
                showConfirm={true}
            />
        );

        const confirmPasswordInput = screen.getByLabelText(/confirm password/i);
        fireEvent.change(confirmPasswordInput, { target: { value: "securepassword" } });

        expect(mockSetConfirmPassword).toHaveBeenCalledWith("securepassword");
    });

    test("does not render confirm password field when showConfirm is false (default case)", () => {
        render(
            <FormFields
                username=""
                setUsername={jest.fn()}
                password=""
                setPassword={jest.fn()}
                confirmPassword=""
                setConfirmPassword={jest.fn()}
                showConfirm={false}
            />
        );

        expect(screen.queryByLabelText(/confirm password/i)).not.toBeInTheDocument();
    });

    test("ensures setConfirmPassword is NOT called when showConfirm is false", () => {
        const mockSetConfirmPassword = jest.fn();

        render(
            <FormFields
                username=""
                setUsername={jest.fn()}
                password=""
                setPassword={jest.fn()}
                confirmPassword=""
                setConfirmPassword={mockSetConfirmPassword}
                showConfirm={false}
            />
        );

        expect(mockSetConfirmPassword).not.toHaveBeenCalled();
    });

    test("handles empty input for username, password, and confirm password", () => {
        const mockSetUsername = jest.fn();
        const mockSetPassword = jest.fn();
        const mockSetConfirmPassword = jest.fn();

        render(
            <FormFields
                username="testuser"
                setUsername={mockSetUsername}
                password="securepass"
                setPassword={mockSetPassword}
                confirmPassword="securepass"
                setConfirmPassword={mockSetConfirmPassword}
                showConfirm={true}
            />
        );

        const usernameInput = screen.getByLabelText(/username/i);
        const passwordInput = screen.getByLabelText("Password", { selector: "input" });
        const confirmPasswordInput = screen.getByLabelText(/confirm password/i);

        fireEvent.change(usernameInput, { target: { value: "" } });
        fireEvent.change(passwordInput, { target: { value: "" } });
        fireEvent.change(confirmPasswordInput, { target: { value: "" } });

        expect(mockSetUsername).toHaveBeenCalledWith("");
        expect(mockSetPassword).toHaveBeenCalledWith("");
        expect(mockSetConfirmPassword).toHaveBeenCalledWith("");
    });


    test("ensures component renders without crashing when all props are missing (default behavior)", () => {
        render(<FormFields username="" password="" confirmPassword="" />);
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
        expect(screen.getByLabelText("Password", { selector: "input" })).toBeInTheDocument();
    });
});
