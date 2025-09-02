import React, {useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import Team from "../components/Team";
import CreateButton from "../components/CreateButton";
import CancelButton from "../components/CancelButton";
import FormFields from "../components/FormFields";
import Toast from "../components/Toast";
import Modal from "../components/Modal";

function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [toast, setToast] = useState(null);
    const navigate = useNavigate();
    const [showCancelModal, setShowCancelModal] = useState(false);

    const handleRegister = async (event) => {
        event.preventDefault();

        if ((password && !confirmPassword) || (!password && confirmPassword)) {
            setToast({
                message: "Please fill both password fields.",
                type: "error"
            });
            return;
        }

        try {
            const response = await fetch("/register", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    username: username,
                    password: password,
                    confirmPassword: confirmPassword
                })
            });

            if (!response.ok) {
                const errorMessage = response.headers.get("X-Error-Message") || `HTTP error: ${response.status}`;
                throw new Error(errorMessage);
            }

            const data = await response.json();

            setToast({
                message: `Account created successfully! Please log in.`,
                type: "success",
                responseBody: {username: data.username}
            });
            setUsername("");
            setPassword("");
            setConfirmPassword("");
            setTimeout(() => navigate("/login"), 1000);
        } catch (error) {
            console.error(error);
            setToast({
                message: "Registration failed. " + error.message,
                type: "error"
            });
        }
    };

    const handleCancel = (event) => {
        event.preventDefault();
        setShowCancelModal(true);
    };

    const handleConfirmCancel = (event) => {
        event.preventDefault();
        setUsername('');
        setPassword('');
        setConfirmPassword('');
        setToast({
            message: "Registration cancelled",
            type: "error"
        });
        setShowCancelModal(false);
        setTimeout(() => navigate("/login"), 1000);
    };

    return (
        <div className="flex flex-col min-h-screen">
            <main
                role="main"
                aria-labelledby="registerHeading"
                className="flex-grow flex items-center justify-center bg-gray-100"
            >
                <section
                    role="region"
                    aria-labelledby="registerHeading"
                    className="bg-blue-100 shadow-lg rounded-lg p-8 w-96 border border-blue-400"
                >
                    <h2
                        id="registerHeading"
                        className="text-center text-2xl font-bold text-purple-600 mb-4"
                    >
                        Create Account
                    </h2>

                    <form
                        role="form"
                        aria-label="Registration form"
                        onSubmit={handleRegister}
                        className="space-y-4"
                    >
                        <FormFields
                            username={username}
                            setUsername={setUsername}
                            password={password}
                            setPassword={setPassword}
                            confirmPassword={confirmPassword}
                            setConfirmPassword={setConfirmPassword}
                            showConfirm={true}
                        />

                        <div role="group" aria-label="Form actions" className="flex justify-between">
                            <CreateButton type="submit" aria-label="Create account"/>
                            <CancelButton
                                type="button"
                                aria-label="Cancel registration"
                                onClick={handleCancel}
                            />
                        </div>
                    </form>
                </section>
            </main>

            <footer role="contentinfo" className="w-full bg-gray-800 text-white py-4 px-6 text-right">
                <Team/>
            </footer>

            {toast && (
                <div role="alert" aria-live="assertive" aria-atomic="true" className="fixed top-4 right-4">
                    <Toast
                        message={toast.message}
                        type={toast.type}
                        responseBody={toast.responseBody}
                        onClose={() => setToast(null)}
                    />
                </div>
            )}

            <Modal
                isOpen={showCancelModal}
                onClose={() => setShowCancelModal(false)}
                onSubmit={handleConfirmCancel}
                title="Confirm Cancellation"
                confirmText="Yes"
                cancelText="No"
                role="dialog"
                aria-modal="true"
                aria-labelledby="cancelModalTitle"
                aria-describedby="cancelModalDesc"
            >
                <h2 id="cancelModalTitle" className="sr-only">
                    Confirm Cancellation
                </h2>
                <p id="cancelModalDesc">Are you sure you want to cancel registration?</p>
            </Modal>
        </div>
    );
}

export default Register;
