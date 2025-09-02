package edu.usc.csci310.project;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchStepDefs {
    private static final String ROOT_URL = "http://localhost:8080/";
    private static final String MATCH_URL = ROOT_URL + "match";
    private static final String LOGIN_URL = ROOT_URL + "login";
    private final WebDriver driver = WebDriverHelper.getDriver();

    @Given("There are multiple users with public accounts")
    public void thereAreMultipleUsersWithPublicAccounts() {
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
            stmt2.executeUpdate(insert1);
            stmt2.executeUpdate(insert2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Given("I am the only user with a public account")
    public void iAmTheOnlyUserWithAPublicAccount() {
        return;
    }

    @Given("I am logged in and on the match page")
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
        driver.get(MATCH_URL);
    }

    @Then("I should see the username {string}")
    public void iShouldSeeTheUsername(String username) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains(username));
        assertTrue(driver.getPageSource().contains(username));
    }

    @Then("I should see a list of their favorites")
    public void iShouldSeeAListOfTheirFavorites() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Song Title"));
        assertTrue(driver.getPageSource().contains("Song Title"));
    }

    @Then("I should see a message {string}")
    public void iShouldSeeAListOfTheirFavorites(String message) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains(message));
        assertTrue(driver.getPageSource().contains(message));
    }

    @Then("I should see a {string} animation")
    public void iShouldSeeAAnimation(String animation) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        if(animation.equals("positive")){
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'🎉')]")));
            assertTrue(driver.getPageSource().contains("🎉"));
        } else {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'😈')]")));
            assertTrue(driver.getPageSource().contains("😈"));
        }
    }
}
