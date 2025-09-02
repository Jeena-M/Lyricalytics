package edu.usc.csci310.project;//package edu.usc.csci310.project;
//
//import io.cucumber.java.After;
//import io.cucumber.java.Before;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.Wait;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.Duration;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class Stepdefs {
//    private static final String ROOT_URL = "http://localhost:8080/";
//    private static final String LOGIN_URL = ROOT_URL + "login";
//    private static final String REGISTER_URL = ROOT_URL + "register";
//    private static final String WORD_CLOUD_URL = ROOT_URL + "search";
//    private final WebDriver driver = new ChromeDriver();
//
//    @Before
//    public void setUp(){
//        try {
//            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
//            Statement stmt = connection.createStatement();
//            String truncateTableSQL = "DELETE FROM users";
//            stmt.executeUpdate(truncateTableSQL);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Given("I am on the homepage")
//    public void iAmOnTheHomepage() {
//        driver.get(ROOT_URL); //http://localhost:8080/
//    }
//
//    @Then("I should see the title {string}")
//    public void iShouldSeeTheTitle(String pageTitle) throws InterruptedException {
//        Thread.sleep(2000);
//        assertTrue(driver.getPageSource().contains(pageTitle));
//    }
//
//    @Then("I should see the website name {string}")
//    public void iShouldSeeTheWebsiteName(String siteName) throws InterruptedException {
//        Thread.sleep(2000);
//        driver.findElement(By.xpath("//*[@id=\"websiteName\"]")).click();
//    }
//
//    @Given("I am on the login homepage")
//    public void iAmOnTheLoginHomepage() {
//        driver.get(LOGIN_URL); //http://localhost:8080/login
//    }
//
//    @Given("I am on the register homepage")
//    public void iAmOnTheRegisterHomepage() {
//        driver.get(REGISTER_URL); //http://localhost:8080/register
//    }
//
//    @Given("The username {string} already exists with password {string}")
//    public void theUsernameAlreadyExists(String username, String password) throws InterruptedException {
//        driver.get(REGISTER_URL); //http://localhost:8080/register
//        driver.findElement(By.xpath("//button[text()=\"Register\"]")).click();
//        driver.findElement(By.ById.id("username")).sendKeys(username);
//        driver.findElement(By.ById.id("password")).sendKeys(password);
//        driver.findElement(By.ById.id("confirmPassword")).sendKeys(password);
//        driver.findElement(By.xpath("//button[text()=\"Create\"]")).click();
//        Thread.sleep(1000);
//        driver.get(LOGIN_URL);
//    }
//
//    @When("I click the {string} button")
//    public void iClickTheButton(String button) {
////        driver.findElement(By.xpath("//button[text()=\""+button+"\"]")).click();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='" + button + "']")));
//        clickableButton.click();
//
//    }
//
//    @When("I click the Login button to login")
//    public void iClickTheButtonLogin() {
//        driver.findElement(By.xpath("/html/body/div/div/main/div/main/div/div/form/div[2]/button")).click();
//    }
//
//    @And("I enter {string} in the {string} field")
//    public void iEnterInTheUsernameField(String arg, String field) {
//        driver.findElement(By.ById.id(field)).sendKeys(arg);
//    }
//
//    @Then("I should see the success message {string}")
//    public void iShouldSeeTheSuccessMessage(String message) {
//        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
//        wait.until(driver -> driver.getPageSource().contains(message));
//        assertTrue(driver.getPageSource().contains(message));
//    }
//
//    @Then("I should see the error message {string}")
//    public void iShouldSeeTheErrorMessage(String message) {
//        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        wait.until(driver -> driver.getPageSource().contains(message));
//        assertTrue(driver.getPageSource().contains(message));
////        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
////        WebElement redPopup = wait.until(
////                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.bg-red-500")));
////        assertTrue(redPopup.isDisplayed());
////        assertTrue(driver.getPageSource().contains(message));
//
//    }
//
//
//    @After
//    public void cleanUp(){
//        driver.manage().deleteAllCookies();
//        try {
//            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
//            Statement stmt = connection.createStatement();
//            String truncateTableSQL = "DELETE FROM users";
//            stmt.executeUpdate(truncateTableSQL);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        driver.quit();
//    }
//
//    @Then("I should see the {string} popup")
//    public void iShouldSeeThePopup(String message) {
//        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
//        wait.until(driver -> driver.getPageSource().contains(message));
//        assertTrue(driver.getPageSource().contains(message));
//    }
//
//
//    @And("I should see the the login homepage with {string} and {string}")
//    public void iShouldSeeTheTheLoginHomepageWithAnd(String arg0, String arg1) {
//        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(4));
//        wait.until(driver -> driver.getPageSource().contains(arg0));
//        wait.until(driver -> driver.getPageSource().contains(arg1));
//        assertTrue(driver.getPageSource().contains(arg0));
//        assertTrue(driver.getPageSource().contains(arg1));
//    }
//
//    @Then("I should not see the {string} popup")
//    public void iShouldNotSeeThePopup(String arg0) {
//        boolean isPopupPresent;
//        try {
//            driver.findElement(By.xpath("//h2[contains(text(),'" + arg0 + "')]"));
//            isPopupPresent = true;
//        } catch (NoSuchElementException e) {
//            isPopupPresent = false;
//        }
//        assertFalse(isPopupPresent);
//    }
//
//    @And("I wait for {int} seconds")
//    public void iWaitForSeconds(int seconds) {
//        try {
//            Thread.sleep(seconds * 1000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Then("I should not see the error message {string}")
//    public void iShouldNotSeeTheErrorMessage(String arg0) {
//        boolean isPopupPresent;
//        try {
//            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            wait.until(driver -> driver.getPageSource().contains(arg0));
//            isPopupPresent = true;
//        } catch (TimeoutException e) {
//            isPopupPresent = false;
//        }
//        assertFalse(isPopupPresent);
//    }
//
//
//    @And("I try {string} as the {string} field {int} times within {int} sec")
//    public void iTryAsTheFieldTimesWithinSec(String value, String field, int times, int totalSeconds) throws InterruptedException {
//
//        if (totalSeconds <= 60) {
//            for (int i = 0; i < times; i++) {
//                WebElement inputField = new WebDriverWait(driver, Duration.ofSeconds(10))
//                        .until(ExpectedConditions.elementToBeClickable(By.id(field)));
//                inputField.clear();
//                inputField.sendKeys(value);
//                driver.findElement(By.xpath("/html/body/div/div/main/div/main/div/div/form/div[2]/button")).click();
//            }
//        }
//        else {
//            for (int i = 0; i < times; i++) {
//                WebElement inputField = new WebDriverWait(driver, Duration.ofSeconds(10))
//                        .until(ExpectedConditions.elementToBeClickable(By.id(field)));
//                inputField.clear();
//                inputField.sendKeys(value);
//                driver.findElement(By.xpath("/html/body/div/div/main/div/main/div/div/form/div[2]/button")).click();
//                if (i % 2 == 0) {
//                    Thread.sleep(60000);
//                }
//            }
//        }
//    }
//
//    @When("The user {string} already exists with password {string}")
//    public void theUserAlreadyExistsWithPassword(String username, String password) {
//        driver.findElement(By.xpath("//button[text()=\"Register\"]")).click();
//        driver.findElement(By.ById.id("username")).sendKeys(username);
//        driver.findElement(By.ById.id("password")).sendKeys(password);
//        driver.findElement(By.ById.id("confirmPassword")).sendKeys(password);
//        driver.findElement(By.xpath("//button[text()=\"Create\"]")).click();
//    }
//
//    @Given("I am on the word cloud homepage")
//    public void iAmOnTheWordCloudHomepage() {
//        driver.get(WORD_CLOUD_URL);
//    }
//
//    @Then("I should see a cloud with the top 100 words from the top {int} songs")
//    public void iShouldSeeACloudWithTheTopWordsFromTheTopSongs(int arg0) throws InterruptedException {
//        Thread.sleep(3000);
//
//        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));
//
//        System.out.println("Words found in cloud: " + wordElements.size());
//
//        assertTrue(wordElements.size() <= 100, "Word cloud contains more than 100 words");
//    }
//
//    @Given("there is an existing word cloud")
//    public void thereIsAnExistingWordCloud() throws InterruptedException {
//        driver.get(WORD_CLOUD_URL);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
//        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("artist")));
//        fieldElement.sendKeys("Taylor Swift");
//
//        WebElement fieldElement2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("songCount")));
//        fieldElement2.sendKeys("7");
//
//        driver.findElement(By.xpath("//button[text()=\""+"Submit"+"\"]")).click();
//        Thread.sleep(7000);
//    }
//
//    @When("I click on the word {string} in the table")
//    public void iClickOnTheWordInTheTable(String arg0) throws InterruptedException {
//        driver.findElement(By.xpath("//button[text()=\""+"View as table"+"\"]")).click();
//        Thread.sleep(1000);
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/main/div/div[3]/div/table/tbody/tr[1]/td[1]")).click();
//        Thread.sleep(7000);
//    }
//
//    @Then("I should see a list of songs with the word {string} and its frequency")
//    public void iShouldSeeAListOfSongsWithTheWordAndItsFrequency(String arg0) throws InterruptedException {
//        String pageSource = driver.getPageSource();
//        System.out.println("Page source: " + pageSource);
//        assertTrue(pageSource.contains("Title"));
//        assertTrue(pageSource.contains("Frequency"));
//        Thread.sleep(5000);
//    }
//
//    @When("I click on a song title {string} from the list")
//    public void iClickOnASongTitleFromTheList(String arg0) throws InterruptedException {
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div/div/form/div[1]/table/tbody/tr[1]/td[1]")).click();
//        Thread.sleep(11000);
//    }
//
//    @Then("I should see the lyrics of the song with the word {string} highlighted")
//    public void iShouldSeeTheLyricsOfTheSongWithTheWordHighlighted(String arg0) throws InterruptedException {
//        String pageSource = driver.getPageSource();
//        assertTrue(pageSource.contains("Lyrics:"));
//        assertTrue(pageSource.contains(arg0));
//        Thread.sleep(2000);
//    }
//
//    @And("I should see the song details \\(title, artist, year)")
//    public void iShouldSeeTheSongDetailsTitleArtistYear() throws InterruptedException {
//        String pageSource = driver.getPageSource();
//        assertTrue(pageSource.contains("Title:"));
//        assertTrue(pageSource.contains("Year:"));
//        assertTrue(pageSource.contains("Artist:"));
//        Thread.sleep(2000);
//    }
//
//    @When("the lyrics contain filler words like {string}")
//    public void theLyricsContainFillerWordsLike(String arg0) throws InterruptedException {
//        Thread.sleep(3000);
//    }
//
//    @Then("the word cloud should not contain {string}")
//    public void theWordCloudShouldNotContain(String arg0) throws Throwable {
//        String pageSource = driver.getPageSource();
//        assertFalse(pageSource.contains(arg0));
//    }
//
//    @Then("I should see a table listing words and their frequencies")
//    public void iShouldSeeATableListingWordsAndTheirFrequencies() throws InterruptedException {
//        String pageSource = driver.getPageSource();
//        assertTrue(pageSource.contains("Word"));
//        assertTrue(pageSource.contains("Frequency"));
//        Thread.sleep(2000);
//    }
//
//    @Then("I should see the graphical word cloud")
//    public void iShouldSeeTheGraphicalWordCloud() throws InterruptedException {
//        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));
//
//        assertFalse(wordElements.isEmpty());
//        Thread.sleep(2000);
//    }
//
//    @When("the lyrics contains stem words like {string}")
//    public void theLyricsContainsStemWordsLike(String arg0) throws InterruptedException {
//        Thread.sleep(2000);
//    }
//
//    @Then("{string} is displayed {string}")
//    public void isDisplayed(String word, String sizeComparison) {
//        WebElement graphButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
//        graphButton.click();
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));
//
//        List<WebElement> textElements = driver.findElements(By.cssSelector("svg text"));
//
//        WebElement wordElement = null;
//        WebElement otherElement = null;
//
//        for (WebElement el : textElements) {
//            String text = el.getText().trim().toLowerCase();
//            if (text.equals(word.toLowerCase())) {
//                wordElement = el;
//            } else if (text.equals(word.equalsIgnoreCase("go") ? "nicki" : "go")) {
//                otherElement = el;
//            }
//        }
//
//        float wordFontSize = parseFontSize(wordElement.getCssValue("font-size"));
//        float otherFontSize = parseFontSize(otherElement.getCssValue("font-size"));
//
//        System.out.println("Font size of '" + word + "': " + wordFontSize);
//        System.out.println("Font size of other word: " + otherFontSize);
//
//        if (sizeComparison.equalsIgnoreCase("larger")) {
//            assertTrue(wordFontSize > otherFontSize, word + " should be larger than the other word");
//        } else if (sizeComparison.equalsIgnoreCase("smaller")) {
//            assertTrue(wordFontSize < otherFontSize, word + " should be smaller than the other word");
//        }
//    }
//
//    private float parseFontSize(String fontSizeStr) {
//        return Float.parseFloat(fontSizeStr.replace("px", ""));
//    }
//
//    @And("there are more than 100 words in the lyrics")
//    public void there_are_more_than_100_words_in_the_lyrics() {
//        WebElement graphViewButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
//        graphViewButton.click();
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));
//    }
//
//    @Then("the word cloud should display no more than 100 words")
//    public void the_word_cloud_should_display_more_than_100_words() {
//        WebElement graphViewButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
//        graphViewButton.click();
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));
//
//        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));
//        int wordCount = wordElements.size();
//
//        System.out.println("Words displayed in word cloud (graph view): " + wordCount);
//
//        assertEquals(100, wordCount, "Expected more than 100 words, but found " + wordCount);
//    }
//}
