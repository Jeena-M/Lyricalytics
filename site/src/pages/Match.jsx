import React, { useState } from "react";
import Team from "../components/Team";
import ParticleBurst from "../components/Animations";

function Match() {
    const [resultType, setResultType] = useState("");
    const [matchedUser, setMatchedUser] = useState(null);
    const [matchedSongs, setMatchedSongs] = useState([]);
    const [error, setError] = useState(null);

    const username = localStorage.getItem("username");

    const handleMatch = async (type) => {
        setError(null);
        setResultType(type);
        setMatchedUser(null);
        setMatchedSongs([]);

        try {
            const endpoint = `/api/favorites/get${type.charAt(0).toUpperCase() + type.slice(1)}`;
            const res = await fetch(endpoint, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username }),
            });

            if (!res.ok) {
                const errMsg = res.headers.get("X-Error-Message") || "Something went wrong.";
                throw new Error(errMsg);
            }

            const data = await res.json();

            if (data.username === username) {
                throw new Error("No match found. Please try again later.");
            }

            setMatchedUser(data.username);
            setMatchedSongs(data.favorites);
        } catch (err) {
            console.error(err);
            setError(err.message);
        }
    };

    return (
        <div className="flex flex-col min-h-screen">
            <main
                role="main"
                aria-labelledby="matchHeading"
                className="flex-grow bg-white flex flex-col items-center pt-24"
            >
                <h1 id="matchHeading" className="text-2xl font-semibold mb-6">
                    Find My Lyrical Soulmate or Enemy
                </h1>

                <div role="group" aria-label="Match type selection" className="flex space-x-6 mb-8">
                    <button
                        onClick={() => handleMatch("soulmate")}
                        className="bg-teal-100 hover:bg-teal-200 text-black px-6 py-2 rounded shadow"
                    >
                        Find Soulmate
                    </button>
                    <button
                        onClick={() => handleMatch("enemy")}
                        className="bg-teal-100 hover:bg-teal-200 text-black px-6 py-2 rounded shadow"
                    >
                        Find Enemy
                    </button>
                </div>

                {error && (
                    <p role="alert" aria-live="assertive" className="text-red-600 mb-4">
                        {error}
                    </p>
                )}

                {matchedUser && (
                    <>
                        <div
                            className="relative w-full h-[300px] mb-4"
                            aria-hidden="true"
                        >
                            <ParticleBurst
                                key={Date.now()}
                                isSoulmate={resultType === "soulmate"}
                            />
                        </div>

                        <p className="text-lg mb-4">
                            Your {resultType} is: <strong>{matchedUser}</strong>
                        </p>

                        {matchedSongs.length > 0 ? (
                            <div className="overflow-auto mb-10">
                                <table
                                    className="min-w-full border border-black text-center"
                                    aria-labelledby="songsCaption"
                                >
                                    <caption id="songsCaption" className="sr-only">
                                        Favorite songs of matched user {matchedUser}
                                    </caption>
                                    <thead>
                                    <tr className="bg-gray-300">
                                        <th scope="col" className="px-6 py-2 border border-black">
                                            Song Title
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {matchedSongs.map((song, idx) => (
                                        <tr key={idx} className="bg-gray-200 border border-black">
                                            <td scope="row" className="px-6 py-2 border border-black">
                                                {song.title}
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="text-gray-600">No favorite songs found for this user.</p>
                        )}
                    </>
                )}
            </main>

            <footer role="contentinfo" className="w-full bg-gray-800 text-white py-4 px-6 text-center">
                <Team />
            </footer>
        </div>
    );
}

export default Match;