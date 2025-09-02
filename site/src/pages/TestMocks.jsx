import React from "react";

jest.mock("../components/FormFields", () => (props) => (
    <div>
        <input
            data-testid="username"
            value={props.username}
            onChange={(e) => props.setUsername(e.target.value)}
            placeholder="Username"
        />
        <input
            data-testid="password"
            value={props.password}
            onChange={(e) => props.setPassword(e.target.value)}
            placeholder="Password"
        />
        {/* Always render confirm password input so that setConfirmPassword is invoked */}
        <input
            data-testid="confirm-password"
            value={props.confirmPassword}
            onChange={(e) => props.setConfirmPassword(e.target.value)}
            placeholder="Confirm Password"
        />
    </div>
));

jest.mock("../components/Team", () => () => (
    <div data-testid="team">Team Component</div>
));

jest.mock("../components/Toast", () => (props) => (
    <div data-testid="toast">
        <span>{props.message}</span>
        <button data-testid="close-toast" onClick={props.onClose}>
            ×
        </button>
    </div>
));