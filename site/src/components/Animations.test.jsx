import React from "react";
import { render, screen } from "@testing-library/react";
import ParticleBurst from "./Animations";

jest.mock("framer-motion", () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
}));

describe('ParticleBurst Component', () => {
    test('renders 10 soulmate particles with 🎉 emoji', () => {
        render(<ParticleBurst isSoulmate={true} />);
        const particles = screen.getAllByText("🎉");
        expect(particles.length).toBe(10);
    });

    test('renders 10 enemy particles with 😈 emoji', () => {
        render(<ParticleBurst isSoulmate={false} />);
        const particles = screen.getAllByText("😈");
        expect(particles.length).toBe(10);
    });
    test('applies correct container styles', () => {
        const { container } = render(<ParticleBurst isSoulmate={true} />);
        const div = container.querySelector('div.relative');
        expect(div).toBeInTheDocument();
        expect(div).toHaveClass("relative w-full h-full overflow-hidden pointer-events-none");
    });
});
