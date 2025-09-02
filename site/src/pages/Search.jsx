import React, { useState, useRef, useCallback, useMemo } from "react";
import WordCloud from "react-d3-cloud";
import Team from "../components/Team";
import Toast from "../components/Toast";
import Modal from "../components/Modal";
import lemmatizer from 'wink-lemmatizer';

const StableWordCloud = React.memo(({ words, onWordClick }) => {
    const handleWordMouseOver = useCallback((event, word) => {
        console.log("Word mouse over:", word?.text);
    }, []);

    const handleWordMouseOut = useCallback((event, word) => {
        console.log("Word mouse out:", word?.text);
    }, []);

    const memoizedRotate = useCallback(rotate, []);
    const memoizedFontSizeMapper = useCallback(fontSizeMapper, []);

    return (
        <div className="mb-10 w-[900px] h-[600px] pointer-events-auto">
            <WordCloud
                data={words}
                width={900}
                height={600}
                fontWeight="bold"
                fontSizeMapper={memoizedFontSizeMapper}
                rotate={memoizedRotate}
                padding={10}
                style={{ cursor: "pointer" }}
                onWordClick={onWordClick}
                onWordMouseOver={handleWordMouseOver}
                onWordMouseOut={handleWordMouseOut}
            />
        </div>
    );
});

