Feature: Creating a new word cloud

  Scenario: Create a regular word cloud with valid inputs
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Taylor Swift" in the artist search field
    And I enter "7" in the song count field
    And I click the "Submit" button
    Then I should see a cloud with the top 100 words from the top 7 songs

  Scenario: Create new word cloud from favorites list
    Given I am logged in and have songs on my favorites list
    And I am on the word cloud homepage
    When I click the "Generate word cloud based on favorites" button
    Then I should see words from songs in the favorites list in the word cloud

  Scenario: Add favorites to existing word cloud
    Given I am logged in and have songs on my favorites list
    And there is an existing word cloud
    When I click the "Update existing word cloud with favorites" button
    Then I should see words from songs in the favorites list in the word cloud

#  Scenario: Create a word cloud with songs from multiple artists
#    Given I am on the word cloud homepage
#    When I enter "Selena Gomez" in the artist search field
#    And I enter "3" in the song count field
#    And I click the "Submit" button
#    Then I should see a word cloud from the top "3" songs of "Selena Gomez"
#    When I click the "Add Song" button
#    And I enter "Justin Bieber" in the artist search field
#    And I select the song "Sorry"
#    And I click the "Add Song" button
#    And I click the "Update Cloud" button
#    Then The cloud should update to have words from the song "Sorry"

  Scenario: Create a word cloud with songs from multiple artists
    Given I am logged in
    And I am on the word cloud homepage
    And I enter "Taylor Swift" in the artist search field
    And I enter "7" in the song count field
    And I click the "Submit" button
    And I should see a cloud with the top 100 words from the top 7 songs
    When I enter "Justin Bieber" in the artist search field
    And I enter "3" in the song count field
    And I click the "Update existing word cloud with Search" button
    Then the word cloud should update to have songs from multiple artists

  Scenario: Create a word cloud with songs from multiple artists - no number
    Given I am logged in
    And I am on the word cloud homepage
    And I enter "Taylor Swift" in the artist search field
    And I enter "7" in the song count field
    And I click the "Submit" button
    And I should see a cloud with the top 100 words from the top 7 songs
    When I enter "Justin Bieber" in the artist search field
    And I enter "" in the song count field
    And I click the "Update existing word cloud with Search" button
    And I should see a song selection dialogue
    And I select the song "Sorry"
    And I click the submit button in the dialogue
    Then the word cloud should update to have songs from multiple artists

  Scenario: Error message for an illegal artist name
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Taylor@Swfit!" in the artist search field
    And I enter "7" in the song count field
    And I click the "Submit" button
    Then I should see the error message "Invalid name format"

  Scenario: Error message for an invalid song count input
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Justin Bieber" in the artist search field
    And I enter "!" in the song count field
    And I click the "Submit" button
    Then I should see the error message "Invalid song count"

  Scenario: Error message for no artist name
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "7" in the song count field
    And I click the "Submit" button
    Then I should see the error message "Please enter an artist name"

  Scenario: Error message for no input
    Given I am logged in
    And I am on the word cloud homepage
    When I click the "Submit" button
    Then I should see the error message "Please enter an artist name"

  Scenario: Generate word cloud using default song count when no count is provided
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Tate McRae" in the artist search field
    And I enter "" in the song count field
    And I click the "Submit" button
    Then I should see a song selection dialogue

  Scenario: Click "Add Song" without selecting a song
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Tate McRae" in the artist search field
    And I enter "" in the song count field
    And I click the "Submit" button
    And I should see a song selection dialogue
    And I click the submit button in the dialogue
    Then I should see the error message "Please select at least one song to add"

  Scenario: Toggle word cloud display between graphical and table views
    Given I am logged in
    And there is an existing word cloud
    When I click the "View as table" button
    Then I should see a table listing words and their frequencies
    When I click the "View as graph" button
    Then I should see the graphical word cloud

  Scenario: Interact with a word in the word cloud to view song details
    Given I am logged in
    And there is an existing word cloud
    When I click on the word "baby" in the word cloud
    Then I should see a list of songs with the word "baby" and its frequency
    When I click on a song title "Cruel Summer" from the list
    Then I should see the lyrics of the song with the word "baby" highlighted
    And I should see the song details (title, artist, year)

  Scenario: Interact with a word in the table to view song details
    Given I am logged in
    And there is an existing word cloud
    When I click on the word "know" in the table
    Then I should see a list of songs with the word "know" and its frequency
    When I click on a song title "Cruel Summer" from the list
    Then I should see the lyrics of the song with the word "know" highlighted
    And I should see the song details (title, artist, year)

  Scenario: Hover over a song title and add the song to favorites
    Given I am logged in
    And there is an existing word cloud
    When I click on the word "baby" in the word cloud
    Then I should see a list of songs with the word "baby" and its frequency
    When I hover over the song title "Cruel Summer"
    Then a popup should appear with an option to add to favorites
    When I click the "Yes" button
    Then "Cruel Summer" should be added to my favorites list

  Scenario: Size of words maps to their frequency
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Justin Bieber" in the artist search field
    And I enter "3" in the song count field
    And I click the "Submit" button
    Then "hit" is displayed "smaller"
    And "sorry" is displayed "larger"

  Scenario: Add song to empty word cloud (favorites)
    Given I am logged in
    And I am on the word cloud homepage
    When I click the "Update existing word cloud with favorites" button
    Then I should see the error message "Can't update empty word cloud"

  Scenario: Add song to empty word cloud (search)
    Given I am logged in
    And I am on the word cloud homepage
    When I click the "Update existing word cloud with Search" button
    Then I should see the error message "Please enter an artist name"

  Scenario: Word cloud generation occurs within time limits
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Taylor Swift" in the artist search field
    And I enter "1" in the song count field
    And I click the "Submit" button
    Then the word cloud should be generated in less than 1 second

  Scenario: Word cloud has no filler words
    Given I am logged in
    And there is an existing word cloud
    When the lyrics contain filler words like "ooh"
    Then the word cloud should not contain "ooh"

  Scenario: Word cloud has no stemming
    Given I am logged in
    And there is an existing word cloud
    When the lyrics contains stem words like "killing"
    Then the word cloud should not contain "killing"

  Scenario: The word cloud displays a maximum of 100 words
    Given I am logged in
    And there is an existing word cloud
    And there are more than 100 words in the lyrics
    Then the word cloud should display no more than 100 words

  Scenario: Ambiguous artist selection
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "Justin" in the artist search field
    And I click the "Submit" button
    Then I should see a popup with all artists named "Justin" with their photos
    When I select "Justin Bieber"
    Then I should see a list of popular songs for "Justin Bieber"

  # DON'T NEED
#    Scenario: Display error when attempting to add a duplicate song
#    Given I have added the song "Sorry" by "Justin Bieber" to my cloud
#    When I try to add the song "Sorry" by "Justin Bieber" again
#    Then I should see the error "Song has already been added"

  Scenario: Unknown artist selection
    Given I am logged in
    And I am on the word cloud homepage
    When I enter "UnknownArtistXXX" in the artist search field
    And I enter "3" in the song count field
    And I click the "Submit" button
    Then I should see the error message "No matching artist found"

  # DON'T NEED
#    Scenario: Cancel out a request to create a word cloud
#    Given I am on the word cloud homepage
#    When I enter "Justin Bieber" in the artist search field
#    And I enter "5" in the song count field
#    And I click the "Cancel" button
#    Then I should not see a word cloud for "Justin Bieber"
