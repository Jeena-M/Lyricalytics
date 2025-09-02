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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FavoritesStepDefs {
    private static final String ROOT_URL = "http://localhost:8080/";
    private static final String LOGIN_URL = ROOT_URL + "login";
    private static final String REGISTER_URL = ROOT_URL + "register";
    private final WebDriver driver = WebDriverHelper.getDriver();
    private static final String WORD_CLOUD_URL = ROOT_URL + "search";
    private static final String FAVORITES_URL = ROOT_URL + "favorites";

    @Given("I am on the website home page")
    public void iAmOnTheHomepage() {
        driver.get(ROOT_URL); //http://localhost:8080/
    }

    @Given("I am logged in and have songs on my favorites list")
    public void iAmLoggedInAndHaveFavorites() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(LOGIN_URL);
        WebElement username2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        username2.sendKeys("user1");
        WebElement password2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        password2.sendKeys("Pass12");
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        loginButton.click();
        Thread.sleep(1000);
        driver.get(WORD_CLOUD_URL);
    }

    @Given("I am on the favorites page")
    public void iAmOnTheFavoritesPage() {
        driver.get(FAVORITES_URL);
    }

    @Given("I just made a new account")
    public void iJustMadeANewAccount() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        driver.get(REGISTER_URL); //http://localhost:8080/register
//        WebElement username = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
//        username.sendKeys("user1");
//        WebElement password = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
//        password.sendKeys("Pass12");
//        WebElement confirmPassword = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("confirmPassword")));
//        confirmPassword.sendKeys("Pass12");
//        WebElement create = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()=\"Create\"]")));
//        create.click();
//        Thread.sleep(1000);
        driver.get(LOGIN_URL);
        WebElement login = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        login.click();
        WebElement username2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        username2.sendKeys("user1");
        WebElement password2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        password2.sendKeys("Pass12");
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        loginButton.click();
        Thread.sleep(1000);
//        driver.get(FAVORITES_URL);
    }

    @Given("My account is public")
    public void myAccountIsPublic() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement publicButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div/main/div/main/div[1]/button[2]")));
//        publicButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='" + "Account Public" + "']")));
        Actions actions = new Actions(driver);
        actions.moveToElement(clickableButton).perform();
        clickableButton.click();
    }

    @When("I click on the {string} button")
    public void iClickTheButton(String button) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='" + button + "']")));
        Actions actions = new Actions(driver);
        actions.moveToElement(clickableButton).perform();
        clickableButton.click();
    }

    @Then("My account should be private")
    public void myAccountShouldBePrivate() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement publicButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[1]/button[2]")));
        String classes = publicButton.getAttribute("class");
        assertTrue(classes.contains("bg-purple-400"));
    }

    @Then("My account should be public")
    public void myAccountShouldBePublic() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement publicButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[1]/button[2]")));
        String classes = publicButton.getAttribute("class");
        assertTrue(classes.contains("bg-purple-700"));
    }

    @Then("I should see a message saying {string}")
    public void iShouldSeeTheSuccessMessage(String message) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(driver -> driver.getPageSource().contains(message));
        assertTrue(driver.getPageSource().contains(message));
    }

    @Then("I should see no songs on my favorites list")
    public void iShouldSeeNoSongsOnMyFavoritesList() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("No favorite songs found."));
        assertTrue(driver.getPageSource().contains("No favorite songs found."));
    }

    @Then("I can see my favorites list")
    public void iCanSeeMyFavoritesList() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Title"));
        assertTrue(driver.getPageSource().contains("Title"));
    }

    @Then("No songs should be deleted from my favorites list")
    public void noSongsShouldBeDeletedFromMyFavoritesList() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Title"));
        assertTrue(driver.getPageSource().contains("Sorry"));
        assertTrue(driver.getPageSource().contains("Love Yourself"));
    }

    @Then("Song 1 should not be removed from my favorites list")
    public void song1ShouldNotBeRemovedFromMyFavoritesList() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Sorry"));
        assertTrue(driver.getPageSource().contains("Sorry"));
    }

    @When("I click on the title of song 1")
    public void iClickOnTheTitleOfSong1() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement listSpace = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[2]/table/tbody/tr[1]/td[2]/button[1]")));
        listSpace.click();
    }

    @Then("I should see the song's artist and year")
    public void iShouldSeeTheSongArtistAndYear() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains("Details"));
        assertTrue(driver.getPageSource().contains("Details"));
    }

    @When("I hover over the title of song 1")
    public void iHoverOverTheTitleOfSong1() {
        // /html/body/div/div/main/div/main/div/div[2]/table/tbody/tr/td[2]
        // /html/body/div/div/main/div/main/div/div[2]/table/tbody

        // /html/body/div/div/main/div/main/div/div[2]/table/tbody/tr[1]/td[2]
        // /html/body/div/div/main/div/main/div/div[2]/table/tbody/tr[1]
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement song1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[2]/table/tbody/tr[1]/td[2]/button[1]")));
        Actions actions = new Actions(driver);
        actions.moveToElement(song1).perform();
    }

    @When("I hover over the title of song 2")
    public void iHoverOverTheTitleOfSong2() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement song2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[2]/table/tbody/tr[2]/td[2]/button[1]")));
        Actions actions = new Actions(driver);
        actions.moveToElement(song2).perform();
    }

    @Then("Song 2 {string} should be the first song on my favorites list")
    public void song2ShouldBeTheFirstSongOnMyFavoritesList(String song) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement song1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[2]/table/tbody/tr[1]/td[2]/button[1]")));
        wait.until(ExpectedConditions.textToBePresentInElement(song1, song));
        assertTrue(song1.getText().contains(song));
    }

    @Then("Song 1 {string} should be the second song on my favorites list")
    public void song1ShouldBeTheSecondSongOnMyFavoritesList(String song) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement song1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div[2]/table/tbody/tr[2]/td[2]/button[1]")));
        wait.until(ExpectedConditions.textToBePresentInElement(song1, song));
        assertTrue(song1.getText().contains(song));
    }
}