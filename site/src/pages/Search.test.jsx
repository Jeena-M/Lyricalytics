    import React from "react";
    import {
        render,
        screen,
        fireEvent,
        waitFor,
        act,
        within,
        cleanup,
    } from "@testing-library/react";
    import Search, {
        normalizeWords,
        highlightLyrics,
        rotate,
        fontSizeMapper,
    } from "./Search";

    // --- Mocks ---
    // Capture the props passed to the WordCloud component.
    let capturedWordCloudProps = null;
    jest.mock("react-d3-cloud", () => (props) => {
        capturedWordCloudProps = props;
        return <div data-testid="word-cloud" />;
    });

    // Mock other components.
    jest.mock("../components/Team", () => () => <div data-testid="team">Team</div>);
    jest.mock("../components/Toast", () => ({ message, type, responseBody, onClose }) => (
        <div data-testid="toast">
            {message}
            <button onClick={onClose}>Close Toast</button>
        </div>
    ));
    jest.mock("../components/Modal", () => ({
                                                isOpen,
                                                onClose,
                                                onSubmit,
                                                title,
                                                confirmText,
                                                cancelText,
                                                children,
                                            }) =>
        isOpen ? (
            <div data-testid="modal">
                <div>{title}</div>
                <div>{children}</div>
                <button onClick={onClose}>{cancelText}</button>
                <button onClick={onSubmit}>{confirmText}</button>
            </div>
        ) : null
    );

    // Spy on console.log (and later on console.error or console.warn if needed)
    let logSpy;
    beforeAll(() => {
        logSpy = jest.spyOn(console, "log").mockImplementation(() => {});
    });
    afterAll(() => {
        logSpy.mockRestore();
    });
    afterEach(cleanup);

    describe("Helper Functions", () => {
        test("fontSizeMapper computes correct font size", () => {
            const result = fontSizeMapper({ value: 15 });
            expect(result).toBeCloseTo(Math.log2(16) * 20);
        });

        test("rotate returns 0 for even values", () => {
            expect(rotate({ value: 10 })).toBe(0);
        });

        test("rotate returns 90 for odd values", () => {
            expect(rotate({ value: 7 })).toBe(90);
        });

        test("normalizeWords returns empty array when input is empty", () => {
            expect(normalizeWords([])).toEqual([]);
        });

        test("normalizeWords returns default size 300 when all values are same", () => {
            const input = [{ value: 5, text: "a" }, { value: 5, text: "b" }];
            const output = normalizeWords(input);
            output.forEach((item) => {
                expect(item.value).toBe(300);
            });
        });

        test("normalizeWords scales values correctly when values differ", () => {
            const input = [{ value: 5, text: "a" }, { value: 10, text: "b" }];
            const output = normalizeWords(input);
            expect(output[0].value).toBeCloseTo(100);
            expect(output[1].value).toBeCloseTo(500);
        });

        test("highlightLyrics highlights matching words based on lemmatization", () => {
            // "running" and "run" are treated as the same.
            const lyrics = "I am running and I run every day.";
            const result = highlightLyrics(lyrics, "run");
            const marked = result.filter(
                (el) =>
                    el &&
                    el.type === "mark" &&
                    typeof el.props.children === "string" &&
                    el.props.children.toLowerCase().includes("run")
            );
            expect(marked.length).toBeGreaterThanOrEqual(1);
        });

        test("highlightLyrics returns unchanged lyrics when target word is falsy", () => {
            expect(highlightLyrics("Some lyrics here", "")).toEqual("Some lyrics here");
            expect(highlightLyrics("Some lyrics here", null)).toEqual("Some lyrics here");
            expect(highlightLyrics("Some lyrics here", undefined)).toEqual("Some lyrics here");
        });
    });

    describe("Search Component", () => {
        beforeEach(() => {
            jest
                .spyOn(Storage.prototype, "getItem")
                .mockImplementation((key) => key === "username" ? "testuser" : null);

            jest.spyOn(global, "fetch").mockRejectedValue(new Error("network failure"));

            capturedWordCloudProps = null;
        });

        afterEach(() => {
            jest.restoreAllMocks();
            cleanup();
        });
        test("handleGenerateFromFavorites shows toast on error with X-Error-Message", async () => {
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 400,
                headers: { get: () => "Favorite fetch error" },
            });

            render(<Search />);
            const button = screen.getByText(/Generate word cloud based on favorites/i);
            fireEvent.click(button);

            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Favorite fetch error");
            });

            localStorage.removeItem("username");
        });

        test("handleGenerateFromFavorites shows fallback error if err.message is empty", async () => {
            localStorage.setItem("username", "testUser");
            global.fetch.mockRejectedValueOnce({ message: "" });

            render(<Search />);
            const button = screen.getByText(/Generate word cloud based on favorites/i);
            fireEvent.click(button);

            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to generate word cloud from favorites.");
            });

            localStorage.removeItem("username");
        });



        test("displays toast error when artist name is empty on submit", async () => {
            render(<Search />);
            const submitButton = screen.getByText(/Submit/);
            fireEvent.click(submitButton);
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Please enter an artist name.")
            );
        });



        test("handleWordClick displays error toast when fetch fails (with number provided)", async () => {
            // Provide a valid generate response so that weights are set
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 50 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "FetchErrorTest" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "3" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            // Wait until the word cloud is rendered (i.e. weights are not empty)
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });
            // Simulate an error response for the word click fetch (song frequency)
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Test error message" },
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "errorTest" });
            });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Test error message");
            });
        });

        test("handleSongTitleClick error branch", async () => {
            const fakeResponse = [{ word: "epsilon", count: 20 }];
            // Generate the word cloud by simulating a successful generate call.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => fakeResponse,
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistDetail" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });
            // Simulate a successful song frequency fetch to open the frequency modal.
            const songFreqData = [{ song: "SongDetail", count: 3 }];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => songFreqData,
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "epsilon" });
            });
            // Wait until the frequency modal (the first Modal) is rendered.
            await waitFor(() => {
                expect(screen.getByTestId("modal")).toBeInTheDocument();
            });
            // Now simulate an error when clicking on a song title within the modal.
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Details error" },
            });
            const songTitleCell = screen.getByText("SongDetail");
            await act(async () => {
                fireEvent.click(songTitleCell);
            });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Details error");
            });
        });

        test("handleWordCloudClick logs warning on invalid word object", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "WarnArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            const warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});
            // Calling onWordClick with an object that has no .text property.
            act(() => {
                capturedWordCloudProps.onWordClick({}, {});
            });
            expect(warnSpy).toHaveBeenCalledWith("Invalid word clicked:", {});
            warnSpy.mockRestore();
        });

        test("calls handleWordMouseOver and handleWordMouseOut callbacks", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "MouseTest" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            const logSpy = jest.spyOn(console, "log").mockImplementation(() => {});
            act(() => {
                capturedWordCloudProps.onWordMouseOver({}, { text: "mouseOverTest" });
                capturedWordCloudProps.onWordMouseOut({}, { text: "mouseOutTest" });
            });
            expect(logSpy).toHaveBeenCalledWith("Word mouse over:", "mouseOverTest");
            expect(logSpy).toHaveBeenCalledWith("Word mouse out:", "mouseOutTest");
            logSpy.mockRestore();
        });

        test("handleWordCloudSubmit ambiguous branch with number provided", async () => {
            // For the number-provided branch, simulate a generate call that returns an error with a header message including "Found many artists"
            global.fetch
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => "Found many artists - ambiguous" },
                })
                // Then simulate fetching ambiguous artist list.
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["AmbiguousArtist1", "AmbiguousArtist2"],
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistAmbiguousNum" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "4" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByText(/Did you mean one of these artists\?/)).toBeInTheDocument();
                expect(screen.getByText("AmbiguousArtist1")).toBeInTheDocument();
                expect(screen.getByText("AmbiguousArtist2")).toBeInTheDocument();
            });
        });

        test("submitSelectedSongs error branch shows toast on failure", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SingleArtist"],
                })
                // Then simulate fetching songs successfully.
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongA", "SongB"],
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "SongPickerError" },
            });
            // Leave number field empty.
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByText("SongA")).toBeInTheDocument());
            // Toggle song selection for SongA.
            const songAContainer = screen.getByText("SongA").parentElement;
            const addButton = within(songAContainer).getByRole("button", { name: /add/i });
            fireEvent.click(addButton);
            // Now, simulate submitSelectedSongs error.
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to generate word cloud." },
            });
            // Find and click the submit button in the song picker container.
            const pickerContainer = songAContainer.closest("div.bg-yellow-100");
            const submitPickerButton = within(pickerContainer).getByText("Submit");
            await act(async () => {
                fireEvent.click(submitPickerButton);
            });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to generate word cloud.");
            });
        });


        test("handleWordClick uses '/api/wordcloud/songFrequencyForWordInLyricsNoNumber' when number input is cleared", async () => {
            // First, supply a valid number so that the generate branch sets weights and renders the word cloud.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "TestArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "3" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument()
            );

            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });

            const fakeSongFreq = [{ song: "SongNoNum", count: 5 }];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => fakeSongFreq,
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "testNoNumber" });
            });
            await waitFor(() => {
                // Expect that fetch was called with the "NoNumber" endpoint.
                expect(global.fetch).toHaveBeenCalledWith(
                    "/api/wordcloud/songFrequencyForWordInLyricsNoNumber",
                    expect.any(Object)
                );
            });
        });

        test("handleWordClick falls back to default error message when header returns null", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistErrorNull" },
            });
            // Provide a valid numeric input (so that weights are set)
            // then clear the input so that handleWordClick uses the no-number branch.
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument()
            );

            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });

            // Now simulate a fetch response returning not ok with header.get returning null.
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 500,
                headers: { get: () => null },
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "errorNoHeader" });
            });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to load song frequency.");
            });
        });

        test("handleWordClick shows fallback message for empty rejection error", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistRejectEmpty" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument()
            );

            // Clear number so that no number branch is used.
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });

            // Simulate a rejection with an empty error message.
            global.fetch.mockRejectedValueOnce({ message: "" });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "rejectEmpty" });
            });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Something went wrong loading song frequencies.");
            });
        });

        test("fetchAndShowSongPicker shows error toast when addSong fetch fails", async () => {
            // Simulate the no-number branch in handleWordCloudSubmit:
            // First, the artists fetch returns a single artist.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["UniqueArtist"],
            });
            // Then, the addSong fetch fails.
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to fetch songs." },
            });
            render(<Search />);
            // Use no number so that the no-number branch is used.
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistPickerFail" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            // Since fetchAndShowSongPicker is called inside the ambiguous branch, expect the toast.
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.");
            });
        });

        test("handleWordMouseOver and handleWordMouseOut callbacks log expected messages", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistMouseCB" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());

            const logSpy = jest.spyOn(console, "log").mockImplementation(() => {});
            act(() => {
                capturedWordCloudProps.onWordMouseOver({}, { text: "overTest" });
                capturedWordCloudProps.onWordMouseOut({}, { text: "outTest" });
            });
            expect(logSpy).toHaveBeenCalledWith("Word mouse over:", "overTest");
            expect(logSpy).toHaveBeenCalledWith("Word mouse out:", "outTest");
            logSpy.mockRestore();
        });

        //
    // 1) artistRes fetch fails => covers "Error fetching possible artists."
    //
        test("handleWordCloudSubmit throws error when artistRes is not ok", async () => {
            // No number => the code calls /api/wordcloud/artists
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 400,
            });
            render(<Search />);

            // Provide an artist, leave the number field blank
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "FailArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // The error should appear in a toast.
            await waitFor(() => {
                // "Error fetching possible artists." is thrown => caught => setToast
                expect(screen.getByTestId("toast")).toHaveTextContent("Error fetching possible artists.");
            });
        });

    //
    // 2) artistList has multiple items => sets possible artists => returns early
    //
        test("handleWordCloudSubmit handles multiple artists by showing artist options and returning", async () => {
            // First fetch: /api/wordcloud/artists => returns multiple artists
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["ArtistOne", "ArtistTwo"],
            });
            render(<Search />);

            // Provide an artist, leave number blank to trigger that code path
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "AmbiguousArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // Because artistList.length > 1, the component sets possibleArtists and showArtistOptions => returns
            await waitFor(() => {
                expect(screen.getByText("Did you mean one of these artists?")).toBeInTheDocument();
                expect(screen.getByText("ArtistOne")).toBeInTheDocument();
                expect(screen.getByText("ArtistTwo")).toBeInTheDocument();
            });
        });

    //
    // 3) artistList has exactly 1 item (string) => calls fetchAndShowSongPicker => that fetch fails
    //
        test("handleWordCloudSubmit calls fetchAndShowSongPicker, which fails fetch", async () => {
            // /api/wordcloud/artists => returns exactly one (string)
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["SingleArtistString"],
            });
            // Next fetch (fetchAndShowSongPicker => /api/wordcloud/addSong) fails
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to fetch songs from addSong." },
            });
            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "SingleArtist" },
            });
            // no number => triggers the no-number path
            fireEvent.click(screen.getByText(/^Submit$/));

            // Should show a toast from the fetchAndShowSongPicker catch
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs from addSong.");
            });
        });

    //
    // 4) artistList has exactly 1 item (object) => calls fetchAndShowSongPicker => success => sets songs
    //
        test("handleWordCloudSubmit calls fetchAndShowSongPicker successfully with an object artist", async () => {
            // /api/wordcloud/artists => returns exactly one (object)
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ name: "SingleArtistObj" }],
            });
            // Now fetchAndShowSongPicker => addSong => successful fetch
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["SongX", "SongY", "SongZ"],
            });
            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistObj" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // Wait for songs to appear in the song picker
            await waitFor(() => {
                expect(screen.getByText("SongX")).toBeInTheDocument();
                expect(screen.getByText("SongY")).toBeInTheDocument();
                expect(screen.getByText("SongZ")).toBeInTheDocument();
            });
        });

    //
    // 5) parseInt(songCountInput) || 0 => user typed "abc" => parseInt => NaN => fallback is 0 => uses /api/wordcloud/generate
    //
    //     test("handleWordCloudSubmit uses 0 songs when user input is non-numeric", async () => {
    //         // Return a successful JSON for the /generate call
    //         global.fetch.mockResolvedValueOnce({
    //             ok: true,
    //             json: async () => [{ word: "testWord", count: 10 }],
    //         });
    //         render(<Search />);
    //
    //         // Provide a numeric artist, but an alphabetic count => parseInt => NaN => 0
    //         fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
    //             target: { value: "ArtistNumFallback" },
    //         });
    //         fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
    //             target: { value: "abc" },
    //         });
    //         fireEvent.click(screen.getByText(/^Submit$/));
    //
    //         await waitFor(() => {
    //             // The word-cloud should appear after the generate call
    //             expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
    //         });
    //         // Confirm the generate call used songCount=0
    //         expect(global.fetch).toHaveBeenCalledWith(
    //             "/api/wordcloud/generate",
    //             expect.objectContaining({
    //                 method: "POST",
    //                 body: JSON.stringify({
    //                     artistName: "ArtistNumFallback",
    //                     songCount: 0,
    //                 }),
    //             })
    //         );
    //     });

        test("handleWordCloudSubmit uses 0 songs when user input is non-numeric", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "testWord", count: 10 }],
            });

            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistNumFallback" },
            });

            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "abc" },
            });

            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            // Parse body string to compare fields flexibly
            const expectedPayload = {
                artistName: "ArtistNumFallback",
                songCount: 0,
                username: "testuser", // <== match what your app is actually sending
            };

            const lastCall = global.fetch.mock.calls[0]; // grab fetch call
            const actualUrl = lastCall[0];
            const actualOptions = lastCall[1];
            const actualBody = JSON.parse(actualOptions.body);

            expect(actualUrl).toBe("/api/wordcloud/generate");
            expect(actualOptions.method).toBe("POST");
            expect(actualBody).toEqual(expectedPayload);
        });



        test("fetchAndShowSongPicker (default count=15) success: slices to 15 and shows song picker", async () => {
            // Step 1: /api/wordcloud/artists => exactly one result => triggers fetchAndShowSongPicker
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["ArtistSingle"],
            });
            // Step 2: /api/wordcloud/addSong => returns 20 songs
            const twentySongs = Array.from({ length: 20 }).map((_, i) => `Song${i + 1}`);
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => twentySongs,
            });

            render(<Search />);

            // Provide an artist name; leave "Optional: number" blank => no-number branch
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistSingle" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                // e.g. "Song1" through "Song15" should appear; "Song16" should NOT
                expect(screen.getByText("Song15")).toBeInTheDocument();
                expect(screen.queryByText("Song16")).toBeNull();
            });

            // Also ensure the song picker container is visible.
            expect(screen.getByText("Song1")).toBeInTheDocument();
            expect(screen.getByText("Song15")).toBeInTheDocument();
            expect(screen.queryByText("Song16")).not.toBeInTheDocument(); // Confirm slicing
        });


        test("fetchAndShowSongPicker (default count=15) failure: shows error toast on !ok response", async () => {
            // /api/wordcloud/artists => single result => calls fetchAndShowSongPicker with default 15
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["ArtistSingleFail"],
            });
            // Then /api/wordcloud/addSong => fails
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to fetch songs with default 15." },
            });

            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistSingleFail" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // The .ok === false branch => throw => catch => setToast => watch for toast
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent(
                    "Failed to fetch songs with default 15."
                );
            });
        });

        test("fetchAndShowSongPicker uses default count=15 and slices to 15 on success", async () => {
            // 1) /api/wordcloud/artists => exactly one artist => triggers fetchAndShowSongPicker with default param.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["SoloArtist"],
            });

            // 2) /api/wordcloud/addSong => returns 20 songs => we confirm only the first 15 appear.
            const twentySongs = Array.from({ length: 20 }, (_, i) => `Song#${i + 1}`);
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => twentySongs,
            });

            render(<Search />);

            // Provide an artist name but leave the “Optional: number…” field blank => no-number path
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "SoloArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // The code sees exactly 1 artist => calls fetchAndShowSongPicker("SoloArtist") => uses default 15.
            // Wait for the song picker with the first 15 songs. 16+ should not appear.
            await waitFor(() => {
                expect(screen.getByText("Song#15")).toBeInTheDocument();
                expect(screen.queryByText("Song#16")).toBeNull(); // Sliced out
            });

            // Also confirm the UI is showing songs 1..15, but not 16..20
            expect(screen.getByText("Song#1")).toBeInTheDocument();
            expect(screen.getByText("Song#15")).toBeInTheDocument();
            expect(screen.queryByText("Song#16")).not.toBeInTheDocument();
        });


        test("fetchAndShowSongPicker fails (default count=15) and shows error toast", async () => {
            // 1) /api/wordcloud/artists => exactly one => triggers fetchAndShowSongPicker with no second param => count=15
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["OneArtistFail"],
            });

            // 2) /api/wordcloud/addSong => fails => we see the fallback or header message
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to fetch songs with default 15." },
            });

            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "OneArtistFail" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // The code sees exactly 1 artist => calls fetchAndShowSongPicker => not ok => throws => catch => setToast
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent(
                    "Failed to fetch songs with default 15."
                );
            });
        });

        test("handleSongTitleClick success: sets song data and opens detail modal", async () => {
            // 1) Generate a word cloud so that there's a word to click.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "alpha", count: 10 }],
            });
            render(<Search />);

            // Provide artist and numeric input (adjust as appropriate for your app)
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "SongDetailArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // Wait for the word cloud to be rendered.
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            // 2) Now simulate a successful fetch of song frequencies.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "SuccessSong", count: 3 }],
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "alpha" });
            });

            // Wait for the frequency modal to appear and the song "SuccessSong" is displayed.
            await waitFor(() => {
                expect(screen.getByTestId("modal")).toBeInTheDocument();
                expect(screen.getByText("SuccessSong")).toBeInTheDocument();
            });

            // 3) Now simulate a successful fetch for song details.
            const mockSongData = { title: "SuccessSong", artist: "SongDetailArtist", year: "2022", lyrics: "Here are lyrics." };
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockSongData,
            });

            // Click the song title cell which triggers handleSongTitleClick.
            const songTitleCell = screen.getByText("SuccessSong");
            await act(async () => {
                fireEvent.click(songTitleCell);
            });

            // 4) Wait for the song detail modal to open; use a custom matcher to check that the lyrics
            // "Here are lyrics." appears even when broken over several elements.
            await waitFor(() => {
                expect(screen.getByText((content, element) => {
                    return element.tagName.toLowerCase() === "pre" &&
                        /here\s+are\s+lyrics\./i.test(element.textContent);
                })).toBeInTheDocument();
                expect(screen.getByText(/Details for "SuccessSong"/)).toBeInTheDocument();
            });
        });


    //
    // Test 2: ERROR path with a custom X-Error-Message => "Server says no" => triggers fallback lines
    //   i.e. if (!res.ok) { const errorMessage = ... throw new Error(errorMessage) } => catch => setToast(err.message)
    //
        test("handleSongTitleClick error path with X-Error-Message", async () => {
            // Same setup to open the frequency modal with a song row to click.
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "beta", count: 5 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ErrorArtistXErr" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "3" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            // Wait for word cloud
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            // Next fetch: song frequencies
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "XErrSong", count: 2 }],
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "beta" });
            });
            await waitFor(() => {
                expect(screen.getByText("XErrSong")).toBeInTheDocument();
            });

            // Now the /api/wordcloud/songDetails call => !ok => X-Error-Message => throw => catch => setToast
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Server says no" },
            });

            // Click the song row
            await act(async () => {
                fireEvent.click(screen.getByText("XErrSong"));
            });

            // Expect the toast to display "Server says no"
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Server says no");
            });
        });

    //
    // Test 3: ERROR path with no X-Error-Message => fallback => "Failed to load song details."
    // => throw => catch => setToast => err.message => fallback => "Error loading song details." if empty
    //
        test("handleSongTitleClick error path with no header message => fallback => setToast with error details", async () => {
            // Setup for frequency modal again
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "gamma", count: 3 }],
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ErrorNoMsg" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            // Frequencies fetch => success => "NoMsgSong"
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "NoMsgSong", count: 1 }],
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "gamma" });
            });
            await waitFor(() => {
                expect(screen.getByText("NoMsgSong")).toBeInTheDocument();
            });

            // Now /api/wordcloud/songDetails => !ok => header is null => "Failed to load song details." => throw => catch => setToast
            // Then we check if the error is empty or not; the code also does err.message || "Error loading song details."
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null },
            });

            await act(async () => {
                fireEvent.click(screen.getByText("NoMsgSong"));
            });

            await waitFor(() => {
                // Typically it will be "Failed to load song details." because that's the thrown message.
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to load song details.");
            });
        });


        test("submitSelectedSongs successfully formats data and updates UI", async () => {
            // Simulate ambiguous branch: first fetch call for artists returns a single artist.
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["ArtistForPicker"],
                })
                // Next, fetch for song list (for picker) returns several songs.
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongA", "SongB", "SongC", "SongD"],
                });
            render(<Search />);

            // Provide artist name and leave the number field empty to trigger the ambiguous branch.
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistForPicker" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            await act(async () => {
                fireEvent.click(screen.getByText(/^Submit$/));
            });

            // Wait for the song picker to display.
            await waitFor(() => {
                expect(screen.getByText("SongA")).toBeInTheDocument();
            });

            // Simulate selecting "SongA" via the toggle button.
            const songAContainer = screen.getByText("SongA").parentElement;
            const addButton = within(songAContainer).getByRole("button", { name: /add/i });
            await act(async () => {
                fireEvent.click(addButton);
            });

            // Now simulate the submission of selected songs:
            // The submitSelectedSongs function is triggered when the user clicks the Submit button
            // inside the song picker container.
            const generateResponse = [
                { word: "alpha", count: "10" },
                { word: "beta", count: "20" }
            ];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => generateResponse,
            });

            const pickerContainer = songAContainer.closest("div.bg-yellow-100");
            const submitPickerButton = within(pickerContainer).getByText("Submit");

            await act(async () => {
                fireEvent.click(submitPickerButton);
            });

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            const expectedFormatted = [
                { text: "alpha", value: 100 },
                { text: "beta", value: 500 }
            ];

            expect(capturedWordCloudProps.data).toEqual(expectedFormatted);

            expect(screen.queryByText("SongA")).toBeNull();
        });

        test("submitSelectedSongs error branch shows toast", async () => {
            // Simulate the ambiguous branch: artists fetch returns a single result.
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["ArtistForPickerError"],
                })
                // The song list fetch returns songs successfully.
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongX", "SongY"],
                });
            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistForPickerError" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByText("SongX")).toBeInTheDocument();
            });

            const songXContainer = screen.getByText("SongX").parentElement;
            const addButton = within(songXContainer).getByRole("button", { name: /add/i });
            fireEvent.click(addButton);

            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Failed to generate word cloud." },
            });

            const pickerContainer = songXContainer.closest("div.bg-yellow-100");
            const submitPickerButton = within(pickerContainer).getByText("Submit");

            await act(async () => {
                fireEvent.click(submitPickerButton);
            });

            // Wait for the toast to display the error.
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to generate word cloud.");
            });
        });

        test("ambiguous artist option click triggers fetchAndShowSongPicker when no number provided", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["AmbigArtist1", "AmbigArtist2"]
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongA"]
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "AmbiguousArtist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByText("AmbigArtist1")).toBeInTheDocument();
            });
            fireEvent.click(screen.getByText("AmbigArtist1"));
            await waitFor(() => {
                expect(global.fetch).toHaveBeenLastCalledWith(
                    "/api/wordcloud/addSong",
                    expect.objectContaining({
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ artistName: "AmbigArtist1" })
                    })
                );
            });
        });

        test("ambiguous artist option click triggers handleWordCloudSubmit when number provided", () => {
            const handleWordCloudSubmit = jest.fn();
            const fetchAndShowSongPicker = jest.fn();
            const wasNumberProvided = true;
            const setArtistName = jest.fn();
            const setShowArtistOptions = jest.fn();
            const setPossibleArtists = jest.fn();

            const ambiguousArtist = { name: "ArtistWithNumber", imageUrl: "http://example.com/image.jpg" };
            const artistName = typeof ambiguousArtist === "string" ? ambiguousArtist : ambiguousArtist.name;

            // onClick simulation for the ambiguous artist block.
            const ambiguousOptionOnClick = () => {
                setArtistName(artistName);
                setShowArtistOptions(false);
                setPossibleArtists([]);
                if (!wasNumberProvided) {
                    fetchAndShowSongPicker(artistName);
                } else {
                    handleWordCloudSubmit();
                }
            };

            ambiguousOptionOnClick();
            expect(handleWordCloudSubmit).toHaveBeenCalled();
        });

        test("onMouseMove with distance >= 40 clears timeout and does not open favorite modal", async () => {
            jest.useFakeTimers();
            const fakeSongFreqData = [{ song: "FarSong", count: 1 }];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistFar" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => fakeSongFreqData
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "dummy" });
            });
            await waitFor(() => expect(screen.getByText("FarSong")).toBeInTheDocument());
            const songCell = screen.getByText("FarSong");
            songCell.getBoundingClientRect = () => ({
                left: 100,
                top: 100,
                width: 100,
                height: 40,
                right: 200,
                bottom: 140
            });
            fireEvent.mouseMove(songCell, { clientX: 190, clientY: 120 });
            act(() => {
                jest.advanceTimersByTime(400);
            });
            expect(screen.queryByText(/Add "FarSong" to favorites\?/)).toBeNull();
            fireEvent.mouseLeave(songCell);
            jest.useRealTimers();
        });

        test("onMouseMove with distance < 40 followed by onMouseLeave clears timer and prevents favorite modal", async () => {
            jest.useFakeTimers();
            const fakeSongFreqData = [{ song: "CloseSong", count: 1 }];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dummy", count: 100 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistClose" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => fakeSongFreqData
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "dummy" });
            });
            await waitFor(() => expect(screen.getByText("CloseSong")).toBeInTheDocument());
            const songCell = screen.getByText("CloseSong");
            songCell.getBoundingClientRect = () => ({
                left: 100,
                top: 100,
                width: 100,
                height: 40,
                right: 200,
                bottom: 140
            });
            fireEvent.mouseMove(songCell, { clientX: 155, clientY: 125 });
            fireEvent.mouseLeave(songCell);
            act(() => {
                jest.advanceTimersByTime(400);
            });
            expect(screen.queryByText(/Add "CloseSong" to favorites\?/)).toBeNull();
            jest.useRealTimers();
        });

        test("fetchAndShowSongPicker displays toast error when songListRes is not ok", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["UniqueArtistError"]
                })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => "Custom error message" }
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "UniqueArtistError" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Custom error message");
            });
        });


        test("fetchAndShowSongPicker throws fallback error when X-Error-Message header returns null", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SingleArtist"]
                })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => null }
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "SingleArtist" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.");
            });
        });

        test("submitSelectedSongs error branch uses fallback error message when header is null", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ["ArtistForPicker"] })
                .mockResolvedValueOnce({ ok: true, json: async () => ["Song1", "Song2", "Song3"] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ArtistForPicker" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByText("Song1")).toBeInTheDocument());
            const songContainer = screen.getByText("Song1").parentElement;
            const addButton = within(songContainer).getByRole("button", { name: /add/i });
            fireEvent.click(addButton);
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null }
            });
            const pickerContainer = songContainer.closest("div.bg-yellow-100");
            const submitPickerButton = within(pickerContainer).getByText("Submit");
            await act(async () => {
                fireEvent.click(submitPickerButton);
            });
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to generate word cloud.")
            );
        });

        test("submitSelectedSongs error branch fallback message when error message is empty", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ["ArtistFallback"] })
                .mockResolvedValueOnce({ ok: true, json: async () => ["SongA", "SongB"] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistFallback" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByText("SongA")).toBeInTheDocument());
            const songContainer = screen.getByText("SongA").parentElement;
            const addButton = within(songContainer).getByRole("button", { name: /add/i });
            fireEvent.click(addButton);
            global.fetch.mockRejectedValueOnce({ message: "" });
            const pickerContainer = songContainer.closest("div.bg-yellow-100");
            const submitPickerButton = within(pickerContainer).getByText("Submit");
            await act(async () => {
                fireEvent.click(submitPickerButton);
            });
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("An error occurred.")
            );
        });

        test("handleSongTitleClick fallback error message when error message is empty", async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "alpha", count: 10 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistDetailTest" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "2" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ song: "SongX", count: 1 }] });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "alpha" });
            });
            await waitFor(() => expect(screen.getByText("SongX")).toBeInTheDocument());
            global.fetch.mockRejectedValueOnce({ message: "" });
            await act(async () => {
                fireEvent.click(screen.getByText("SongX"));
            });
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Error loading song details.")
            );
        });


        test("handleWordCloudSubmit error branch: non ambiguous returns HTTP error message", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 404,
                headers: { get: () => "Not found" }
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "TestNonAmbiguous" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "5" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Not found");
            });
        });

        test("handleWordCloudSubmit ambiguous branch: error fetching possible artists", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => "Found many artists" }
                })
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => "dummy" }
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "AmbiguousTest" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Error fetching possible artists.");
            });
        });

        test("handleAddToFavorites error branch when fetch rejects", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "favTest", count: 10 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "FavArtistReject" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "3" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "FavSongReject", count: 5 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "favTest" });
            });
            await waitFor(() => expect(screen.getByText("FavSongReject")).toBeInTheDocument());
            const songCell = screen.getByText("FavSongReject");
            songCell.getBoundingClientRect = () => ({
                left: 100,
                top: 100,
                width: 100,
                height: 40,
                right: 200,
                bottom: 140
            });
            fireEvent.mouseMove(songCell, { clientX: 150, clientY: 120 });
            act(() => {
                jest.advanceTimersByTime(400);
            });
            const modals = screen.getAllByTestId("modal");
            const favModal = modals.find((m) => m.textContent.includes('Add "FavSongReject" to favorites?'));
            expect(favModal).toBeDefined();
            global.fetch.mockRejectedValueOnce({ message: "Network failure" });
            const yesButton = within(favModal).getByText("Yes");
            await act(async () => {
                fireEvent.click(yesButton);
            });
            await waitFor(() => expect(screen.getByTestId("toast")).toHaveTextContent("Network failure"));
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("toggleSongSelection adds and removes song", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SoloArtist"]
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongToggle1", "SongToggle2"]
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "SoloArtist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByText("SongToggle1")).toBeInTheDocument();
            });
            const songContainer = screen.getByText("SongToggle1").parentElement;
            const toggleButton = within(songContainer).getByRole("button", { name: /Add/i });
            expect(toggleButton).toHaveTextContent("Add");
            fireEvent.click(toggleButton);
            expect(toggleButton).toHaveTextContent("Remove");
            fireEvent.click(toggleButton);
            expect(toggleButton).toHaveTextContent("Add");
        });

        test("handleAddToFavorites duplicate branch sets error toast message", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "favTest", count: 10 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "FavArtistDup" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "3" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "FavSongDup", count: 5 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "favTest" });
            });
            await waitFor(() => expect(screen.getByText("FavSongDup")).toBeInTheDocument());
            const songCell = screen.getByText("FavSongDup");
            songCell.getBoundingClientRect = () => ({ left: 100, top: 100, width: 100, height: 40, right: 200, bottom: 140 });
            fireEvent.mouseMove(songCell, { clientX: 150, clientY: 120 });
            act(() => { jest.advanceTimersByTime(400); });
            const modals = screen.getAllByTestId("modal");
            const favModal = modals.find(m => m.textContent.includes(`Add "FavSongDup" to favorites?`));
            expect(favModal).toBeDefined();
            global.fetch.mockResolvedValueOnce({
                ok: true,
                text: async () => "duplicate entry: song already in favorites"
            });
            const yesButton = within(favModal).getByText("Yes");
            await act(async () => {
                fireEvent.click(yesButton);
            });
            await waitFor(() => expect(screen.getByTestId("toast")).toHaveTextContent(`"FavSongDup" is already in your favorites.`));
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("handleAddToFavorites success branch sets success toast message", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "favTest", count: 10 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "FavArtistSuccess" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "3" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "FavSongSuccess", count: 5 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "favTest" });
            });
            await waitFor(() => expect(screen.getByText("FavSongSuccess")).toBeInTheDocument());
            const songCell = screen.getByText("FavSongSuccess");
            songCell.getBoundingClientRect = () => ({ left: 100, top: 100, width: 100, height: 40, right: 200, bottom: 140 });
            fireEvent.mouseMove(songCell, { clientX: 150, clientY: 120 });
            act(() => { jest.advanceTimersByTime(400); });
            const modals = screen.getAllByTestId("modal");
            const favModal = modals.find(m => m.textContent.includes(`Add "FavSongSuccess" to favorites?`));
            expect(favModal).toBeDefined();
            global.fetch.mockResolvedValueOnce({
                ok: true,
                text: async () => "Song added successfully"
            });
            const yesButton = within(favModal).getByText("Yes");
            await act(async () => {
                fireEvent.click(yesButton);
            });
            await waitFor(() => expect(screen.getByTestId("toast")).toHaveTextContent("Song added successfully"));
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("table cell click triggers handleWordClick and shows modal on success", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: "test", count: 5 }] })
                .mockResolvedValueOnce({ ok: true, json: async () => [{ song: "SongA", count: 2 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistTable" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            fireEvent.click(screen.getByText(/View as table/));
            expect(screen.getByText("test")).toBeInTheDocument();
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText("test"));
            await waitFor(() => expect(screen.getByTestId("modal")).toBeInTheDocument());
        });

        test("table cell click triggers handleWordClick error branch and shows toast", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: "err", count: 3 }] })
                .mockResolvedValueOnce({ ok: false, headers: { get: () => "Cell error" } });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistErr" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            fireEvent.click(screen.getByText(/View as table/));
            expect(screen.getByText("err")).toBeInTheDocument();
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText("err"));
            await waitFor(() => expect(screen.getByTestId("toast")).toHaveTextContent("Cell error"));
        });

        test("clicking 'Back' on song detail modal closes it", async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "w", count: 1 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "Artist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ song: "S1", count: 1 }] });
            act(() => capturedWordCloudProps.onWordClick({}, { text: "w" }));
            await waitFor(() => expect(screen.getByText("S1")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => ({ title: "S1", artist: "Artist", lyrics: "lyrics" }) });
            fireEvent.click(screen.getByText("S1"));
            await waitFor(() => {
                const modals = screen.getAllByTestId("modal");
                const detailModal = modals.find(m => m.textContent.includes('Details for "S1"'));
                const backButton = within(detailModal).getByText("Back");
                fireEvent.click(backButton);
                expect(screen.queryByText(/Details for "S1"/)).toBeNull();
            });
        });

        test("clicking 'Close' on song detail modal closes it", async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "x", count: 1 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistX" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ song: "SX", count: 1 }] });
            act(() => capturedWordCloudProps.onWordClick({}, { text: "x" }));
            await waitFor(() => expect(screen.getByText("SX")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => ({ title: "SX", artist: "ArtistX", lyrics: "ly" }) });
            fireEvent.click(screen.getByText("SX"));
            await waitFor(() => {
                const modals = screen.getAllByTestId("modal");
                const detailModal = modals.find(m => m.textContent.includes('Details for "SX"'));
                const closeButton = within(detailModal).getByText("Close");
                fireEvent.click(closeButton);
                expect(screen.queryByText(/Details for "SX"/)).toBeNull();
            });
        });

        test("clicking 'No' on favorite modal closes it", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "user");
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "f", count: 1 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistF" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ song: "Fav", count: 1 }] });
            act(() => capturedWordCloudProps.onWordClick({}, { text: "f" }));
            await waitFor(() => expect(screen.getByText("Fav")).toBeInTheDocument());
            const cell = screen.getByText("Fav");
            cell.getBoundingClientRect = () => ({ left: 0, top: 0, width: 100, height: 40, right: 100, bottom: 40 });
            fireEvent.mouseMove(cell, { clientX: 50, clientY: 20 });
            act(() => jest.advanceTimersByTime(400));
            const favModals = screen.getAllByTestId("modal");
            const favModal = favModals.find(m => m.textContent.includes('Add "Fav" to favorites?'));
            const noButton = within(favModal).getByText("No");
            fireEvent.click(noButton);
            expect(screen.queryByText('Add "Fav" to favorites?')).toBeNull();
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("clicking 'Close Toast' removes the toast", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null }
            });
            render(<Search />);
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("toast")).toBeInTheDocument());
            fireEvent.click(screen.getByText("Close Toast"));
            expect(screen.queryByTestId("toast")).toBeNull();
        });

        test("clicking 'Close' on frequency modal closes it", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "a", count: 1 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "Artist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "Song1", count: 1 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "a" });
            });
            await waitFor(() => expect(screen.getByText(/Songs with "a"/)).toBeInTheDocument());
            fireEvent.click(screen.getByText("Close"));
            expect(screen.queryByText(/Songs with "a"/)).toBeNull();
        });

        test("frequency modal onClose via Back button closes modal", async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "w", count: 1 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "Artist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ song: "S2", count: 1 }] });
            act(() => capturedWordCloudProps.onWordClick({}, { text: "w" }));
            await waitFor(() => expect(screen.getByText(/Songs with "w"/)).toBeInTheDocument());
            const modals = screen.getAllByTestId("modal");
            const freqModal = modals.find(m => m.textContent.includes('Songs with "w"'));
            const backButton = within(freqModal).getByText("Back");
            fireEvent.click(backButton);
            expect(screen.queryByText(/Songs with "w"/)).toBeNull();
        });

        test("clicking View as table and View as graph toggles between table and graph", async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "foo", count: 1 }] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistToggle" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            fireEvent.click(screen.getByText(/View as table/));
            expect(screen.getByText("foo")).toBeInTheDocument();
            fireEvent.click(screen.getByText(/View as graph/));
            expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
        });

        test("possibleArtists object with imageUrl and string without imageUrl render correctly and object click triggers fetchAndShowSongPicker", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ name: "ObjArtist", imageUrl: "url" }, "StrArtist"]
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["SongA"]
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "Artist1" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByText("ObjArtist")).toBeInTheDocument());
            expect(screen.getByAltText("Image of ObjArtist"))
                .toHaveAttribute("src", "url");
            expect(screen.getByText("StrArtist")).toBeInTheDocument();
            fireEvent.click(screen.getByText("ObjArtist"));
            await waitFor(() =>
                expect(global.fetch).toHaveBeenLastCalledWith(
                    "/api/wordcloud/addSong",
                    expect.objectContaining({
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ artistName: "ObjArtist" })
                    })
                )
            );
        });

        // test("number provided ambiguous artist list object click triggers generate fetch with original artistName", async () => {
        //     global.fetch
        //         .mockResolvedValueOnce({
        //             ok: false,
        //             status: 400,
        //             headers: { get: () => "Found many artists - ambiguous" }
        //         })
        //         .mockResolvedValueOnce({
        //             ok: true,
        //             json: async () => [{ name: "ObjArtistNum", imageUrl: "urlNum" }, "StrArtistNum"]
        //         })
        //         .mockResolvedValueOnce({
        //             ok: true,
        //             json: async () => [{ word: "w", count: 1 }]
        //         });
        //
        //     render(<Search />);
        //
        //     fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
        //         target: { value: "ArtistNum" }
        //     });
        //     fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
        //         target: { value: "2" }
        //     });
        //     fireEvent.click(screen.getByText(/^Submit$/));
        //
        //     await waitFor(() => expect(screen.getByText("ObjArtistNum")).toBeInTheDocument());
        //
        //     fireEvent.click(screen.getByText("ObjArtistNum"));
        //
        //     await waitFor(() => {
        //         expect(global.fetch).toHaveBeenNthCalledWith(
        //             3,
        //             "/api/wordcloud/generate",
        //             expect.objectContaining({
        //                 method: "POST",
        //                 headers: { "Content-Type": "application/json" },
        //                 body: JSON.stringify({ artistName: "ArtistNum", songCount: 2 })
        //             })
        //         );
        //     });
        // });

        test("handleAddToFavorites error branch throws fallback error when resultMessage is falsy", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "w", count: 1 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistFav" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "FavSong", count: 1 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "w" });
            });
            await waitFor(() => expect(screen.getByText("FavSong")).toBeInTheDocument());
            const cell = screen.getByText("FavSong");
            cell.getBoundingClientRect = () => ({ left: 0, top: 0, width: 100, height: 40, right: 100, bottom: 40 });
            fireEvent.mouseMove(cell, { clientX: 50, clientY: 20 });
            act(() => jest.advanceTimersByTime(400));
            const favModal = screen.getAllByTestId("modal").find(m => m.textContent.includes('Add "FavSong" to favorites?'));
            global.fetch.mockResolvedValueOnce({
                ok: false,
                text: async () => ""
            });
            const yesButton = within(favModal).getByText("Yes");
            await act(async () => { fireEvent.click(yesButton); });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to add song to favorites.");
            });
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("handleAddToFavorites catch branch uses unknown error fallback when err.message is empty", async () => {
            jest.useFakeTimers();
            localStorage.setItem("username", "testUser");
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "x", count: 1 }]
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistReject" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "1" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId("word-cloud")).toBeInTheDocument());
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ song: "SongReject", count: 1 }]
            });
            act(() => {
                capturedWordCloudProps.onWordClick({}, { text: "x" });
            });
            await waitFor(() => expect(screen.getByText("SongReject")).toBeInTheDocument());
            const cell = screen.getByText("SongReject");
            cell.getBoundingClientRect = () => ({ left: 0, top: 0, width: 100, height: 40, right: 100, bottom: 40 });
            fireEvent.mouseMove(cell, { clientX: 50, clientY: 20 });
            act(() => jest.advanceTimersByTime(400));
            const favModal = screen.getAllByTestId("modal").find(m => m.textContent.includes('Add "SongReject" to favorites?'));
            global.fetch.mockRejectedValueOnce({ message: "" });
            const yesButton2 = within(favModal).getByText("Yes");
            await act(async () => { fireEvent.click(yesButton2); });
            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("An unknown error occurred while adding to favorites.");
            });
            localStorage.removeItem("username");
            jest.useRealTimers();
        });

        test("handleWordCloudSubmit HTTP error fallback shows correct toast", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 404,
                headers: { get: () => null }
            });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "TestArtist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "2" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("HTTP error: 404")
            );
        });

        test("handleWordCloudSubmit ambiguous generate branch artistRes failure shows error fetching possible artists toast", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => "Found many artists" }
                })
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => null }
                });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "ArtistNum" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "5" } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Error fetching possible artists.")
            );
        });

        test("handleWordCloudSubmit catch branch fallback 'An unknown error occurred.' when fetch rejects", async () => {
            global.fetch.mockRejectedValueOnce({});
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ErrArtistFallback" }
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "1" }
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("An unknown error occurred.")
            );
        });


        test("handleWordCloudSubmit without number triggers fetchAndShowSongPicker and shows header error toast when addSong fetch fails", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ["SoloArtist"] }) // artists fetch
                .mockResolvedValueOnce({ ok: false, headers: { get: () => "Custom fetch error" } }); // addSong fetch

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "SoloArtist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Custom fetch error");
            });
        });

        test("handleWordCloudSubmit without number triggers fetchAndShowSongPicker and shows fallback error toast when addSong fetch fails with no header", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ["SoloArtist"] }) // artists fetch
                .mockResolvedValueOnce({ ok: false, headers: { get: () => null } }); // addSong fetch

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: "SoloArtist" } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: "" } });
            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.");
            });
        });
        /// from here is the code error
        test("handleWordClick logs warning and returns early if word is undefined", async () => {
            const warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [
                    { word: "hello", count: 1 },
                ],
            });

            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/i), {
                target: { value: "Test Artist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/i), {
                target: { value: "1" },
            });

            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            await act(async () => {
                capturedWordCloudProps.onWordClick(undefined, undefined);
            });

            expect(warnSpy).toHaveBeenCalledWith("Invalid word clicked:", undefined);
            warnSpy.mockRestore();
        });




        test("handleGenerateFromFavorites sets weights and hides tables/options on success", async () => {
            localStorage.setItem("username", "testUser");

            const mockData = [{ word: "hello", count: 1 }];
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });

            render(<Search />);
            const button = screen.getByText(/Generate word cloud based on favorites/i);

            await act(async () => {
                fireEvent.click(button);
            });

            await waitFor(() => {
                expect(capturedWordCloudProps).not.toBeNull();
                expect(capturedWordCloudProps.data).toEqual([
                    { text: "hello", value: 300 }, // account for normalization
                ]);
            });

            localStorage.removeItem("username");
        });

        // test("handleUpdateExistingWordCloud sets weights and success toast on success", async () => {
        //     localStorage.setItem("username", "testUser");
        //
        //     const mockUpdatedData = [{ word: "world", count: 2 }];
        //     global.fetch.mockResolvedValueOnce({
        //         ok: true,
        //         json: async () => mockUpdatedData,
        //     });
        //
        //     render(<Search />);
        //     const updateButton = screen.getByText(/Update existing word cloud/i);
        //
        //     await act(async () => {
        //         fireEvent.click(updateButton);
        //     });
        //
        //     await waitFor(() => {
        //         expect(capturedWordCloudProps).not.toBeNull();
        //         expect(capturedWordCloudProps.data).toEqual([
        //             { text: "world", value: 300 }, // 2 * 300
        //         ]);
        //         expect(screen.getByTestId("toast")).toHaveTextContent("Word cloud updated!");
        //     });
        //
        //     localStorage.removeItem("username");
        // });


        test("onWordClick logs warning for word missing text property", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "test", count: 1 }],
            });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "BadWordClick" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "1" },
            });

            await act(async () => {
                fireEvent.click(screen.getByText(/^Submit$/));
            });

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            const warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});

            await act(async () => {
                capturedWordCloudProps.onWordClick({}, {});
            });

            expect(warnSpy).toHaveBeenCalledWith("Invalid word clicked:", {});
            warnSpy.mockRestore();
        });

        test('fetchAndShowSongPicker shows header error when songListRes.ok is false', async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => ['ArtistA'] });
            global.fetch.mockResolvedValueOnce({ ok: false, headers: { get: () => 'Header error' } });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: 'ArtistA' } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: '' } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId('toast')).toHaveTextContent('Header error'));
        });

        test('fetchAndShowSongPicker shows fallback error when header is null', async () => {
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => ['ArtistB'] });
            global.fetch.mockResolvedValueOnce({ ok: false, headers: { get: () => null } });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: 'ArtistB' } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: '' } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => expect(screen.getByTestId('toast')).toHaveTextContent('Failed to fetch songs.'));
        });


        test('ambiguous artist click: addSong returns header error → shows that header', async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ['A1','A2'] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: 'X' } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: '' } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByText('Did you mean one of these artists?'));

            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => 'Header‐Level Error' },
            });

            fireEvent.click(screen.getByText('A1'));
            await waitFor(() =>
                expect(screen.getByTestId('toast')).toHaveTextContent('Header‐Level Error')
            );
        });

        test('ambiguous artist click: addSong returns ok=false with no header → fallback message', async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ['B1','B2'] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: 'Y' } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: '' } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByText('Did you mean one of these artists?'));

            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null },
            });

            fireEvent.click(screen.getByText('B1'));
            await waitFor(() =>
                expect(screen.getByTestId('toast')).toHaveTextContent('Failed to fetch songs.')
            );
        });


        test('ambiguous artist click: addSong rejects with empty error → shows generic fetch error', async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ['Z1','Z2'] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: 'Z' },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: '' },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByText('Did you mean one of these artists?'));

            // 2) simulate addSong rejection with no .message
            global.fetch.mockRejectedValueOnce({});

            fireEvent.click(screen.getByText('Z1'));
            await waitFor(() =>
                expect(screen.getByTestId('toast'))
                    .toHaveTextContent('An error occurred while fetching songs.')
            );
        });

        test('ambiguous artist click + addSong rejects with empty message → shows fetch‐songs fallback', async () => {

            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ['X1','X2'] });
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), { target: { value: 'X' } });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), { target: { value: '' } });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByText('Did you mean one of these artists?'));

            global.fetch.mockRejectedValueOnce({});

            fireEvent.click(screen.getByText('X1'));
            await waitFor(() =>
                expect(screen.getByTestId('toast'))
                    .toHaveTextContent('An error occurred while fetching songs.')
            );
        });

        test('handleGenerateFromFavorites shows fallback HTTP <status> message when X-Error-Message header missing', async () => {
            localStorage.setItem('username', 'testUser');
            global.fetch.mockResolvedValueOnce({
                ok: false,
                status: 503,
                headers: { get: () => null },
            });
            render(<Search />);
            fireEvent.click(screen.getByText(/Generate word cloud based on favorites/i));
            await waitFor(() =>
                expect(screen.getByTestId('toast')).toHaveTextContent('HTTP 503')
            );
            localStorage.removeItem('username');
        });

        test("shows error toast for invalid artist name format", async () => {
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "!@#$" },  // invalid format
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Invalid name format")
            );
        });

        test("shows error toast for invalid song count format", async () => {
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "ValidArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "-5" }, // Invalid format
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Invalid song count")
            );
        });

        test("mergedMap accumulates duplicate words correctly", () => {
            const weights = [{ text: "hello", value: 3 }, { text: "hello", value: 2 }];
            const newWordMap = new Map([["hello", 5]]);

            const mergedMap = new Map();
            weights.forEach(({ text, value }) => {
                mergedMap.set(text, (mergedMap.get(text) || 0) + value);
            });
            newWordMap.forEach((count, word) => {
                mergedMap.set(word, (mergedMap.get(word) || 0) + count);
            });

            expect(mergedMap.get("hello")).toBe(10); // 3 + 2 + 5
        });

        test("submitSelectedSongs merges existing and new word counts correctly", async () => {

            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => ["MergeArtist"] }) // artist disambiguation
                .mockResolvedValueOnce({ ok: true, json: async () => ["Song1"] }); // song list

            render(<Search />);


            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "MergeArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getAllByText(/^Submit$/)[0]); // FIRST Submit = search submit


            await waitFor(() => screen.getByText("Song1"));


            const songBlock = screen.getByText("Song1").closest("div");
            const addButton = within(songBlock).getByRole("button", { name: /Add/i });
            fireEvent.click(addButton);

            // Inject existing word cloud (beta and gamma)
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [
                    { word: "beta", count: 5 },
                    { word: "gamma", count: 4 },
                ],
            });

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "InitArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getAllByText(/^Submit$/)[0]); // FIRST Submit = search submit

            await waitFor(() => screen.getByTestId("word-cloud"));


            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [
                    { word: "alpha", count: 3 },
                    { word: "beta", count: 2 },
                ],
            });


            const pickerSubmitBtn = screen.getAllByText(/^Submit$/)[1]; // SECOND Submit = song picker
            await act(async () => {
                fireEvent.click(pickerSubmitBtn);
            });

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Word cloud updated with selected songs!")
            );


            expect(capturedWordCloudProps.data).toEqual(
                expect.arrayContaining([
                    expect.objectContaining({ text: "alpha", value: 100 }),
                    expect.objectContaining({ text: "beta", value: 500 }),
                    expect.objectContaining({ text: "gamma", value: 200 }),
                ])
            );

        });


        test("ambiguous artist click with number provided calls handleWordCloudSubmit", async () => {

            global.fetch
                .mockResolvedValueOnce({
                    ok: false,
                    status: 400,
                    headers: { get: () => "Found many artists - ambiguous" },
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["ArtistOption1", "ArtistOption2"],
                })

                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: "resolved", count: 1 }],
                });

            render(<Search />);


            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "AmbiguousWithNumber" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));


            await waitFor(() => {
                expect(screen.getByText("ArtistOption1")).toBeInTheDocument();
            });

            fireEvent.click(screen.getByText("ArtistOption1"));


            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });


            expect(global.fetch).toHaveBeenLastCalledWith(
                "/api/wordcloud/generate",
                expect.objectContaining({
                    method: "POST",
                    body: expect.stringContaining("AmbiguousWithNumber")
                })
            );
        });



        test("handleUpdateExistingWordCloudFavorites fallback error when X-Error-Message is null", async () => {
            localStorage.setItem("username", "testUser");

            const { getByTestId, getAllByText, getAllByPlaceholderText } = render(<Search />);

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "hello", count: 1 }],
            });

            fireEvent.change(getAllByPlaceholderText("Type artist")[0], {
                target: { value: "FallbackArtist" },
            });
            fireEvent.change(getAllByPlaceholderText("Optional: number of songs")[0], {
                target: { value: "1" },
            });
            fireEvent.click(getAllByText(/^Submit$/)[0]);

            await waitFor(() => getByTestId("word-cloud"));


            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null },
            });

            fireEvent.click(getAllByText("Update existing word cloud with favorites")[0]);
            await waitFor(() =>
                expect(getByTestId("toast")).toHaveTextContent("Failed to update existing word cloud.")
            );
        });

        test("handleUpdateExistingWordCloudFavorites catch fallback when error message is empty", async () => {
            localStorage.setItem("username", "testUser");

            const { getByTestId, getAllByText, getAllByPlaceholderText } = render(<Search />);

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "hello", count: 1 }],
            });

            fireEvent.change(getAllByPlaceholderText("Type artist")[0], {
                target: { value: "CatchArtist" },
            });
            fireEvent.change(getAllByPlaceholderText("Optional: number of songs")[0], {
                target: { value: "1" },
            });
            fireEvent.click(getAllByText(/^Submit$/)[0]);

            await waitFor(() => getByTestId("word-cloud"));

            global.fetch.mockRejectedValueOnce({ message: "" });

            fireEvent.click(getAllByText("Update existing word cloud with favorites")[0]);

            await waitFor(() =>
                expect(getByTestId("toast")).toHaveTextContent(
                    "An unknown error occurred while updating word cloud."
                )
            );
        });

        test("handleUpdateExistingWordCloudFavorites shows toast if word cloud is empty", async () => {
            localStorage.setItem("username", "testUser");

            const { getByTestId, getByText } = render(<Search />);


            fireEvent.click(getByText("Update existing word cloud with favorites"));

            await waitFor(() =>
                expect(getByTestId("toast")).toHaveTextContent("Can't update empty word cloud.")
            );

            localStorage.removeItem("username");
        });

        test("handleUpdateExistingWordCloudFavorites shows toast if username is missing", async () => {
            // 1) Make sure getItem really returns null
            jest
                .spyOn(Storage.prototype, "getItem")
                .mockImplementation((key) => key === "username" ? null : null);

            const { getByText, getByTestId } = render(<Search />);

            // 2) Click the Favorites button right away
            fireEvent.click(getByText("Update existing word cloud with favorites"));

            // 3) Assert the missing-username toast shows
            await waitFor(() =>
                expect(getByTestId("toast")).toHaveTextContent("Username is missing.")
            );
        });


        test("handleUpdateExistingWordCloudFavorites sets weights and success toast on success", async () => {
            localStorage.setItem("username", "testUser");

            const mockUpdatedData = [{ word: "world", count: 2 }];
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: "init", count: 1 }],
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => mockUpdatedData,
                });

            const { getByText, getByTestId, getByPlaceholderText } = render(<Search />);

            fireEvent.change(getByPlaceholderText("Type artist"), {
                target: { value: "ArtistSuccess" },
            });
            fireEvent.change(getByPlaceholderText("Optional: number of songs"), {
                target: { value: "1" },
            });
            fireEvent.click(getByText(/^Submit$/));

            await waitFor(() => expect(getByTestId("word-cloud")).toBeInTheDocument());

            fireEvent.click(getByText("Update existing word cloud with favorites"));

            await waitFor(() => {
                expect(getByTestId("toast")).toHaveTextContent("Word cloud updated!");
                expect(capturedWordCloudProps.data).toEqual([
                    { text: "world", value: 300 },
                ]);
            });

            localStorage.removeItem("username");
        });

        test("handleUpdateExistingWordCloudSearch trims whitespace from artistName", async () => {
            const mockData = [{ word: "clean", count: 1 }];


            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });

            render(<Search />);


            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "CleanArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional: number of songs/), {
                target: { value: "1" },
            });


            fireEvent.click(screen.getByText(/^Submit$/));

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "   CleanArtist   " },
            });

            fireEvent.click(screen.getByText("Update existing word cloud with favorites"));

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });
        });


        test("shows error toast when artistName is empty", async () => {
            render(<Search />);


            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "init", count: 1 }],
            });

            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "RealArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            // Step 2: Now test empty artistName with valid weights
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "   " },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Please enter an artist name.")
            );
        });


        test("shows error toast when weights are empty", async () => {
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Adele" },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with favorites"));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Can't update empty word cloud.")
            );
        });

        // here
        test("shows error when updating empty word cloud", async () => {
            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Any Artist" },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with Search"));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Can't update an empty word cloud.")
            );
        });

        test("shows error when artistRes fails", async () => {

            const mockWord = { word: "hello", count: 3 };
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [mockWord],
            });

            render(<Search />);


            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "InitialArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText("Submit"));
            await waitFor(() => screen.getByTestId("word-cloud"));


            global.fetch.mockResolvedValueOnce({
                ok: false,
                json: async () => ({}),
                headers: { get: () => "Error fetching possible artists." },
            });


            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Error fetching possible artists.")
            );
        });


        test("shows artist options when multiple results found", async () => {
            global.fetch
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: "init", count: 1 }],
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ["A1", "A2"],
                });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "TestArtist" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByTestId("word-cloud"));
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with Search"));
            await waitFor(() =>
                expect(screen.getByText("Did you mean one of these artists?")).toBeInTheDocument()
            );
        });

        test("resolves artistName from string artistList[0]", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: "init", count: 1 }] })
                .mockResolvedValueOnce({ ok: true, json: async () => ["Taylor Swift"] })
                .mockResolvedValueOnce({ ok: false, headers: { get: () => "Failed to fetch songs." } });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Taylor" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByTestId("word-cloud"));
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with Search"));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.")
            );
        });

        test("resolves artistName from object artistList[0]", async () => {
            global.fetch
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: "init", count: 1 }] })
                .mockResolvedValueOnce({ ok: true, json: async () => [{ name: "Lorde" }] })
                .mockResolvedValueOnce({ ok: false, headers: { get: () => "Failed to fetch songs." } });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Lorde" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText(/^Submit$/));
            await waitFor(() => screen.getByTestId("word-cloud"));
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });
            fireEvent.click(screen.getByText("Update existing word cloud with Search"));
            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.")
            );
        });


        test("handles failed song list fetch with custom error", async () => {
            const errorMsg = "Custom song fetch failure";
            global.fetch.mockResolvedValueOnce({ ok: true, json: async () => [{ word: "hi", count: 2 }] }); // initial weights
            render(<Search />);

            // Setup weights
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Adele" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText("Submit"));
            await waitFor(() => screen.getByTestId("word-cloud"));

            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });


            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ["Adele"],
            });


            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => errorMsg },
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent(errorMsg)
            );
        });

        test("shows error when word cloud generation fetch fails", async () => {
            global.fetch.mockResolvedValueOnce({ // initial weights
                ok: true,
                json: async () => [{ word: "yo", count: 5 }],
            });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Drake" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "2" },
            });
            fireEvent.click(screen.getByText("Submit"));
            await waitFor(() => screen.getByTestId("word-cloud"));

            // Mock res.ok === false for generation
            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => "Generation error" },
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Generation error")
            );
        });

        test("successfully updates word cloud with merged words", async () => {

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "love", count: 2 }],
            });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Eminem" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "3" },
            });
            fireEvent.click(screen.getByText("Submit"));
            await waitFor(() => screen.getByTestId("word-cloud"));

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "rap", count: 3 }],
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Word cloud updated with search song count!")
            );
        });

        test("shows fallback error when fetch throws unexpected error", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "hi", count: 1 }],
            });

            render(<Search />);
            fireEvent.change(screen.getByPlaceholderText(/Type artist/), {
                target: { value: "Kanye" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "5" },
            });
            fireEvent.click(screen.getByText("Submit"));
            await waitFor(() => screen.getByTestId("word-cloud"));

            global.fetch.mockImplementationOnce(() => {
                throw new Error("Unexpected fetch error");
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent("Unexpected fetch error")
            );
        });

        test("sets all songs and shows picker when only one artist is found and no song count is given", async () => {
            const mockWeights = [{ word: "dream", count: 3 }];
            const mockSongs = Array.from({ length: 20 }, (_, i) => `song${i + 1}`);

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockWeights,
            });

            render(<Search />);

            await act(async () => {

                const updateBtn = screen.getByText("Generate word cloud based on favorites");
                fireEvent.click(updateBtn);
            });

            await waitFor(() => {
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument();
            });

            fireEvent.change(screen.getByPlaceholderText(/Type artist/i), {
                target: { value: "Taylor Swift" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ name: "Taylor Swift" }],
            });

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockSongs,
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() => {
                expect(screen.getByText("song1")).toBeInTheDocument();
                expect(screen.getByText("song15")).toBeInTheDocument();
            });
        });

        test("shows fallback error message when X-Error-Message header is missing", async () => {
            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: "dream", count: 3 }],
            });

            render(<Search />);

            fireEvent.change(screen.getByPlaceholderText(/Type artist/i), {
                target: { value: "InitialSearch" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "1" },
            });
            fireEvent.click(screen.getByText("Submit"));

            await waitFor(() =>
                expect(screen.getByTestId("word-cloud")).toBeInTheDocument()
            );

            global.fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => [{ name: "Adele" }],
            });

            global.fetch.mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null }, // No X-Error-Message
            });
            fireEvent.change(screen.getByPlaceholderText(/Type artist/i), {
                target: { value: "Adele" },
            });
            fireEvent.change(screen.getByPlaceholderText(/Optional/), {
                target: { value: "" },
            });

            fireEvent.click(screen.getByText("Update existing word cloud with Search"));

            await waitFor(() => {
                expect(screen.getByTestId("toast")).toHaveTextContent("Failed to fetch songs.");
            });
        });

        // --- Suggestions: onKeyDown Enter & Space ---
        test('pressing Enter on a suggested artist calls handleSelect and hides suggestions', async () => {
            // 1) mock artists search then song list
            global.fetch = jest.fn()
                .mockResolvedValueOnce({ ok: true, json: async () => ['A', 'B'] })
                .mockResolvedValueOnce({ ok: true, json: async () => [] });
            render(<Search />);

            // 2) type & submit to trigger suggestions
            fireEvent.change(screen.getByLabelText(/Artist name/i), { target: { value: 'A' } });
            fireEvent.click(screen.getByRole('button', { name: /Submit search/i }));

            // 3) wait for the suggestion button
            const btn = await screen.findByRole('button', { name: /Select artist A/i });
            fireEvent.keyDown(btn, { key: 'Enter', code: 'Enter', charCode: 13 });
            expect(screen.queryByRole('button', { name: /Select artist A/i })).toBeNull();
        });


        test('shows "Failed to update existing word cloud." when update-favorites API returns not ok', async () => {
            // 1) seed weights by generating from favorites
            localStorage.setItem('username', 'me');
            global.fetch = jest.fn().mockResolvedValueOnce({
                ok: true,
                json: async () => [{ word: 'foo', count: 2 }],
            });
            render(<Search />);
            fireEvent.click(screen.getByRole('button', { name: /Generate word cloud based on favorites/i }));
            await screen.findByTestId('word-cloud');

            // 2) mock update API failure
            global.fetch = jest.fn().mockResolvedValueOnce({
                ok: false,
                headers: { get: () => null },
                text: async () => '',
            });
            fireEvent.click(screen.getByRole('button', { name: /Update existing word cloud with favorites/i }));
            const toast = await screen.findByTestId('toast');
            expect(toast).toHaveTextContent('Failed to update existing word cloud.');
        });

        test('shows "Please enter an artist name." when updating search with empty artistName', async () => {
            localStorage.setItem('username', 'user');
            render(<Search />);
            fireEvent.click(screen.getByRole('button', { name: /Update existing word cloud with Search/i }));
            const toast = await screen.findByTestId('toast');
            expect(toast).toHaveTextContent('Please enter an artist name.');
        });


        test('shows custom header error when update-search API fails with X-Error-Message', async () => {
            // 1) seed weights
            localStorage.setItem('username', 'u');
            global.fetch = jest.fn()
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: 'w', count: 1 }] })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => 'Custom search error' },
                    json: async () => [],
                });
            render(<Search />);
            fireEvent.click(screen.getByRole('button', { name: /Generate word cloud based on favorites/i }));
            await screen.findByTestId('word-cloud');

            // 2) set inputs
            fireEvent.change(screen.getByLabelText(/Artist name/i), { target: { value: 'ArtistX' } });
            fireEvent.change(screen.getByLabelText(/Number of songs/i), { target: { value: '3' } });
            fireEvent.click(screen.getByRole('button', { name: /Update existing word cloud with Search/i }));

            const toast = await screen.findByTestId('toast');
            expect(toast).toHaveTextContent('Custom search error');
        });

        test('shows "Failed to update word cloud." when update-search API fails without X-Error-Message', async () => {
            localStorage.setItem('username', 'u');
            global.fetch = jest.fn()
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: 'w', count: 1 }] })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => null },
                    json: async () => [],
                });
            render(<Search />);
            fireEvent.click(screen.getByRole('button', { name: /Generate word cloud based on favorites/i }));
            await screen.findByTestId('word-cloud');

            fireEvent.change(screen.getByLabelText(/Artist name/i), { target: { value: 'ArtistX' } });
            fireEvent.change(screen.getByLabelText(/Number of songs/i), { target: { value: '3' } });
            fireEvent.click(screen.getByRole('button', { name: /Update existing word cloud with Search/i }));

            const toast = await screen.findByTestId('toast');
            expect(toast).toHaveTextContent('Failed to update word cloud.');
        });

        test('shows fallback "Error updating word cloud from search." when update-search throws', async () => {
            localStorage.setItem('username', 'u');
            global.fetch = jest.fn()
                .mockResolvedValueOnce({ ok: true, json: async () => [{ word: 'w', count: 1 }] })
                .mockImplementationOnce(() => { throw new Error(); });
            render(<Search />);
            fireEvent.click(screen.getByRole('button', { name: /Generate word cloud based on favorites/i }));
            await screen.findByTestId('word-cloud');

            fireEvent.change(screen.getByLabelText(/Artist name/i), { target: { value: 'ArtistX' } });
            fireEvent.change(screen.getByLabelText(/Number of songs/i), { target: { value: '3' } });
            fireEvent.click(screen.getByRole('button', { name: /Update existing word cloud with Search/i }));

            const toast = await screen.findByTestId('toast');
            expect(toast).toHaveTextContent('Error updating word cloud from search.');
        });

        // covers the <td onKeyDown …> handler for both Enter and Space
        test.each([
            { key: 'Enter', code: 'Enter', charCode: 13 },
            { key: ' ',     code: 'Space', charCode: 32 },
        ])('pressing %s on a song row opens the song-details modal', async ({ key, code, charCode }) => {
            localStorage.setItem('username', 'user');

            // queue fetch responses in the exact order the component will call them
            global.fetch = jest
                // 1) /api/favorites/generate  → populate word-cloud with the word “foo”
                .fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: 'foo', count: 1 }],
                })
                // 2) /api/wordcloud/songFrequencyForWordInLyrics(NoNumber) → one song “SongA”
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ song: 'SongA', count: 3 }],
                })
                // 3) /api/wordcloud/songDetails → details for SongA
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => ({
                        title: 'SongA',
                        artist: 'ArtistA',
                        lyrics: 'la la',
                        year: 2021,
                    }),
                });

            render(<Search />);

            // trigger the first call so weights are created and the WordCloud is rendered
            fireEvent.click(
                screen.getByRole('button', { name: /Generate word cloud based on favorites/i })
            );
            await screen.findByTestId('word-cloud');                // WordCloud wrapper is in the DOM
            await waitFor(() => expect(capturedWordCloudProps).not.toBeNull());

            // simulate clicking the word “foo” in the cloud to load the song-frequency table
            act(() => capturedWordCloudProps.onWordClick({}, { text: 'foo', value: 1 }));

            // wait for the song row (role="button") to appear in the frequencies modal
            const songCell = await screen.findByRole('button', { name: /Details for SongA/i });

            // fire the desired key on that <td>
            fireEvent.keyDown(songCell, { key, code, charCode });

            expect(await screen.findByText('SongA')).toBeInTheDocument();
        });



    });

    describe("Search Component – extra coverage", () => {
        // clean up spies between each test
        afterEach(() => jest.restoreAllMocks());


        test("favorites-update shows fallback error toast when no X-Error-Message header", async () => {
            // simulate logged-in user
            jest
                .spyOn(Storage.prototype, "getItem")
                .mockReturnValue("testuser");

            // 1) generate-from-favorites, 2) update-favorites error
            jest.spyOn(global, "fetch")
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: "foo", count: 2 }],
                })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => null },
                });

            render(<Search />);

            // click Generate (populates weights + <WordCloud data-testid="word-cloud" />)
            fireEvent.click(
                screen.getByRole("button", {
                    name: /Generate word cloud based on favorites/i,
                })
            );
            await screen.findByTestId("word-cloud");

            // click the update-from-favorites button
            fireEvent.click(
                screen.getByRole("button", {
                    name: /Update existing word cloud with favorites/i,
                })
            );

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent(
                    "Failed to update existing word cloud."
                )
            );
        });

        test("search-update shows header-propagated error toast when X-Error-Message present", async () => {
            jest
                .spyOn(Storage.prototype, "getItem")
                .mockReturnValue("testuser");

            // 1) generate-from-favorites, 2) update-search error with custom header
            jest.spyOn(global, "fetch")
                .mockResolvedValueOnce({
                    ok: true,
                    json: async () => [{ word: "foo", count: 2 }],
                })
                .mockResolvedValueOnce({
                    ok: false,
                    headers: { get: () => "Please enter an artist name" },
                });

            render(<Search />);

            fireEvent.click(
                screen.getByRole("button", {
                    name: /Generate word cloud based on favorites/i,
                })
            );
            await screen.findByTestId("word-cloud");

            fireEvent.click(
                screen.getByRole("button", {
                    name: /Update existing word cloud with Search/i,
                })
            );

            await waitFor(() =>
                expect(screen.getByTestId("toast")).toHaveTextContent(
                    "Please enter an artist name"
                )
            );
        });

        describe("submitSelectedSongs guard clauses", () => {
            beforeEach(() => {
                // suggestion → song-picker
                jest.spyOn(global, "fetch")
                    .mockResolvedValueOnce({
                        ok: true,
                        json: async () => [{ artistName: "O", imageUrl: "u" }],
                    })
                    .mockResolvedValueOnce({
                        ok: true,
                        json: async () => ["SongA", "SongB"],
                    });
            });

            test("shows missing-username toast if no user", async () => {
                jest
                    .spyOn(Storage.prototype, "getItem")
                    .mockReturnValue(null);

                render(<Search />);
                // open the picker
                fireEvent.change(
                    screen.getByRole("textbox", { name: /Artist name/i }),
                    { target: { value: "O" } }
                );
                fireEvent.click(
                    screen.getByRole("button", { name: /Submit search/i })
                );
                await screen.findByRole("region", { name: /Song picker/i });

                fireEvent.click(
                    screen.getByRole("button", { name: /Submit selected songs/i })
                );
                await waitFor(() =>
                    expect(screen.getByTestId("toast")).toHaveTextContent(
                        "Username is missing."
                    )
                );
            });

            test("shows error if artistName is blank", async () => {
                jest
                    .spyOn(Storage.prototype, "getItem")
                    .mockReturnValue("testuser");

                render(<Search />);
                fireEvent.click(
                    screen.getByRole("button", { name: /Submit search/i })
                );
                await waitFor(() =>
                    expect(screen.getByTestId("toast")).toHaveTextContent(
                        "Please enter an artist name."
                    )
                );
            });

            test("shows error if no songs have been selected", async () => {
                jest
                    .spyOn(Storage.prototype, "getItem")
                    .mockReturnValue("testuser");

                render(<Search />);
                fireEvent.change(
                    screen.getByRole("textbox", { name: /Artist name/i }),
                    { target: { value: "O" } }
                );
                fireEvent.click(
                    screen.getByRole("button", { name: /Submit search/i })
                );
                await screen.findByRole("region", { name: /Song picker/i });

                fireEvent.click(
                    screen.getByRole("button", { name: /Submit selected songs/i })
                );
                await waitFor(() =>
                    expect(screen.getByTestId("toast")).toHaveTextContent(
                        "Please select at least one song to add."
                    )
                );
            });
        });
    });





