import React, { useEffect, useRef } from "react";

const Modal = ({
                   isOpen,
                   onClose,
                   onSubmit,
                   title,
                   children,
                   confirmText = "Yes",
                   cancelText = "No",
               }) => {
    const dialogRef = useRef(null);

    useEffect(() => {
        if (isOpen && dialogRef.current) {
            dialogRef.current.focus();
        }
    }, [isOpen]);

    if (!isOpen) return null;

    return (
        <div
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
            role="presentation"
            onClick={onClose} // clicking backdrop closes
        >
            <div
                className="bg-white p-6 rounded-md shadow-lg w-96"
                role="dialog"
                aria-modal="true"
                aria-labelledby="modalTitle"
                tabIndex={-1}
                ref={dialogRef}
                onClick={(e) => e.stopPropagation()} // prevent backdrop click
            >
                <h2 id="modalTitle" className="text-xl font-semibold mb-4">
                    {title}
                </h2>
                <form onSubmit={onSubmit}>
                    {children}
                    <div className="flex justify-end space-x-2 mt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="bg-gray-300 text-gray-800 px-4 py-2 rounded-md"
                        >
                            {cancelText}
                        </button>
                        <button
                            type="submit"
                            className="bg-blue-500 text-white px-4 py-2 rounded-md"
                        >
                            {confirmText}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Modal;