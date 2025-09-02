import React from "react";
import { render, screen } from "@testing-library/react";
import Team from "./Team";

describe("Team Component", () => {
    test("renders Team component with text 'Team 6'", () => {
        render(<Team />);

        expect(screen.getByText("Team 6")).toBeInTheDocument();
    });
});
