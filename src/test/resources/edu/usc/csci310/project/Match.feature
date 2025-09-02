Feature: Finding lyrical soulmate or enemy
  Scenario: I don't find my lyrical soulmate
    Given I am the only user with a public account
    And I am logged in and on the match page
    When I click on the "Find Soulmate" button
    Then I should see a message "Error finding user: No soulmate found"

  Scenario: I successfully find my lyrical soulmate
    Given There are multiple users with public accounts
    And I am logged in and on the match page
    When I click on the "Find Soulmate" button
    Then I should see the username "user2"
    And I should see a list of their favorites
    And I should see a "positive" animation

  Scenario: I don't find my lyrical enemy
    Given I am the only user with a public account
    And I am logged in and on the match page
    When I click on the "Find Enemy" button
    Then I should see a message "Error finding user: No enemy found"

  Scenario: I successfully find my lyrical enemy
    Given There are multiple users with public accounts
    And I am logged in and on the match page
    When I click on the "Find Enemy" button
    Then I should see the username "user3"
    And I should see a list of their favorites
    And I should see a "negative" animation
