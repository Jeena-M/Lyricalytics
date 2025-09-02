import React, { useState, useEffect, useRef } from "react";
import Team from "../components/Team";
import Toast from "../components/Toast";
import Modal from "../components/Modal";

function Favorites() {
    const [toast, setToast] = useState(null);
    const [publicAcc, setPublicAcc] = useState(false);
    const [showTable, setShowTable] = useState(true);
    const [favorites, setFavorites] = useState([]);
    const username = localStorage.getItem("username");
    const [showDeleteAllModal, setShowDeleteAllModal] = useState(false);

    const [favoriteHoverTimer, setFavoriteHoverTimer] = useState(null);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [pendingDeleteSong, setPendingDeleteSong] = useState(null); // { title, artist }

    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);

    const [songDetailModalOpen, setSongDetailModalOpen] = useState(false);
    const [songDetails, setSongDetails] = useState(null); // { title, artist, year }

    const hoverTimerRef = useRef(null);
    const clickedRecentlyRef = useRef(false);

    const fetchFavoriteSongs = async () => {
        if (!username || !username.trim()) {
            setToast({ message: "Username is required to fetch favorites.", type: "error" });
            return;
        }

        try {
            const res = await fetch("/api/favorites/getFavoritesSongs", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: username.trim() }),
            });

            if (!res.ok) {
                const errorMessage =
                    res.headers.get("X-Error-Message") || "Failed to load favorite songs.";
                throw new Error(errorMessage);
            }

            const data = await res.json();
            setFavorites(data);
            setShowTable(true);
        } catch (err) {
            setToast({
                message: err.message || "Error fetching favorite songs.",
                type: "error",
            });
        }
    };

    const handleDeleteAllFavorites = async () => {
        try {
            const res = await fetch("/api/favorites/deleteAllSongs", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            setFavorites([]);
            setShowTable(true);
            setToast({ message: "All favorites deleted.", type: "success" });
        } catch (err) {
            setToast({
                message: err.message || "Failed to delete favorites.",
                type: "error",
            });
        }
    };

    const handleDeleteSong = async (songName, artistName) => {
        try {
            const res = await fetch("/api/favorites/deleteOneSong", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, songName, artistName }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            setFavorites((prev) =>
                prev.filter((song) => !(song.title === songName && song.artist === artistName))
            );

            setToast({ message: `Deleted "${songName}" by ${artistName}`, type: "success" });
        } catch (err) {
            setToast({
                message: err.message || "Failed to delete song.",
                type: "error",
            });
        }
    };

    const handleMoveSong = (direction) =>
        handleMoveSongHelper({
            pendingDeleteSong,
            username,
            direction,
            fetchFn: fetch,
            setToast,
            fetchFavorites: fetchFavoriteSongs,
            setDeleteModalOpen,
        });

    const handleTogglePrivacy = async (desiredPrivacy) => {
        try {
            const res = await fetch("/api/favorites/togglePrivacyMode", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, privacy: !desiredPrivacy }),
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            const resultMessage = await res.text();
            setPublicAcc(desiredPrivacy);
            setToast({ message: resultMessage, type: "success" });
        } catch (err) {
            setToast({
                message: err.message || "Failed to toggle privacy.",
                type: "error",
            });
        }
    };

    const fetchPrivacy = async () => {
        try {
            const res = await fetch(
                `/api/favorites/privacy?username=${encodeURIComponent(username)}`
            );
            if (!res.ok) throw new Error("Could not load privacy");
            const { isPrivate } = await res.json();
            setPublicAcc(!isPrivate);
        } catch (err) {
            console.error(err);
        }
    };

    const handleSongTitleClick = async (title, artist) => {
        try {
            console.log("Fetching song details for:", title, artist, username);
            const res = await fetch("/api/wordcloud/songDetailsFavorites", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    songName: title,
                    artistName: artist,
                }),
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

    useEffect(() => {
        fetchFavoriteSongs();
        fetchPrivacy();
    }, [username]);

    return (
        <div className="flex flex-col min-h-screen">
            <main
                role="main"
                aria-labelledby="favoritesHeading"
                className="flex-grow bg-white flex flex-col items-center mt-20 space-y-6"
            >
                <h1 id="favoritesHeading" className="text-2xl font-semibold mb-4">
                    Favorites
                </h1>

                <div role="group" aria-label="Privacy controls" className="flex space-x-4 mt-4">
                    <button
                        onClick={() => handleTogglePrivacy(false)}
                        aria-pressed={!publicAcc}
                        className={`${
                            !publicAcc
                                ? "bg-purple-700 text-white"
                                : "bg-purple-400 hover:bg-purple-600 text-black"
                        } font-medium px-4 py-2 rounded`}
                    >
                        Account Private
                    </button>
                    <button
                        onClick={() => handleTogglePrivacy(true)}
                        aria-pressed={publicAcc}
                        className={`${
                            publicAcc
                                ? "bg-purple-700 text-white"
                                : "bg-purple-400 hover:bg-purple-600 text-black"
                        } font-medium px-4 py-2 rounded`}
                    >
                        Account Public
                    </button>

                    <button
                        onClick={() => setShowDeleteAllModal(true)}
                        aria-label="Delete All Favorites"
                        className="bg-red-400 hover:bg-red-600 text-white font-medium px-4 py-2 rounded"
                    >
                        Delete All Favorites
                    </button>
                </div>

                {showTable && favorites.length > 0 && (
                    <div className="overflow-auto max-h-[400px] mt-4 mb-10 pb-6 border border-gray-400 rounded w-[500px] shadow-md">
                        <table
                            className="min-w-full bg-gray-200 text-center border-collapse"
                            aria-labelledby="favoritesTableCaption"
                        >
                            <caption id="favoritesTableCaption" className="sr-only">
                                List of favorite songs
                            </caption>
                            <thead>
                            <tr>
                                <th scope="col" className="border border-black px-4 py-2">
                                    #
                                </th>
                                <th scope="col" className="border border-black px-4 py-2">
                                    Title
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            {favorites.map((song, index) => (
                                <tr key={index}>
                                    <td scope="row" className="border border-black px-4 py-2">
                                        {index + 1}
                                    </td>
                                    <td className="border border-black px-4 py-2 relative">
                                        <button
                                            type="button"
                                            className="w-full text-left cursor-pointer hover:bg-blue-100 p-0 m-0"
                                            aria-label={`View details for ${song.title} by ${song.artist}`}
                                            onClick={(e) => {
                                                clickedRecentlyRef.current = true;
                                                clearTimeout(hoverTimerRef.current);
                                                handleSongTitleClick(song.title, song.artist);
                                                setTimeout(() => {
                                                    clickedRecentlyRef.current = false;
                                                }, 300);
                                            }}
                                            onKeyDown={(e) => {
                                                if (e.key === "Enter" || e.key === " ") {
                                                    e.preventDefault();
                                                    clickedRecentlyRef.current = true;
                                                    clearTimeout(hoverTimerRef.current);
                                                    handleSongTitleClick(song.title, song.artist);
                                                    setTimeout(() => {
                                                        clickedRecentlyRef.current = false;
                                                    }, 300);
                                                } else {
                                                    //nothing
                                                }
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
                                                    !deleteModalOpen &&
                                                    !clickedRecentlyRef.current
                                                ) {
                                                    clearTimeout(hoverTimerRef.current);
                                                    hoverTimerRef.current = setTimeout(() => {
                                                        setPendingDeleteSong({
                                                            title: song.title,
                                                            artist: song.artist,
                                                            index,
                                                        });
                                                        setDeleteModalOpen(true);
                                                    }, 400);
                                                } else {
                                                    clearTimeout(hoverTimerRef.current);
                                                }
                                            }}
                                            onMouseLeave={() => {
                                                clearTimeout(hoverTimerRef.current);
                                            }}
                                        >
                                            {song.title}
                                        </button>
                                        <button
                                            className="sr-only"
                                            aria-label={`Open actions for ${song.title}`}
                                            onClick={() => {
                                                    setPendingDeleteSong({title: song.title, artist: song.artist, index});
                                                    setDeleteModalOpen(true);
                                            }}
                                        >
                                            Open actions for {song.title}
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
                {showTable && favorites.length === 0 && (
                    <p className="text-gray-600 text-lg mt-8">No favorite songs found.</p>
                )}

                <Modal
                    isOpen={showDeleteAllModal}
                    onClose={() => setShowDeleteAllModal(false)}
                    onSubmit={async (e) => {
                        e.preventDefault();
                        await handleDeleteAllFavorites();
                        setShowDeleteAllModal(false);
                    }}
                    title="Delete All Favorites?"
                    confirmText="Delete"
                    cancelText="Cancel"
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="deleteAllTitle"
                    aria-describedby="deleteAllDesc"
                >
                    <p id="deleteAllDesc" className="text-center">
                        Are you sure you want to delete <strong>all</strong> your favorite songs?
                    </p>
                </Modal>

                <Modal
                    isOpen={deleteModalOpen}
                    onClose={() => {
                        setDeleteModalOpen(false);
                        setPendingDeleteSong(null);
                    }}
                    title={`Actions for "${pendingDeleteSong?.title}"`}
                    confirmText="Close"
                    cancelText="Back"
                    onSubmit={(e) => {
                        e.preventDefault();
                        setDeleteModalOpen(false);
                        setPendingDeleteSong(null);
                    }}
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="songActionsTitle"
                    aria-describedby="songActionsDesc"
                >
                    <div className="text-center space-y-4">
                        <p id="songActionsDesc">
                            What would you like to do with{" "}
                            <strong>{pendingDeleteSong?.title}</strong> by{" "}
                            <strong>{pendingDeleteSong?.artist}</strong>?
                        </p>

                        <div className="flex justify-center space-x-4 mt-6">
                            <button
                                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
                                onClick={() => {
                                    setDeleteModalOpen(false);
                                    setShowConfirmDeleteModal(true);
                                }}
                            >
                                Delete
                            </button>

                            <button
                                className={`px-4 py-2 rounded text-white ${
                                    pendingDeleteSong?.index === 0
                                        ? "bg-blue-300 cursor-not-allowed"
                                        : "bg-blue-500 hover:bg-blue-600"
                                }`}
                                onClick={() => handleMoveSong("up")}
                                disabled={pendingDeleteSong?.index === 0}
                            >
                                Move Up
                            </button>

                            <button
                                className={`px-4 py-2 rounded text-white ${
                                    pendingDeleteSong?.index === favorites.length - 1
                                        ? "bg-green-300 cursor-not-allowed"
                                        : "bg-green-500 hover:bg-green-600"
                                }`}
                                onClick={() => handleMoveSong("down")}
                                disabled={pendingDeleteSong?.index === favorites.length - 1}
                            >
                                Move Down
                            </button>
                        </div>
                    </div>
                </Modal>

                <Modal
                    isOpen={showConfirmDeleteModal}
                    onClose={() => {
                        setShowConfirmDeleteModal(false);
                        setPendingDeleteSong(null);
                    }}
                    onSubmit={async (e) => {
                        e.preventDefault();
                        await handleDeleteSong(pendingDeleteSong.title, pendingDeleteSong.artist);
                        setShowConfirmDeleteModal(false);
                    }}
                    title={`Confirm Delete`}
                    confirmText="Delete"
                    cancelText="Cancel"
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="confirmDeleteTitle"
                    aria-describedby="confirmDeleteDesc"
                >
                    <p id="confirmDeleteDesc" className="text-center">
                        Are you sure you want to delete{" "}
                        <strong>{pendingDeleteSong?.title}</strong> by{" "}
                        <strong>{pendingDeleteSong?.artist}</strong>?
                    </p>
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
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="songDetailTitle"
                        aria-describedby="songDetailDesc"
                    >
                        <div id="songDetailDesc" className="text-left space-y-2">
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

            <footer role="contentinfo" className="w-full bg-gray-800 text-white py-4 px-6 text-center">
                <Team />
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
        </div>
    );
}

export default Favorites;

export async function handleMoveSongHelper({
                                               pendingDeleteSong,
                                               username,
                                               direction,
                                               fetchFn,
                                               setToast,
                                               fetchFavorites,
                                               setDeleteModalOpen,
                                           }) {
    if (!pendingDeleteSong) return;
    const endpoint =
        direction === "up" ? "/api/favorites/moveSongUp" : "/api/favorites/moveSongDown";
    try {
        const res = await fetchFn(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                username,
                songName: pendingDeleteSong.title,
                artistName: pendingDeleteSong.artist,
            }),
        });
        if (!res.ok) {
            const msg = await res.text();
            throw new Error(msg);
        }
        setToast({ message: `Moved "${pendingDeleteSong.title}" ${direction}`, type: "success" });
        await fetchFavorites();
    } catch (err) {
        setToast({ message: err.message || `Failed to move song ${direction}`, type: "error" });
    } finally {
        setDeleteModalOpen(false);
    }
}