function Search() {
    const [toast, setToast] = useState(null);
    const [artistName, setArtistName] = useState("");
    const typeNumberRef = useRef();
    const [weights, setWeights] = useState([]); // Raw API data for the word cloud
    const [showTable, setShowTable] = useState(false);
    const [songDetailModalOpen, setSongDetailModalOpen] = useState(false);
    const [selectedSongTitle, setSelectedSongTitle] = useState("");
    const [modalOpen, setModalOpen] = useState(false);
    const [selectedWord, setSelectedWord] = useState("");
    const [songFrequencies, setSongFrequencies] = useState([]);
    const [favoriteModalOpen, setFavoriteModalOpen] = useState(false);
    const [pendingFavoriteSong, setPendingFavoriteSong] = useState(null);
    const hoverTimerRef = useRef(null);
    const clickedRecentlyRef = useRef(false);
    const [possibleArtists, setPossibleArtists] = useState([]);
    const [showArtistOptions, setShowArtistOptions] = useState(false);

    const [allSongs, setAllSongs] = useState([]);
    const [selectedSongs, setSelectedSongs] = useState([]);
    const [showSongPicker, setShowSongPicker] = useState(false);

    const [wasNumberProvided, setWasNumberProvided] = useState(false);
    const username = localStorage.getItem("username");
    const memoizedWords = useMemo(() => {
        return weights.length > 0 ? normalizeWords(weights) : [];
    }, [weights]);

    const handleGenerateFromFavorites = useCallback(async () => {
        try {

            const res = await fetch("/api/favorites/generate", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({username}),
            });
            if (!res.ok) {
                const msg = res.headers.get("X-Error-Message") || `HTTP ${res.status}`;
                throw new Error(msg);
            }
            const data = await res.json();
            // map into the format your word‑cloud expects:
            const formatted = data.map(item => ({
                text: String(item.word),
                value: Number(item.count),
            }));
            setWeights(formatted);
            setShowTable(false);
            setShowArtistOptions(false);
        } catch (err) {
            setToast({
                message: err.message || "Failed to generate word cloud from favorites.",
                type: "error",
            });
        }
    }, []);

    // Handle individual word click.
    const handleWordClick = useCallback(
        async (word) => {
            // if (!word) {
            //     console.error("handleWordClick called with undefined word");
            //     return;
            // }
            try {
                console.log("handleWordClick triggered for:", word);
                const rawInput = typeNumberRef.current?.value?.trim();
                const isNumberProvided = rawInput !== "";
                const songCount = isNumberProvided ? parseInt(rawInput) : 0;
                const endpoint = isNumberProvided
                    ? "/api/wordcloud/songFrequencyForWordInLyrics"
                    : "/api/wordcloud/songFrequencyForWordInLyricsNoNumber";

                const requestBody = {
                    word: word,
                    artistName: artistName.trim(),
                    username: username,
                };
                if (isNumberProvided) {
                    requestBody.songCount = songCount;
                }

                const res = await fetch(endpoint, {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(requestBody),
                });

                if (!res.ok) {
                    const errorMessage =
                        res.headers.get("X-Error-Message") || "Failed to load song frequency.";
                    throw new Error(errorMessage);
                }

                const data = await res.json();
                console.log("Song frequency data:", data);
                setSelectedWord(word);
                setSongFrequencies(data);
                setModalOpen(true);
            } catch (err) {
                setToast({
                    message: err.message || "Something went wrong loading song frequencies.",
                    type: "error",
                });
            }
        },
        [artistName]
    );

    const handleWordCloudClick = useCallback(
        (event, word) => {
            if (word?.text) {
                console.log("Word clicked:", word.text);
                handleWordClick(word.text);
            } else {
                console.warn("Invalid word clicked:", word);
            }
        },
        [handleWordClick]
    );

    const fetchAndShowSongPicker = async (resolvedArtistName, count = 15) => {
        try {
            const songListRes = await fetch("/api/wordcloud/addSong", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({artistName: resolvedArtistName}),
            });

            if (!songListRes.ok) {
                const errorMessage = songListRes.headers.get("X-Error-Message") || "Failed to fetch songs.";
                throw new Error(errorMessage);
            }

            const songs = await songListRes.json();
            setAllSongs(songs.slice(0, count));
            setShowSongPicker(true);
            setSelectedSongs([]);
        } catch (err) {
            setToast({
                message: err.message || "An error occurred while fetching songs.",
                type: "error",
            });
        }
    };

    const resolveArtistAndFetchSongs = async (trimmedArtist) => {
        const artistRes = await fetch("/api/wordcloud/artists", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({artistName: trimmedArtist}),
        });

        if (!artistRes.ok) {
            throw new Error("Error fetching possible artists.");
        }

        const artistList = await artistRes.json();

        if (artistList.length > 1) {
            setPossibleArtists(artistList);
            setShowArtistOptions(true);
            return {earlyReturn: true};
        }

        const resolvedName = typeof artistList[0] === "string"
            ? artistList[0]
            : artistList[0].name;

        const songListRes = await fetch("/api/wordcloud/addSong", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({artistName: resolvedName}),
        });

        if (!songListRes.ok) {
            const errorMessage = songListRes.headers.get("X-Error-Message") || "Failed to fetch songs.";
            throw new Error(errorMessage);
        }

        const songs = await songListRes.json();
        setAllSongs(songs.slice(0, 15));
        setShowSongPicker(true);
        setSelectedSongs([]);

        return {earlyReturn: false};
    };

    const handleWordCloudSubmit = async () => {
        const trimmedArtist = artistName.trim();
        const songCountInput = typeNumberRef.current?.value || "";

        const isNumberProvided = songCountInput.trim() !== "";
        setWasNumberProvided(isNumberProvided);

        if (!trimmedArtist) {
            setToast({message: "Please enter an artist name.", type: "error"});
            return;
        }

        try {
            let res;

            if (!/^[A-Za-z0-9\s]+$/.test(trimmedArtist)) {
                setToast({message: "Invalid name format", type: "error"});
                return;
            }

            if (!isNumberProvided) {
                const result = await resolveArtistAndFetchSongs(trimmedArtist);
                if (result?.earlyReturn) return;
            }

            if (songCountInput.trim() !== "" && !/^[1-9]\d*$/.test(songCountInput.trim())) {
                setToast({message: "Invalid song count", type: "error"});
            }
            const songCount = parseInt(songCountInput) || 0;

            console.log("Artist Name: ", trimmedArtist);
            console.log("Song Count: ", songCount);
            console.log("Username: ", username);
            res = await fetch("/api/wordcloud/generate", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    artistName: trimmedArtist,
                    songCount: songCount,
                    username: username,
                }),
            });

            if (!res.ok) {
                const errorMessage = res.headers.get("X-Error-Message") || `HTTP error: ${res.status}`;
                if (errorMessage.includes("Found many artists")) {
                    const artistRes = await fetch("/api/wordcloud/artists", {
                        method: "POST",
                        headers: {"Content-Type": "application/json"},
                        body: JSON.stringify({artistName: trimmedArtist}),
                    });

                    if (!artistRes.ok) {
                        throw new Error("Error fetching possible artists.");
                    }

                    const artistList = await artistRes.json();
                    setPossibleArtists(artistList);
                    setShowArtistOptions(true);
                    return;
                }

                throw new Error(errorMessage);
            }

            const data = await res.json();
            const formatted = data.map((item) => ({
                text: String(item.word),
                value: Number(item.count),
            }));

            setShowArtistOptions(false);
            setPossibleArtists([]);
            setShowTable(false);
            setWeights(formatted);

            console.log("WordCloud API response:", formatted);
        } catch (err) {
            setToast({
                message: err.message || "An unknown error occurred.",
                type: "error",
            });
        }
    };

    const toggleSongSelection = (songName) => {
        setSelectedSongs(prev =>
            prev.includes(songName)
                ? prev.filter(s => s !== songName)
                : [...prev, songName]
        );
    };

    const submitSelectedSongs = async () => {
        try {
            if (!username) {
                setToast({message: "Username is missing.", type: "error"});
                return;
            }

            if (!artistName.trim()) {
                setToast({message: "Please enter an artist name.", type: "error"});
                return;
            }

            if (selectedSongs.length === 0) {
                setToast({message: "Please select at least one song to add.", type: "error"});
                return;
            }

            if (weights.length === 0) {
                // No word cloud yet — create new
                const res = await fetch("/api/wordcloud/generateFromList", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        artistName: artistName.trim(),
                        songs: selectedSongs,
                        username: username,
                    }),
                });

                console.log("Submitting to backend:", res);

                if (!res.ok) {
                    const errorMessageWC = res.headers.get("X-Error-Message") || "Failed to generate word cloud.";
                    throw new Error(errorMessageWC);
                }

                await mergeNewWordData(res, weights, setWeights);
                setToast({message: "Word cloud created with selected songs!", type: "success"});

            } else {
                // Word cloud exists — update it
                const res = await fetch("/api/wordcloud/updateExistingWordCloudFromList", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        artist: artistName.trim(),
                        songs: selectedSongs,
                        username: username,
                        wordCloudJson: JSON.stringify(weights),
                    }),
                });

                if (!res.ok) {
                    const errorMessageWC = res.headers.get("X-Error-Message") || "Failed to update existing word cloud.";
                    throw new Error(errorMessageWC);
                }

                await mergeNewWordData(res, weights, setWeights);
                setToast({message: "Word cloud updated with selected songs!", type: "success"});
            }

            setShowTable(false);
            setShowSongPicker(false);
            setSelectedSongs([]);

        } catch (err) {
            setToast({
                message: err.message || "An error occurred.",
                type: "error",
            });
        }
    };

    const handleSongTitleClick = async (songName) => {
        try {
            const res = await fetch("/api/wordcloud/songDetails", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    songName: songName,
                    artistName: artistName.trim(),
                    username: username,
                }),
            });

            if (!res.ok) {
                const errorMessage =
                    res.headers.get("X-Error-Message") || "Failed to load song details.";
                throw new Error(errorMessage);
            }

            const songData = await res.json();
            console.log("Song detail data:", songData);
            setSelectedSongTitle(songData);
            setSongDetailModalOpen(true);
        } catch (err) {
            setToast({
                message: err.message || "Error loading song details.",
                type: "error",
            });
        }
    };

    const handleAddToFavorites = async () => {
        try {
            const res = await fetch("/api/favorites/addSongToFavorites", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    songName: pendingFavoriteSong,
                    artistName: artistName.trim(),
                    username: username,
                }),
            });

            const resultMessage = await res.text();

            if (!res.ok) {
                throw new Error(resultMessage || "Failed to add song to favorites.");
            }

            // Use result message to determine toast type
            if (resultMessage.toLowerCase().includes("duplicate")) {
                setToast({message: `"${pendingFavoriteSong}" is already in your favorites.`, type: "error"});
            } else {
                setToast({message: `${resultMessage}`, type: "success"});
            }
        } catch (err) {
            setToast({
                message: err.message || "An unknown error occurred while adding to favorites.",
                type: "error",
            });
        } finally {
            setFavoriteModalOpen(false);
        }
    };

    const handleUpdateExistingWordCloudFavorites = async () => {
        try {

            const username = localStorage.getItem("username");
            if (!username) {
                setToast({ message: "Username is missing.", type: "error" });
                return;
            }

            if (weights.length === 0) {
                setToast({message: "Can't update empty word cloud.", type: "error"});
                return;
            }

            const res = await fetch("/api/favorites/updateExistingWordCloudWithFavorites", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    username: username,
                    wordCloud: JSON.stringify(weights), // weights already has { text, value }
                }),
            });

            console.log("Submitting to backend:", res);

            if (!res.ok) {
                const errorMessage =
                    res.headers.get("X-Error-Message") ||
                    `Failed to update existing word cloud.`;
                throw new Error(errorMessage);
            }

            const updatedData = await res.json();
            const formatted = updatedData.map((item) => ({
                text: String(item.word),
                value: Number(item.count),
            }));

            setWeights(formatted);
            setToast({message: "Word cloud updated!", type: "success"});
        } catch (err) {
            setToast({
                message: err.message || "An unknown error occurred while updating word cloud.",
                type: "error",
            });
        }
    };

    const handleUpdateExistingWordCloudSearch = async () => {
        const trimmedArtist = artistName.trim();
        const songCountInput = typeNumberRef.current?.value || "";

        const isNumberProvided = songCountInput.trim() !== "";

        if (!trimmedArtist) {
            setToast({message: "Please enter an artist name.", type: "error"});
            return;
        }

        if (!username) {
            setToast({message: "Username is missing.", type: "error"});
            return;
        }

        try {
            if (weights.length === 0) {
                setToast({message: "Can't update an empty word cloud.", type: "error"});
                return;
            }

            if (isNumberProvided) {
                const songCount = parseInt(songCountInput);

                const requestBody = {
                    artist: trimmedArtist,
                    songCount: songCount,
                    username: username,
                    wordCloudJson: JSON.stringify(weights),
                };

                console.log("Sending to updateExistingWordCloudWithSearchSongCount:", requestBody);

                const res = await fetch("/api/wordcloud/updateExistingWordCloudWithSearchSongCount", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(requestBody),
                });

                if (!res.ok) {
                    const errorMessage = res.headers.get("X-Error-Message") || "Failed to update word cloud.";
                    throw new Error(errorMessage);
                }

                const updatedData = await res.json();
                const formatted = updatedData.map(item => ({
                    text: String(item.word),
                    value: Number(item.count),
                }));

                setWeights(formatted);
                setToast({message: "Word cloud updated with search song count!", type: "success"});

            } else {
                // No number provided -> fallback to fetching songs manually
                const result = await resolveArtistAndFetchSongs(trimmedArtist);
            }

        } catch (err) {
            setToast({
                message: err.message || "Error updating word cloud from search.",
                type: "error",
            });
        }
    };

    const renderWordTable = () => {
        return (
            <div
                className="overflow-auto max-h-[400px] mt-4 mb-10 pb-6 border border-gray-400 rounded w-[500px] shadow-md">
                <table className="min-w-full bg-gray-200 text-center border-collapse">
                    <thead>
                    <tr>
                        <th className="border border-black px-4 py-2">Word</th>
                        <th className="border border-black px-4 py-2">Frequency</th>
                    </tr>
                    </thead>
                    <tbody>
                    {weights.map((wordObj, index) => (
                        <tr key={index}>
                            <td
                                className="border border-black px-4 py-2 cursor-pointer hover:bg-blue-100"
                                onClick={() => handleWordClick(wordObj.text)}
                            >
                                {wordObj.text}
                            </td>
                            <td className="border border-black px-4 py-2">{wordObj.value}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        );
    };

    return (
        <div
            className="flex flex-col min-h-screen"
            role="application"
            aria-labelledby="page-title"
        >
            <main
                id="main-content"
                className="flex-grow bg-white flex flex-col items-center mt-20 space-y-6"
            >
                <h1 id="page-title" className="text-2xl font-semibold mb-4">
                    Search Songs
                </h1>

                <div
                    className="flex flex-col space-y-4 items-center"
                    role="form"
                    aria-label="Search songs by artist"
                >
                    <div className="flex space-x-2" role="group" aria-label="Artist search inputs">
                        <input
                            id="artist-name-input"
                            type="text"
                            value={artistName}
                            onChange={(e) => setArtistName(e.target.value)}
                            placeholder="Type artist"
                            aria-label="Artist name"
                            className="w-80 px-4 py-2 border border-gray-400 rounded bg-gray-200"
                        />
                        <input
                            id="song-number-input"
                            type="text"
                            ref={typeNumberRef}
                            placeholder="Optional: number of songs"
                            aria-label="Number of songs (optional)"
                            className="w-64 px-4 py-2 border border-gray-400 rounded bg-gray-200"
                        />
                        <button
                            onClick={handleWordCloudSubmit}
                            aria-label="Submit search"
                            className="bg-blue-300 px-4 py-2 rounded shadow hover:bg-blue-400"
                        >
                            Submit
                        </button>
                    </div>

                    {showSongPicker && allSongs.length > 0 && (
                        <div
                            className="bg-yellow-100 p-4 rounded shadow-md w-[350px] text-sm space-y-2"
                            role="region"
                            aria-label="Song picker"
                        >
                            {allSongs.slice(0, 15).map((song, index) => (
                                <div key={index} className="flex justify-between items-center">
                                    <span>{song}</span>
                                    <button
                                        className="bg-green-200 px-3 py-1 rounded hover:bg-green-300"
                                        onClick={() => toggleSongSelection(song)}
                                        aria-label={
                                            selectedSongs.includes(song)
                                                ? `Remove ${song}`
                                                : `Add ${song}`
                                        }
                                    >
                                        {selectedSongs.includes(song) ? "Remove" : "Add"}
                                    </button>
                                </div>
                            ))}

                            <button
                                className="mt-4 bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
                                onClick={submitSelectedSongs}
                                aria-label="Submit selected songs"
                            >
                                Submit
                            </button>
                        </div>
                    )}

                    <div className="flex space-x-4 mt-4" role="group" aria-label="View options">
                        <button
                            onClick={() => setShowTable(false)}
                            aria-label="View as graph"
                            className="bg-purple-400 hover:bg-purple-600 text-black font-medium px-4 py-2 rounded"
                        >
                            View as graph
                        </button>
                        <button
                            onClick={() => setShowTable(true)}
                            aria-label="View as table"
                            className="bg-purple-400 hover:bg-purple-600 text-black font-medium px-4 py-2 rounded"
                        >
                            View as table
                        </button>
                        <button
                            onClick={handleGenerateFromFavorites}
                            aria-label="Generate word cloud based on favorites"
                            className="bg-blue-300 hover:bg-blue-400 text-black font-medium px-4 py-2 rounded"
                        >
                            Generate word cloud based on favorites
                        </button>
                        <button
                            onClick={handleUpdateExistingWordCloudFavorites}
                            aria-label="Update existing word cloud with favorites"
                            className="bg-blue-300 hover:bg-blue-400 text-black font-medium px-4 py-2 rounded"
                        >
                            Update existing word cloud with favorites
                        </button>
                        <button
                            onClick={handleUpdateExistingWordCloudSearch}
                            aria-label="Update existing word cloud with search"
                            className="bg-blue-300 hover:bg-blue-400 text-black font-medium px-4 py-2 rounded"
                        >
                            Update existing word cloud with Search
                        </button>
                    </div>

                    {showArtistOptions && possibleArtists.length > 0 && (
                        <div
                            className="bg-gray-100 p-6 rounded border border-black-400 mt-4 w-full max-w-4xl pb-16"
                            role="region"
                            aria-label="Possible artist suggestions"
                        >
                            <h2 className="font-semibold mb-4 text-lg text-gray-800">
                                Did you mean one of these artists?
                            </h2>

                            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
                                {possibleArtists.slice(0, 12).map((artist, index) => {
                                    const artistName =
                                        typeof artist === "string" ? artist : artist.name;
                                    const imageUrl =
                                        typeof artist === "object" ? artist.imageUrl : null;

                                    const handleSelect = () => {
                                        setArtistName(artistName);
                                        setShowArtistOptions(false);
                                        setPossibleArtists([]);

                                        if (!wasNumberProvided) {
                                            fetchAndShowSongPicker(artistName);
                                        } else {
                                            handleWordCloudSubmit();
                                        }
                                    };

                                    return (
                                        <div
                                            key={index}
                                            role="button"
                                            tabIndex={0}
                                            aria-label={`Select artist ${artistName}`}
                                            className="flex flex-col items-center cursor-pointer group"
                                            onClick={handleSelect}
                                            onKeyDown={(e) => {
                                                if (e.key === "Enter" || e.key === " ") {
                                                    e.preventDefault();
                                                    handleSelect();
                                                }
                                            }}
                                        >
                                            <div
                                                className="w-24 h-24 bg-gray-300 rounded shadow overflow-hidden group-hover:opacity-80">
                                                {imageUrl ? (
                                                    <img
                                                        src={imageUrl}
                                                        alt={`Image of ${artistName}`}
                                                        className="w-full h-full object-cover"
                                                    />
                                                ) : (
                                                    <div
                                                        className="w-full h-full flex items-center justify-center text-gray-500 text-sm">
                                                        No Image
                                                    </div>
                                                )}
                                            </div>
                                            <p className="mt-2 text-sm text-center font-medium text-blue-600 group-hover:underline">
                                                {artistName}
                                            </p>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    )}

                    {showTable && weights.length > 0 && (
                        <div className="mb-20" role="region" aria-label="Word frequency table">
                            {renderWordTable()}
                        </div>
                    )}

                    {!showTable && !showArtistOptions && memoizedWords.length > 0 && (
                        <StableWordCloud
                            words={memoizedWords}
                            onWordClick={handleWordCloudClick}
                            aria-label="Word cloud visualization"
                        />
                    )}
                </div>
            </main>

            <footer className="w-full bg-gray-800 text-white py-4 px-6 text-center">
                <Team/>
            </footer>

            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    responseBody={toast.responseBody}
                    onClose={() => setToast(null)}
                />
            )}

            <Modal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                onSubmit={(e) => {
                    e.preventDefault();
                    setModalOpen(false);
                }}
                title={`Songs with "${selectedWord}"`}
                confirmText="Close"
                cancelText="Back"
                aria-label="Songs with selected word"
            >
                <div
                    className="max-h-[400px] overflow-y-auto"
                    role="table"
                    aria-label="Song list and frequencies"
                >
                    <table className="min-w-full bg-gray-200 text-center border-collapse mt-4">
                        <thead>
                        <tr>
                            <th className="border border-black px-4 py-2">Title</th>
                            <th className="border border-black px-4 py-2">Frequency</th>
                        </tr>
                        </thead>
                        <tbody>
                        {songFrequencies.map((song, index) => {
                            const handleSongClick = () => {
                                clickedRecentlyRef.current = true;
                                clearTimeout(hoverTimerRef.current);
                                handleSongTitleClick(song.song);
                                setTimeout(() => {
                                    clickedRecentlyRef.current = false;
                                }, 300);
                            };

                            return (
                                <tr key={index}>
                                    <td
                                        className="border border-black px-4 py-2 cursor-pointer hover:bg-blue-100 relative"
                                        role="button"
                                        tabIndex={0}
                                        aria-label={`Details for ${song.song}`}
                                        onClick={handleSongClick}
                                        onKeyDown={(e) => {
                                            if (e.key === "Enter" || e.key === " ") {
                                                e.preventDefault();
                                                handleSongClick();
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
                                                !favoriteModalOpen &&
                                                !clickedRecentlyRef.current
                                            ) {
                                                clearTimeout(hoverTimerRef.current);
                                                hoverTimerRef.current = setTimeout(() => {
                                                    setPendingFavoriteSong(song.song);
                                                    setFavoriteModalOpen(true);
                                                }, 400);
                                            } else {
                                                clearTimeout(hoverTimerRef.current);
                                            }
                                        }}
                                        onMouseLeave={() => {
                                            clearTimeout(hoverTimerRef.current);
                                        }}
                                    >
                                        {song.song}
                                    </td>
                                    <td className="border border-black px-4 py-2">
                                        {song.count}
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                </div>
            </Modal>

            <Modal
                isOpen={songDetailModalOpen}
                onClose={() => setSongDetailModalOpen(false)}
                onSubmit={(e) => {
                    e.preventDefault();
                    setSongDetailModalOpen(false);
                }}
                title={`Details for "${selectedSongTitle?.title || ""}"`}
                confirmText="Close"
                cancelText="Back"
                aria-label="Song details"
            >
                {selectedSongTitle ? (
                    <div className="text-left max-h-[400px] overflow-y-auto space-y-2 px-1">
                        <p>
                            <strong>Title:</strong> {selectedSongTitle.title}
                        </p>
                        <p>
                            <strong>Artist:</strong> {selectedSongTitle.artist}
                        </p>
                        {selectedSongTitle.year && (
                            <p>
                                <strong>Year:</strong> {selectedSongTitle.year}
                            </p>
                        )}
                        <p>
                            <strong>Lyrics:</strong>
                        </p>
                        <pre className="whitespace-pre-wrap bg-gray-100 p-2 rounded border border-gray-300">
                        {highlightLyrics(selectedSongTitle.lyrics, selectedWord)}
                    </pre>
                    </div>
                ) : (
                    <p>Loading...</p>
                )}
            </Modal>

            <Modal
                isOpen={favoriteModalOpen}
                onClose={() => setFavoriteModalOpen(false)}
                onSubmit={(e) => {
                    e.preventDefault();
                    setFavoriteModalOpen(false);
                    handleAddToFavorites();
                }}
                title={`Add "${pendingFavoriteSong}" to favorites?`}
                confirmText="Yes"
                cancelText="No"
                aria-label="Add to favorites confirmation"
            >
                <div className="text-center space-y-4">
                    <p>
                        Would you like to add <strong>{pendingFavoriteSong}</strong> to your
                        favorites?
                    </p>
                </div>
            </Modal>
        </div>
    );
}

export default Search;

const normalizeWords = (weights) => {
    if (weights.length === 0) return weights;
    const minValue = Math.min(...weights.map((weight) => weight.value));
    const maxValue = Math.max(...weights.map((weight) => weight.value));
    return weights.map((weight) => ({
        ...weight,
        value:
            minValue === maxValue
                ? 300
                : 100 + ((weight.value - minValue) / (maxValue - minValue)) * 400,
    }));
};

const highlightLyrics = (lyrics, targetWord) => {
    if (!targetWord) return lyrics;

    const normalizedTarget = lemmatizer.verb(targetWord.toLowerCase()); // lemmatize the target word

    return lyrics.split(/\b/).map((part, i) => {
        const isWord = /^[a-zA-Z]+$/.test(part); // check if it's a word
        const lemma = isWord ? lemmatizer.verb(part.toLowerCase()) : null;

        if (lemma === normalizedTarget) {
            return (
                <mark key={i} className="bg-yellow-300 font-bold">{part}</mark>
            );
        } else {
            return <span key={i}>{part}</span>;
        }
    });
};

const rotate = (word) => (word.value % 2 === 0 ? 0 : 90);
const fontSizeMapper = (word) => Math.log2(word.value + 1) * 20;

const mergeNewWordData = async (res, weights, setWeights) => {

    const newData = await res.json(); // [{ word, count }]
    const newWordMap = new Map();

    newData.forEach(item => {
        const word = item.word;
        const count = item.count;
        newWordMap.set(word, count);
    });

    const mergedMap = new Map();
    weights.forEach(({ text, value }) => {
        mergedMap.set(text, (mergedMap.get(text) || 0) + value);
    });
    newWordMap.forEach((count, word) => {
        mergedMap.set(word, (mergedMap.get(word) || 0) + count);
    });

    const mergedWeights = Array.from(mergedMap.entries()).map(([text, value]) => ({
        text,
        value,
    }));

    setWeights(mergedWeights);
    return mergedWeights;
};


export { normalizeWords, highlightLyrics, rotate, fontSizeMapper};