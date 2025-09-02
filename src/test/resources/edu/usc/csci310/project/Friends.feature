Feature: Friends Feature to compare users' favorite songs

  Scenario: Default ordering list is descending by frequency
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "user2" in the search friends field
    And I click the "Submit" button
    Then I should see list of songs high to low frequency

  Scenario: Display comparison list with frequency
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "user2" in the search friends field
    And I click the "Submit" button
    Then I should see a list of shared favorite songs

  Scenario: Toggle sorting order for list
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "user2" in the search friends field
    And I click the "Submit" button
    And I click the "Sort Ascending" button
    Then I should see list of songs low to high frequency
    When I click the "Sort Descending" button
    Then I should see list of songs high to low frequency

  Scenario: All songs are unique among friends
    Given None of the songs are shared between my friends' favorites lists
    And I am logged in as "user4" and on the friends comparison page
    When I enter "user5" in the search friends field
    And I click the "Submit" button
    Then I should see a list of songs each with a frequency of 1

  Scenario: Hovering over frequency number shows list of friends
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "user2" in the search friends field
    And I click the "Submit" button
    And I enter "user3" in the search friends field
    And I click the "Submit" button
    And I hover over the frequency number next to song1
    Then I see the usernames of friends who favorited that song

  Scenario: Clicking on a song title shows song details
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "user2" in the search friends field
    And I click the "Submit" button
    And I click on the song title "Sorry"
    Then I should see the song's artist and year of recording

  Scenario: Error message when adding a non-existent friend
    Given There are multiple users with public accounts and favorites
    And I am logged in and on the friends comparison page
    When I enter "Bob" in the search friends field
    And I click the "Submit" button
    Then I should see an error message "User does not exist"

  Scenario: Error message when adding a private friend
    Given There are multiple users with public accounts and favorites
    And User Joe exists but has a private account
    And I am logged in and on the friends comparison page
    When I enter "Joe" in the search friends field
    And I click the "Submit" button
    Then I should see an error message "User account is private"
