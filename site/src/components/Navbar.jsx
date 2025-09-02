import { useNavigate, useLocation } from "react-router-dom";

export default function Navbar({ isLoggedIn, logout }) {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <nav
            className="flex flex-wrap justify-between items-center bg-blue-200 px-4 md:px-8 py-4 shadow-md"
            aria-label="Main navigation"
        >
            <h1 className="text-2xl font-bold text-black mb-2 md:mb-0">
                Let’s Get Lyrical
            </h1>

            <div className="flex flex-wrap gap-4" role="group" aria-label="Site pages">
                {isLoggedIn ? (
                    <>
                        <button
                            type="button"
                            onClick={() => navigate("/match")}
                            aria-current={location.pathname === "/match" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/match"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Lyrical Matching
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate("/friends")}
                            aria-current={location.pathname === "/friends" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/friends"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Friends
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate("/favorites")}
                            aria-current={location.pathname === "/favorites" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/favorites"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Favorites
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate("/search")}
                            aria-current={location.pathname === "/search" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/search"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Search
                        </button>

                        <button
                            type="button"
                            onClick={() => {
                                logout();
                                navigate("/login");
                            }}
                            className="px-4 py-2 rounded-md text-lg text-red-600 hover:underline"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <button
                            type="button"
                            onClick={() => navigate("/login")}
                            aria-current={location.pathname === "/login" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/login"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Login
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate("/register")}
                            aria-current={location.pathname === "/register" ? "page" : undefined}
                            className={`px-4 py-2 rounded-md text-lg transition ${
                                location.pathname === "/register"
                                    ? "text-purple-600 font-bold shadow-md bg-white"
                                    : "text-gray-600 hover:text-purple-600"
                            }`}
                        >
                            Register
                        </button>
                    </>
                )}
            </div>
        </nav>
    );
}