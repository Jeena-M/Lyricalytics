Feature: Favorites list
  Scenario: I can view my favorite songs
    Given I am on the website home page
    And I am logged in and have songs on my favorites list
    When I click on the "Favorites" button
    Then I can see my favorites list

  Scenario: I can delete all the songs from my list
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I click on the "Delete All Favorites" button
    And I click on the "Delete" button
    Then I should see no songs on my favorites list
    And I should see a message saying "All favorites deleted."

  Scenario: I can cancel deleting all songs
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I click on the "Delete All Favorites" button
    And I click on the "Cancel" button
    Then No songs should be deleted from my favorites list

  Scenario: I can delete one song from my favorite songs list
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I hover over the title of song 1
    And I click on the "Delete" button
    And I click on the "Delete" button
    Then I should see a message saying "Deleted \"Sorry\" by Justin Bieber"

  Scenario: I can cancel deleting one song from my favorite songs list
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I hover over the title of song 1
    And I click on the "Delete" button
    And I click on the "Cancel" button
    Then Song 1 should not be removed from my favorites list

  Scenario: Move a song up in the list
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I hover over the title of song 2
    And I click on the "Move Up" button
    Then Song 2 "Love Yourself" should be the first song on my favorites list

  Scenario: Move a song down in the list
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I hover over the title of song 1
    And I click on the "Move Down" button
    Then Song 1 "Sorry" should be the second song on my favorites list

  Scenario: Toggle from public to private account
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    And My account is public
    When I click on the "Account Private" button
    Then My account should be private

  Scenario: Toggle from private to public account
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I click on the "Account Public" button
    Then My account should be public

  Scenario: Account starts as private
    Given I just made a new account
    And I am on the favorites page
    Then My account should be private

  Scenario: Clicking on a song title shows song info
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I click on the title of song 1
    Then I should see the song's artist and year

  Scenario: Favorites list is persistent
    Given I am logged in and have songs on my favorites list
    And I am on the website home page
    When I click on the "Favorites" button
    And I click on the "Search" button
    And I click on the "Favorites" button
    Then I can see my favorites list

  Scenario: Ordering is persistent
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I hover over the title of song 2
    And I click on the "Move Up" button
    And I click on the "Search" button
    And I click on the "Favorites" button
    Then Song 2 "Love Yourself" should be the first song on my favorites list

  Scenario: Privacy setting is persistent
    Given I am logged in and have songs on my favorites list
    And I am on the favorites page
    When I click on the "Account Public" button
    And I click on the "Search" button
    And I click on the "Favorites" button
    Then My account should be public