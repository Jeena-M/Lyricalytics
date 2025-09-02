Feature: Login
  Scenario: Initial Page is Login Page
    Given I am on the homepage
    Then I should see the title "Login"
  Scenario: See Website Name
    Given I am on the login homepage
    Then I should see the website name "Let's Get Lyrical"
  Scenario: Access Account Creation from Login
    Given I am on the login homepage
    And I click the "Register" button
    Then I should see the title "Create Account"
  Scenario: Successful Account Creation
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the success message "Account created successfully!"
  Scenario: Cancel Account Creation
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Cancel" button
    Then I should see the "Confirm Cancellation" popup
    And I click the "Yes" button
    Then I should see the error message "Registration cancelled"
  Scenario: Cancel Account Creation and Change Mind
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Cancel" button
    And I click the "No" button
    Then I should not see the "Confirm Cancellation" popup
    And I should see the the login homepage with "Jeena" and "Login123"
  Scenario: Passwords Don’t Match
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login1234" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Passwords do not match"
  Scenario: Taken Username
    Given I am on the register homepage
    When The username "Jeena" already exists with password "Login123"
    And I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Username already exists"
  Scenario: Invalid Password
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "login123" in the "password" field
    And I enter "login123" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Invalid password"
  Scenario: Didn’t Confirm Password
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Please fill both password fields"
  Scenario: Didn’t Enter Username
    Given I am on the register homepage
    And I click the "Register" button
    And I enter "" in the "username" field
    And I enter "Login123" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Username can't be empty"
  Scenario: Didn’t Enter Password
    Given I am on the register homepage
    When I click the "Register" button
    And I enter "Jeena" in the "username" field
    And I enter "" in the "password" field
    And I enter "Login123" in the "confirmPassword" field
    And I click the "Create" button
    Then I should see the error message "Please fill both password fields"
  Scenario: Successful Account Login
    Given I am on the login homepage
    And The username "Jeena" already exists with password "Login123"
    When I click the "Login" button
    And I enter "Jeena" in the "username" field
    And I enter "Login123" in the "password" field
    And I click the Login button to login
    Then I should see the success message "Welcome back"
  Scenario: Failed Account Login
    Given I am on the login homepage
    When The username "Jeena" already exists with password "Login123"
    And I click the "Login" button
    And I enter "Jeena" in the "username" field
    And I enter "Login1234" in the "password" field
    And I click the Login button to login
    Then I should see the error message "Username or password is incorrect"
  Scenario: Three Failed Account Login Attempts Under a Minute and Lock
    Given I am on the login homepage
    And I click the "Login" button
    And The username "JB" already exists with password "Login123"
    And I enter "JB" in the "username" field
    And I try "Log12" as the "password" field 3 times within 50 sec
    Then I should see the error message "Account locked due to multiple failed login attempts. Please try again in 30 seconds."
    And I wait for 30 seconds
    Then I should see the success message "Account unlocked. Try again"
  Scenario: Three Failed Account Login Attempts Over a Minute
    Given I am on the login homepage
    And I click the "Login" button
    And The username "Jeena" already exists with password "Login123"
    And I enter "Jeena" in the "username" field
    And I try "Log12" as the "password" field 3 times within 180 sec
    Then I should not see the error message "Account locked due to multiple failed login attempts. Please try again in 30 seconds."
