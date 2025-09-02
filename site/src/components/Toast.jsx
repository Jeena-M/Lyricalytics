import React from "react";

const Toast = ({ message, type, responseBody, onClose }) => {
    const isSuccess = type === "success";

    return (
        <div
            role={isSuccess ? "status" : "alert"}
            aria-live={isSuccess ? "polite" : "assertive"}
            aria-atomic="true"
            className={`fixed bottom-5 right-5 p-4 rounded-md text-white ${
                isSuccess ? "bg-green-500" : "bg-red-500"
            }`}
        >
            <div>{message}</div>
            {responseBody && (
                <div className="text-sm mt-2">{JSON.stringify(responseBody)}</div>
            )}
            <button
                type="button"
                onClick={onClose}
                aria-label="Close notification"
                className="absolute top-1 right-1 text-lg"
            >
                ×
            </button>
        </div>
    );
};

export default Toast;