import React, {useState, useEffect, useRef} from "react";
import Team from "../components/Team";
import { useLocation } from "react-router-dom";
import Toast from "../components/Toast";
import Modal from "../components/Modal";

function Friends() {
    const [toast, setToast] = useState(null);
    const [usernameSearch, setUsernameSearch] = useState("");
    const [friendComparisonResults, setFriendComparisonResults] = useState({});
    const [sortAscending, setSortAscending] = useState(false);

    const [showFriendsModal, setShowFriendsModal] = useState(false);
    const [modalSongTitle, setModalSongTitle] = useState("");
    const [modalFriendList, setModalFriendList] = useState([]);

    const [songDetailModalOpen, setSongDetailModalOpen] = useState(false);
    const [songDetails, setSongDetails] = useState(null); // { title, artist, year }

    const hoverTimerRef = useRef(null);
    const clickedRecentlyRef = useRef(false);

    const location = useLocation();

    useEffect(() => {
        const handleClearFriendsList = () => {
            const username = localStorage.getItem("username");
            if (username) {
                const data = JSON.stringify({ username });
                const blob = new Blob([data], { type: 'application/json' });
                navigator.sendBeacon("/api/friends/clearComparisonMap", blob);
            }
        };

        window.addEventListener("beforeunload", handleClearFriendsList);

        return () => {
            handleClearFriendsList();
            window.removeEventListener("beforeunload", handleClearFriendsList);
        };
    }, [location.pathname]);

    const mergeFriendResults = (prev, newData) => {
        const combined = { ...prev };

        for (const [song, list] of Object.entries(newData)) {
            if (combined[song]) {
                combined[song] = Array.from(new Set([...combined[song], ...list]));
            } else {
                combined[song] = [...list];
            }
        }

        return combined;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            if (!usernameSearch.trim()) {
                setToast({ message: "Please enter a username.", type: "error" });
                return;
            }

            const username = localStorage.getItem("username");
            if (!username) {
                setToast({ message: "You must be logged in.", type: "error" });
                return;
            }

            const requestBody = {
                username: username,
                friendname: usernameSearch.trim(),
            };

            const res = await fetch("/api/friends/getFriends", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(requestBody),
            });

            if (!res.ok) {
                const data = await res.json();
                const errorMsg = data._error?.[0] || `HTTP error: ${res.status}`;
                throw new Error(errorMsg);
            }

            const newData = await res.json();
            setFriendComparisonResults(prev => mergeFriendResults(prev, newData));
        } catch (err) {
            setToast({
                message: err.message || "User not valid.",
                type: "error",
            });
        }
    };

    const renderResultsTable = () => {
        const entries = Object.entries(friendComparisonResults);

        if (entries.length === 0) {
            return null;
        }

        const flattened = entries.map(([song, lists]) => ({
            song,
            count: (lists.includes(localStorage.getItem("username")) ? 1 : 0) +
                lists.filter(user => user !== localStorage.getItem("username")).length,
        }));


        flattened.sort((a, b) => {
            if (sortAscending) {
                return a.count - b.count;
            } else {
                return b.count - a.count;
            }
        });

        return (
            <div className="overflow-auto mt-8 mb-10 w-[600px]">
                <table className="min-w-full bg-gray-200 text-center border-collapse shadow-md rounded">
                    <thead>
                    <tr className="bg-gray-300">
                        <th className="border border-black px-4 py-2">Song Title</th>
                        <th className="border border-black px-4 py-2"># of Lists Appeared In</th>
                    </tr>
                    </thead>
                    <tbody>
                    {flattened.map((item, index) => (
                        <tr key={index}>
                            <td className="border border-black px-4 py-2 cursor-pointer hover:bg-blue-100 relative"
                                onClick={(e) => {
                                    clickedRecentlyRef.current = true;
                                    clearTimeout(hoverTimerRef.current); // cancel pending hover
                                    handleSongTitleClick(item.song); // open detail modal
                                    setTimeout(() => {
                                        clickedRecentlyRef.current = false;
                                    }, 300);
                                }}>{item.song}</td>
                            <td
                                className="border border-black px-4 py-2 cursor-pointer hover:bg-blue-100 relative"
                                onClick={(e) => {
                                    clickedRecentlyRef.current = true;
                                    clearTimeout(hoverTimerRef.current);
                                    setModalSongTitle(item.song);
                                    setModalFriendList(friendComparisonResults[item.song]);
                                    setShowFriendsModal(true);
                                    setTimeout(() => {
                                        clickedRecentlyRef.current = false;
                                    }, 300);
                                }}
                                onMouseMove={(e) => {
                                    const rect = e.currentTarget.getBoundingClientRect();
                                    const centerX = rect.left + rect.width / 2;
                                    const centerY = rect.top + rect.height / 2;
                                    const mouseX = e.clientX;
                                    const mouseY = e.clientY;
                                    const distance = Math.sqrt(
                                        (mouseX - centerX) ** 2 + (mouseY - centerY) ** 2
                                    );
                                    if (
                                        distance < 40 &&
                                        !showFriendsModal &&
                                        !clickedRecentlyRef.current
                                    ) {
                                        clearTimeout(hoverTimerRef.current);
                                        hoverTimerRef.current = setTimeout(() => {
                                            setModalSongTitle(item.song);
                                            setModalFriendList(friendComparisonResults[item.song]);
                                            setShowFriendsModal(true);
                                        }, 400);
                                    } else {
                                        clearTimeout(hoverTimerRef.current);
                                    }
                                }}
                                onMouseLeave={() => {
                                    clearTimeout(hoverTimerRef.current);
                                }}
                            >
                                {item.count}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        );
    };

    const handleSongTitleClick = async (title) => {
        try {
            const res = await fetch("/api/friends/songDetailsFriendsPage", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ songName: title }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            const song = await res.json();
            setSongDetails(song);
            setSongDetailModalOpen(true);
        } catch (err) {
            setToast({ message: err.message || "Failed to load song details.", type: "error" });
        }
    };

    const handleSortFriends = (ascending) => {
        setSortAscending(ascending);
    };

    return (
        <div
            className="flex flex-col min-h-screen"
            role="application"
            aria-labelledby="page-title"
        >
            <main
                id="main-content"
                className="flex-grow bg-white flex flex-col items-center pt-24"
            >
                <h1 id="page-title" className="text-2xl font-semibold mb-6">
                    Search Friends
                </h1>

                <form
                    onSubmit={handleSubmit}
                    className="flex items-center space-x-4"
                    aria-label="Search for a friend’s username"
                >
                    <input
                        id="friend-username-input"
                        type="text"
                        placeholder="type a username"
                        aria-label="Friend username"
                        value={usernameSearch}
                        onChange={(e) => setUsernameSearch(e.target.value)}
                        className="w-100 px-4 py-2 border border-gray-400 rounded bg-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />
                    <button
                        type="submit"
                        aria-label="Submit username"
                        className="bg-blue-300 hover:bg-blue-400 text-black px-4 py-2 rounded"
                    >
                        Submit
                    </button>
                </form>

                <div
                    className="flex space-x-4 mt-6"
                    role="group"
                    aria-label="Sort song results"
                >
                    <button
                        type="button"
                        aria-label="Sort songs by ascending count"
                        onClick={() => handleSortFriends(true)}
                        className={`${
                            sortAscending === true
                                ? "bg-purple-700 text-white"
                                : "bg-purple-400 hover:bg-purple-600 text-black"
                        } font-medium px-4 py-2 rounded`}
                    >
                        Sort Ascending
                    </button>

                    <button
                        type="button"
                        aria-label="Sort songs by descending count"
                        onClick={() => handleSortFriends(false)}
                        className={`${
                            sortAscending === false
                                ? "bg-purple-700 text-white"
                                : "bg-purple-400 hover:bg-purple-600 text-black"
                        } font-medium px-4 py-2 rounded`}
                    >
                        Sort Descending
                    </button>
                </div>

                <section
                    aria-labelledby="results-heading"
                    className="w-full flex justify-center"
                >
                    <h2 id="results-heading" className="sr-only">
                        Song-comparison results
                    </h2>
                    {renderResultsTable()}
                </section>



                <Modal
                    isOpen={showFriendsModal}
                    onClose={() => setShowFriendsModal(false)}
                    onSubmit={(e) => {
                        e.preventDefault();
                        setShowFriendsModal(false);
                    }}
                    title={`Friends who have "${modalSongTitle}"`}
                    confirmText="Close"
                    cancelText="Back"
                    aria-label="Friends who have this song"
                >
                    <ul className="list-disc list-inside space-y-1 text-left">
                        {modalFriendList.map((name, index) => (
                            <li key={index}>{name}</li>
                        ))}
                    </ul>
                </Modal>

                {songDetails && (
                    <Modal
                        isOpen={songDetailModalOpen}
                        onClose={() => {
                            setSongDetailModalOpen(false);
                            setSongDetails(null);
                        }}
                        onSubmit={(e) => {
                            e.preventDefault();
                            setSongDetailModalOpen(false);
                            setSongDetails(null);
                        }}
                        title={`Details for "${songDetails.title}"`}
                        confirmText="Close"
                        cancelText="Back"
                        aria-label="Song details"
                    >
                        <div className="text-left space-y-2">
                            <p>
                                <strong>Artist:</strong> {songDetails.artist}
                            </p>
                            {songDetails.year && (
                                <p>
                                    <strong>Year:</strong> {songDetails.year}
                                </p>
                            )}
                        </div>
                    </Modal>
                )}
            </main>

            <footer className="w-full bg-gray-800 text-white py-4 px-6 text-center">
                <Team />
            </footer>

            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    responseBody={toast.responseBody}
                    onClose={() => setToast(null)}
                />
            )}
        </div>
    );
}

export default Friends;