import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Modal from "./Modal";

beforeEach(() => {
    fetch.resetMocks();
});

afterEach(() => {
    window.history.pushState(null, document.title, "/");
});

test("doesn't render when isOpen = false", () => {
    render(<Modal isOpen={false} title="ModalTest" />);
    expect(screen.queryByText(/ModalTest/i)).toBeNull();
});

test("renders when isOpen = true", () => {
    render(<Modal isOpen={true} title="ModalTest" />);
    expect(screen.getByText(/ModalTest/i)).toBeInTheDocument();
});

test("calls onClose when Cancel button is clicked", async () => {
    const user = userEvent.setup();
    const onClose = jest.fn();
    render(<Modal isOpen={true} title="ModalTest" onClose={onClose} />);
    await user.click(screen.getByText(/No/i));
    expect(onClose).toHaveBeenCalledTimes(1);
});

test("calls onSubmit when Submit button is clicked", async () => {
    const user = userEvent.setup();
    const onSubmit = jest.fn((e) => e.preventDefault());
    render(
        <Modal isOpen={true} title="ModalTest" onSubmit={onSubmit}>
            <input type="text" name="testInput" />
        </Modal>
    );
    await user.click(screen.getByText(/Yes/i));
    expect(onSubmit).toHaveBeenCalledTimes(1);
});

