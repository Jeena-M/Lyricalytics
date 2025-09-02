package edu.usc.csci310.project;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FriendsStepDefs {
    private static final String ROOT_URL = "http://localhost:8080/";
    private static final String FRIENDS_URL = ROOT_URL + "friends";
    private static final String LOGIN_URL = ROOT_URL + "login";
    private WebDriver driver = WebDriverHelper.getDriver();

    @Given("There are multiple users with public accounts and favorites")
    public void thereAreMultipleUsersWithPublicAccountsAndFavorites() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
            Statement stmt = connection.createStatement();
            Statement stmt2 = connection.createStatement();
            String login = "INSERT INTO users (username, password, privacy) VALUES ('user2', 'Pass12', 0)";
            String login2 = "INSERT INTO users (username, password, privacy) VALUES ('user3', 'Pass12', 0)";
            stmt.executeUpdate(login);
            stmt.executeUpdate(login2);
            String insert1 = "INSERT INTO favorites (song, artist, username) VALUES ('Sorry', 'Justin Bieber', 'user2')";
            String insert2 = "INSERT INTO favorites (song, artist, username) VALUES ('august', 'Taylor Swift', 'user3')";
            String insert3 = "INSERT INTO favorites (song, artist, username) VALUES ('Love Yourself', 'Justin Bieber', 'user2')";
            String insert4 = "INSERT INTO favorites (song, artist, username) VALUES ('WILDFLOWER', 'Billie Eilish', 'user3')";
            stmt2.executeUpdate(insert1);
            stmt2.executeUpdate(insert2);
            stmt2.executeUpdate(insert3);
            stmt2.executeUpdate(insert4);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("User Joe exists but has a private account")
    public void userExistsPrivateAccount() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
            Statement stmt = connection.createStatement();
            String login = "INSERT INTO users (username, password, privacy) VALUES ('Joe', 'Pass12', 1)";
            stmt.executeUpdate(login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("None of the songs are shared between my friends' favorites lists")
    public void noneOfTheSongsAreSharedBetweenMyFriendsFavoritesLists() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
            Statement stmt = connection.createStatement();
            Statement stmt2 = connection.createStatement();
            String login = "INSERT INTO users (username, password, privacy) VALUES ('user4', 'Pass12', 0)";
            String login2 = "INSERT INTO users (username, password, privacy) VALUES ('user5', 'Pass12', 0)";
            stmt.executeUpdate(login);
            stmt.executeUpdate(login2);
            String insert1 = "INSERT INTO favorites (song, artist, username) VALUES ('BLUE', 'Billie Eilish', 'user4')";
            String insert2 = "INSERT INTO favorites (song, artist, username) VALUES ('cardigan', 'Taylor Swift', 'user5')";
            stmt2.executeUpdate(insert1);
            stmt2.executeUpdate(insert2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("I am logged in and on the friends comparison page")
    public void iAmLoggedInAndOnTheMatchPage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(LOGIN_URL);
        WebElement username2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        username2.sendKeys("user1");
        WebElement password2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        password2.sendKeys("Pass12");
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        loginButton.click();
        Thread.sleep(1000);
        driver.get(FRIENDS_URL);
    }

    @Given("I am logged in as {string} and on the friends comparison page")
    public void iAmLoggedInAndOnTheMatchPage(String user) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(LOGIN_URL);
        WebElement username2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        username2.sendKeys(user);
        WebElement password2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        password2.sendKeys("Pass12");
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        loginButton.click();
        Thread.sleep(1000);
        driver.get(FRIENDS_URL);
    }

    @When("I enter {string} in the search friends field")
    public void iEnterUser2InTheSearchFriendsField(String user) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/form/input")));
        searchField.sendKeys(user);
    }

    @When("I click on the song title {string}")
    public void iClickOnTheTitleOfSong1(String song) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement songTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[1]/td[1]")));
        songTitle.click();
    }

    @When("I hover over the frequency number next to song1")
    public void iHoverOverTheTitleOfSong2() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement freq = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[1]/td[2]")));
        Actions actions = new Actions(driver);
        actions.moveToElement(freq).perform();
    }

    @Then("I should see a list of songs each with a frequency of 1")
    public void iShouldSeeAListOfSongsEachWithAFrequencyOf1() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement freq = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[1]/td[2]")));
        assertTrue(freq.getText().contains("1"));
    }

    @Then("I should see the song's artist and year of recording")
    public void iShouldSeeTheSongDetailsTitleArtistYear() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Details"));
        assertTrue(driver.getPageSource().contains("Details"));
        assertTrue(driver.getPageSource().contains("Year:"));
        assertTrue(driver.getPageSource().contains("Artist:"));
    }

    @Then("I should see list of songs low to high frequency")
    public void iShouldSeeListOfSongsLowToHighFrequency() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement freq1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[1]/td[2]")));
        WebElement freq2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[2]/td[2]")));
        int f1 = Integer.parseInt(freq1.getText());
        int f2 = Integer.parseInt(freq2.getText());
        assertTrue(f1 <= f2);
    }

    @Then("I should see list of songs high to low frequency")
    public void iShouldSeeListOfSongsHighToLowFrequency() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement freq1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[1]/td[2]")));
        WebElement freq2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/div/table/tbody/tr[2]/td[2]")));
        //assertTrue(freq.getText().contains("1"));
        int f1 = Integer.parseInt(freq1.getText());
        int f2 = Integer.parseInt(freq2.getText());
        assertTrue(f1 >= f2);
    }

    @Then("I should see a list of shared favorite songs")
    public void iShouldSeeAListOfSharedSongs() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Sorry"));
        assertTrue(driver.getPageSource().contains("Sorry"));
    }

    @Then("I see the usernames of friends who favorited that song")
    public void iSeeTheUsernamesOfFriendsWhoFavoritedThatSong() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("user2"));
        assertTrue(driver.getPageSource().contains("user2"));
        assertTrue(driver.getPageSource().contains("user3"));
    }

    @Then("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String message) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement toast = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/div")));
//        wait.until(driver -> driver.getPageSource().contains(message));
//        assertTrue(driver.getPageSource().contains(message));
        assertTrue(toast.getText().contains(message));
    }
}
