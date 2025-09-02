package edu.usc.csci310.project;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginStepDefs {
    private final WebDriver driver = WebDriverHelper.getDriver();
    private static final String ROOT_URL = "http://localhost:8080/";
    private static final String LOGIN_URL = ROOT_URL + "login";
    private static final String REGISTER_URL = ROOT_URL + "register";

    @Given("I am on the homepage")
    public void iAmOnTheHomepage() {
        driver.get(ROOT_URL); //http://localhost:8080/
    }

    @Then("I should see the title {string}")
    public void iShouldSeeTheTitle(String pageTitle) throws InterruptedException {
//        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> driver.getPageSource().contains(pageTitle));
        assertTrue(driver.getPageSource().contains(pageTitle));
    }

    @Then("I should see the website name {string}")
    public void iShouldSeeTheWebsiteName(String siteName) throws InterruptedException {
//        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean isPopupPresent;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/nav/h1")));
//            driver.findElement(By.xpath("/html/body/div/div/nav/h1"));
            isPopupPresent = true;
        } catch (NoSuchElementException e) {
            isPopupPresent = false;
        }
        assertTrue(isPopupPresent);
    }


    @Given("I am on the login homepage")
    public void iAmOnTheLoginHomepage() {
        driver.get(LOGIN_URL); //http://localhost:8080/login
    }

    @Given("I am on the register homepage")
    public void iAmOnTheRegisterHomepage() {
        driver.get(REGISTER_URL); //http://localhost:8080/register
    }

    @Given("The username {string} already exists with password {string}")
    public void theUsernameAlreadyExists(String username, String password) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(REGISTER_URL); //http://localhost:8080/register
        WebElement name = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        name.sendKeys(username);
        WebElement pass = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        pass.sendKeys(password);
        WebElement confirmPassword = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("confirmPassword")));
        confirmPassword.sendKeys(password);
        WebElement create = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()=\"Create\"]")));
        create.click();
        Thread.sleep(1000);
        driver.get(LOGIN_URL);
    }

    @When("I click the {string} button")
    public void iClickTheButton(String button) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='" + button + "']")));
        clickableButton.click();

    }

    @When("I click the Login button to login")
    public void iClickTheButtonLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        driver.findElement(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")).click();
    }

    @And("I enter {string} in the {string} field")
    public void iEnterInTheUsernameField(String arg, String field) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(field)));
        fieldElement.sendKeys(arg);
    }

    @Then("I should see the success message {string}")
    public void iShouldSeeTheSuccessMessage(String message) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(driver -> driver.getPageSource().contains(message));
        assertTrue(driver.getPageSource().contains(message));
    }

    @Then("I should see the error message {string}")
    public void iShouldSeeTheErrorMessage(String message) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement toast = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/div")));
        System.out.println(toast.getText());
        assertTrue(toast.getText().contains(message));
    }

    @Then("I should see the {string} popup")
    public void iShouldSeeThePopup(String message) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(driver -> driver.getPageSource().contains(message));
        assertTrue(driver.getPageSource().contains(message));
    }

    @And("I should see the the login homepage with {string} and {string}")
    public void iShouldSeeTheTheLoginHomepageWithAnd(String arg0, String arg1) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(driver -> driver.getPageSource().contains(arg0));
        wait.until(driver -> driver.getPageSource().contains(arg1));
        assertTrue(driver.getPageSource().contains(arg0));
        assertTrue(driver.getPageSource().contains(arg1));
    }

    @Then("I should not see the {string} popup")
    public void iShouldNotSeeThePopup(String arg0) {
        boolean isPopupPresent;
        try {
            driver.findElement(By.xpath("//h2[contains(text(),'" + arg0 + "')]"));
            isPopupPresent = true;
        } catch (NoSuchElementException e) {
            isPopupPresent = false;
        }
        assertFalse(isPopupPresent);
    }

    @And("I wait for {int} seconds")
    public void iWaitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("I should not see the error message {string}")
    public void iShouldNotSeeTheErrorMessage(String arg0) {
        boolean isPopupPresent;
        try {
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(driver -> driver.getPageSource().contains(arg0));
            isPopupPresent = true;
        } catch (TimeoutException e) {
            isPopupPresent = false;
        }
        assertFalse(isPopupPresent);
    }

    @And("I try {string} as the {string} field {int} times within {int} sec")
    public void iTryAsTheFieldTimesWithinSec(String value, String field, int times, int totalSeconds) throws InterruptedException {

        if (totalSeconds <= 60) {
            for (int i = 0; i < times; i++) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(By.id(field)));
                inputField.clear();
                inputField.sendKeys(value);
                WebElement login = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
//                        driver.findElement(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")).click();
                login.click();
            }
        }
        else {
            for (int i = 0; i < times; i++) {
                WebElement inputField = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(By.id(field)));
                inputField.clear();
                inputField.sendKeys(value);
                driver.findElement(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")).click();
                if (i % 2 == 0) {
                    Thread.sleep(60000);
                }
            }
        }
        Thread.sleep(1000);
    }


    @Given("I am logged in")
    public void iAmLoggedIn() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(LOGIN_URL);
        WebElement username2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("username")));
        username2.sendKeys("user1");
        WebElement password2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.ById.id("password")));
        password2.sendKeys("Pass12");
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/main/section/form/div[2]/button")));
        loginButton.click();
        Thread.sleep(1000);
    }
}