import React, { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";

const SessionTimeout = ({ logout, timeout = 60000 }) => {
    const navigate = useNavigate();
    const timerId = useRef(null);

    const resetTimer = () => {
        // Clear the previous timer
        if (timerId.current) clearTimeout(timerId.current);
        // Set a new timeout to trigger logout
        timerId.current = setTimeout(() => {
            logout();
            navigate("/login");
        }, timeout);
    };

    useEffect(() => {
        // List of events that reset our timeout timer
        const events = ["mousemove", "mousedown", "keydown", "scroll", "touchstart"];
        events.forEach((event) => window.addEventListener(event, resetTimer));

        // Initialize the timer
        resetTimer();

        // Cleanup on unmount: remove event listeners and timer
        return () => {
            events.forEach((event) => window.removeEventListener(event, resetTimer));
            if (timerId.current) {
                clearTimeout(timerId.current);
            }
        };
    }, [logout, navigate, timeout]);

    return null; // This component does not render anything visible
};

export default SessionTimeout;
