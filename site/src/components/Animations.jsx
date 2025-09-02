import { motion } from "framer-motion";
import React from "react";

// animation styles
const soulmateAnimation = {
    initial: { y: 0, opacity: 1 },
    animate: { y: -200, opacity: 0 },
    transition: { duration: 3 },
};

const enemyAnimation = {
    initial: { scale: 1, opacity: 1 },
    animate: { scale: 2, opacity: 0 },
    transition: { duration: 3 },
};

const Particle = ({ text, x, y, animation }) => (
    <motion.div
        role="presentation"
        aria-hidden="true"
        style={{
            position: "absolute",
            top: `${y}px`,
            left: `${x}px`,
            fontSize: "2rem",
        }}
        initial={animation.initial}
        animate={animation.animate}
        transition={animation.transition}
    >
        {text}
    </motion.div>
);

const ParticleBurst = ({ isSoulmate }) => {
    const particle = isSoulmate ? "🎉" : "😈";
    const animation = isSoulmate ? soulmateAnimation : enemyAnimation;

    return (
        <div
            className="relative w-full h-full overflow-hidden pointer-events-none"
            role="presentation"
            aria-hidden="true"
        >
            {Array.from({ length: 10 }).map((_, i) => (
                <Particle
                    key={i}
                    text={particle}
                    x={Math.random() * 600}
                    y={Math.random() * 150}
                    animation={animation}
                />
            ))}
        </div>
    );
};

export default ParticleBurst;
