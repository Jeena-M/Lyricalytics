package edu.usc.csci310.project;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordCloudStepDefs {
    private static final String ROOT_URL = "http://localhost:8080/";
    private static final String LOGIN_URL = ROOT_URL + "login";
    private final WebDriver driver = WebDriverHelper.getDriver();
    private static final String WORD_CLOUD_URL = ROOT_URL + "search";
    private static final String FAVORITES_URL = ROOT_URL + "favorites";
    private Connection connection;

    @Before
    public void setUp(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "failed_attempts INTEGER DEFAULT 0, " +
                    "last_failed_attempt TIMESTAMP, " +
                    "locked_until TIMESTAMP," +
                    "privacy BOOLEAN DEFAULT 1" +
                    ");";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table users created");
            String truncateTableSQL = "DELETE FROM users";
            stmt.executeUpdate(truncateTableSQL);
            String login = "INSERT INTO users (username, password) VALUES ('user1', 'Pass12')";
            stmt.executeUpdate(login);

            Statement stmt2 = connection.createStatement();
            String createFavoritesTable = "CREATE TABLE IF NOT EXISTS favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "song TEXT NOT NULL, " +
                    "artist TEXT NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username)" +
                    ");";
            stmt2.executeUpdate(createFavoritesTable);
            System.out.println("Table favorites created");
            String truncateFavs = "DELETE FROM favorites";
            stmt2.executeUpdate(truncateFavs);

            String insert1 = "INSERT INTO favorites (song, artist, username) VALUES ('Sorry', 'Justin Bieber', 'user1')";
            String insert2 = "INSERT INTO favorites (song, artist, username) VALUES ('Love Yourself', 'Justin Bieber', 'user1')";
            stmt2.executeUpdate(insert1);
            stmt2.executeUpdate(insert2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    @Before
//    public void setUp() {
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db?journal_mode=WAL&cache=shared");
//            Statement stmt = connection.createStatement();
//
//            stmt.executeUpdate("""
//                        CREATE TABLE IF NOT EXISTS users (
//                            id INTEGER PRIMARY KEY AUTOINCREMENT,
//                            username TEXT NOT NULL,
//                            password TEXT NOT NULL,
//                            failed_attempts INTEGER DEFAULT 0,
//                            last_failed_attempt TIMESTAMP,
//                            locked_until TIMESTAMP,
//                            privacy BOOLEAN DEFAULT 1
//                        );
//                    """);
//
//            stmt.executeUpdate("DELETE FROM users");
//            stmt.executeUpdate("INSERT INTO users (username, password, privacy) VALUES ('user1', 'Pass12', 1)");
//
//            stmt.executeUpdate("""
//                        CREATE TABLE IF NOT EXISTS favorites (
//                            id INTEGER PRIMARY KEY AUTOINCREMENT,
//                            username TEXT NOT NULL,
//                            song TEXT NOT NULL,
//                            artist TEXT NOT NULL,
//                            FOREIGN KEY (username) REFERENCES users(username)
//                        );
//                    """);
//
//            stmt.executeUpdate("DELETE FROM favorites");
//            stmt.executeUpdate("INSERT INTO favorites (song, artist, username) VALUES ('Sorry', 'Justin Bieber', 'user1')");
//            stmt.executeUpdate("INSERT INTO favorites (song, artist, username) VALUES ('Love Yourself', 'Justin Bieber', 'user1')");
//
//            stmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    @After
    public void cleanUp(){
        driver.manage().deleteAllCookies();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:userDatabase.db");
            Statement stmt = connection.createStatement();
            String truncateTableSQL = "DELETE FROM users";
            stmt.executeUpdate(truncateTableSQL);

//            Connection connection2 = DriverManager.getConnection("jdbc:sqlite:favoritesDatabase.db");
            Statement stmt2 = connection.createStatement();
            String dropTableSQL = "DELETE FROM favorites";
            stmt2.executeUpdate(dropTableSQL);
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        WebDriverHelper.quit();
    }
//    @After
//    public void cleanUp() {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                Statement stmt = connection.createStatement();
//                stmt.executeUpdate("DELETE FROM users");
//                stmt.executeUpdate("DELETE FROM favorites");
//                stmt.close();
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        driver.manage().deleteAllCookies();
//        WebDriverHelper.quit();
//    }

    @Given("I am on the word cloud homepage")
    public void iAmOnTheWordCloudHomepage() {
        driver.get(WORD_CLOUD_URL);
    }

    @Then("I should see a cloud with the top 100 words from the top {int} songs")
    public void iShouldSeeACloudWithTheTopWordsFromTheTopSongs(int arg0) throws InterruptedException {
        Thread.sleep(7000);

        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));

        System.out.println("Words found in cloud: " + wordElements.size());

        assertTrue(wordElements.size() <= 100, "Word cloud contains more than 100 words");
    }

    @Given("there is an existing word cloud")
    public void thereIsAnExistingWordCloud() throws InterruptedException {
        driver.get(WORD_CLOUD_URL);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
//        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("artist")));
//        fieldElement.sendKeys("Taylor Swift");
//
//        WebElement fieldElement2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("songCount")));
//        fieldElement2.sendKeys("7");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div/div[1]/input[1]")));
        fieldElement.sendKeys("Taylor Swift");

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(4));
        WebElement fieldElement2 = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div/div[1]/input[2]")));
        fieldElement2.sendKeys("7");


        driver.findElement(By.xpath("//button[text()=\""+"Submit"+"\"]")).click();
        Thread.sleep(7000);
    }

    @When("I click on the word {string} in the table")
    public void iClickOnTheWordInTheTable(String arg0) throws InterruptedException {
        driver.findElement(By.xpath("//button[text()=\""+"View as table"+"\"]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/main/div/div[3]/div/table/tbody/tr[1]/td[1]")).click();
        Thread.sleep(7000);
    }

    @Then("I should see a list of songs with the word {string} and its frequency")
    public void iShouldSeeAListOfSongsWithTheWordAndItsFrequency(String arg0) throws InterruptedException {
        String pageSource = driver.getPageSource();
        //System.out.println("Page source: " + pageSource);
        assertTrue(pageSource.contains("Title"));
        assertTrue(pageSource.contains("Frequency"));
        Thread.sleep(5000);
    }

    @When("I click on a song title {string} from the list")
    public void iClickOnASongTitleFromTheList(String arg0) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div/div/form/div[1]/table/tbody/tr[1]/td[1]")).click();
        Thread.sleep(11000);
    }

    @Then("I should see the lyrics of the song with the word {string} highlighted")
    public void iShouldSeeTheLyricsOfTheSongWithTheWordHighlighted(String arg0) throws InterruptedException {
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Lyrics:"));
        assertTrue(pageSource.contains(arg0));
        Thread.sleep(2000);
    }

    @And("I should see the song details \\(title, artist, year)")
    public void iShouldSeeTheSongDetailsTitleArtistYear() throws InterruptedException {
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Title:"));
        assertTrue(pageSource.contains("Year:"));
        assertTrue(pageSource.contains("Artist:"));
        Thread.sleep(2000);
    }

    @When("the lyrics contain filler words like {string}")
    public void theLyricsContainFillerWordsLike(String arg0) throws InterruptedException {
        Thread.sleep(3000);
    }

    @Then("the word cloud should not contain {string}")
    public void theWordCloudShouldNotContain(String arg0) throws Throwable {
        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains(arg0));
    }

    @Then("I should see a table listing words and their frequencies")
    public void iShouldSeeATableListingWordsAndTheirFrequencies() throws InterruptedException {
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Word"));
        assertTrue(pageSource.contains("Frequency"));
        Thread.sleep(2000);
    }

    @Then("I should see the graphical word cloud")
    public void iShouldSeeTheGraphicalWordCloud() throws InterruptedException {
        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));

        assertFalse(wordElements.isEmpty());
        Thread.sleep(2000);
    }

    @When("the lyrics contains stem words like {string}")
    public void theLyricsContainsStemWordsLike(String arg0) throws InterruptedException {
        Thread.sleep(2000);
    }

    @Then("{string} is displayed {string}")
    public void isDisplayed(String word, String sizeComparison) {
        WebElement graphButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
        graphButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));

        List<WebElement> textElements = driver.findElements(By.cssSelector("svg text"));

        WebElement wordElement = null;
        WebElement otherElement = null;

        String otherWord = word.equalsIgnoreCase("sorry") ? "hit" : "sorry";

        for (WebElement el : textElements) {
            String text = el.getText().trim().toLowerCase();
            if (text.equals(word.toLowerCase())) {
                wordElement = el;
            } else if (text.equals(otherWord.toLowerCase())) {
                otherElement = el;
            }
        }

        if (wordElement == null || otherElement == null) {
            throw new RuntimeException("Could not find both '" + word + "' and '" + otherWord + "' in the SVG elements.");
        }

        float wordFontSize = parseFontSize(wordElement.getCssValue("font-size"));
        float otherFontSize = parseFontSize(otherElement.getCssValue("font-size"));

        System.out.println("Font size of '" + word + "': " + wordFontSize);
        System.out.println("Font size of '" + otherWord + "': " + otherFontSize);

        if (sizeComparison.equalsIgnoreCase("larger")) {
            assertTrue(wordFontSize > otherFontSize, word + " should be larger than " + otherWord);
        } else if (sizeComparison.equalsIgnoreCase("smaller")) {
            assertTrue(wordFontSize < otherFontSize, word + " should be smaller than " + otherWord);
        }
    }


    private float parseFontSize(String fontSizeStr) {
        return Float.parseFloat(fontSizeStr.replace("px", ""));
    }

    @And("there are more than 100 words in the lyrics")
    public void there_are_more_than_100_words_in_the_lyrics() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement graphViewButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
        WebElement graphViewButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'View as graph')]")));
        graphViewButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));
    }

    @Then("the word cloud should display no more than 100 words")
    public void the_word_cloud_should_display_more_than_100_words() {
        WebElement graphViewButton = driver.findElement(By.xpath("//button[contains(text(), 'View as graph')]"));
        graphViewButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("svg text")));

        List<WebElement> wordElements = driver.findElements(By.cssSelector("svg text"));
        int wordCount = wordElements.size();

        System.out.println("Words displayed in word cloud (graph view): " + wordCount);

        assertEquals(100, wordCount, "Expected more than 100 words, but found " + wordCount);
    }

    @When("I enter {string} in the artist search field")
    public void iEnterInTheSearchField(String arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div/div[1]/input[1]")));
        fieldElement.clear();
        fieldElement.sendKeys(arg0);
    }

    @And("I enter {string} in the song count field")
    public void iEnterInTheSongCountSearchField(String arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/main/div/div[1]/input[2]")));
        fieldElement.clear();
        fieldElement.sendKeys(arg0);
    }


    @Then("I should not see a word cloud for {string}")
    public void iShouldNotSeeAWordCloudFor(String arg0) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean present;
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("/html/body/div/div/main/div/main/div/div[3]/div/svg")
                    )
            );
            present = true;

        } catch (TimeoutException expected) {
            present = false;
        }
        assertFalse(present);
    }

    @Then("I should see a song selection dialogue")
    public void iShouldSeeASongSelectionDialogue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean present;
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".bg-yellow-100")
                    )
            );
            present = true;

        } catch (TimeoutException expected) {
            present = false;
        }
        assertTrue(present);
    }

    @And("I click the submit button in the dialogue")
    public void iClickTheSubmitButtonInTheDialogue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div/main/div/main/div/div[2]/button")));
        clickableButton.click();
    }

    @Then("I should see words from songs in the favorites list in the word cloud")
    public void iShouldSeeWordsFromSongsInTheFavoritesListInTheWordCloud() throws InterruptedException {
        Thread.sleep(5000);
        String pageSource = driver.getPageSource();
//        System.out.println("Page source: " + pageSource);
        assertTrue(pageSource.contains("baby"));
    }

    @Then("I should see a popup with all artists named {string} with their photos")
    public void iShouldSeeAPopupWithAllArtistsNamedWithTheirPhotos(String arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean present;
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".bg-gray-100")
                    )
            );
            present = true;

        } catch (TimeoutException expected) {
            present = false;
        }
        assertTrue(present);
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(arg0));

    }

    @When("I select {string}")
    public void iSelect(String arg0) {
        WebElement namebtn = driver.findElement(By.xpath("/html/body/div/div/main/div/main/div/div[3]/div/div[1]"));
        namebtn.click();
    }

    @Then("I should see a list of popular songs for {string}")
    public void iShouldSeeAListOfPopularSongsFor(String arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean present;
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".bg-yellow-100")
                    )
            );
            present = true;

        } catch (TimeoutException expected) {
            present = false;
        }
        assertTrue(present);
    }

    @Then("the word cloud should be generated in less than {int} second")
    public void theWordCloudShouldBeGeneratedInLessThanSecond(int arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        List<WebElement> wordElements = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("svg text"))
        );

        assertFalse(wordElements.isEmpty());

    }

    @When("I click on the word {string} in the word cloud")
    public void iClickOnTheWordInTheWordCloud(String word) throws InterruptedException {

        String xpath = String.format(
                "//*[name()='svg']//*[name()='text' and normalize-space(.)='%s']",
                word
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement textElem = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
        );

        wait.until(ExpectedConditions.elementToBeClickable(textElem));
        textElem.click();

        Thread.sleep(5000);
    }

    @When("I hover over the song title {string}")
    public void iHoverOverTheSongTitle(String arg0) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement song1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/main/div/div/div/form/div[1]/table/tbody/tr[1]/td[1]")));
        Actions actions = new Actions(driver);
        actions.moveToElement(song1).perform();
    }

    @Then("a popup should appear with an option to add to favorites")
    public void aPopupShouldAppearWithAnOptionToAddToFavorites() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/div[2]/div/h2")));
        String text = heading.getText();
        assertTrue(heading.isDisplayed());
        assertTrue(text.matches("^Add\\s+.+\\s+to favorites\\?$"));

    }

    @Then("{string} should be added to my favorites list")
    public void shouldBeAddedToMyFavoritesList(String arg0) {
        driver.get(FAVORITES_URL);
        String pageSource = driver.getPageSource();
        //System.out.println("Page source: " + pageSource);
        assertTrue(pageSource.contains(arg0));

    }

    @Then("the word cloud should update to have songs from multiple artists")
    public void theWordCloudShouldUpdateToHaveSongsFromMultipleArtists() throws InterruptedException {
        Thread.sleep(8000);
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("baby"));
        assertTrue(pageSource.contains("sorry"));
    }

    @And("I select the song {string}")
    public void iSelectTheSong(String songTitle) {
        WebElement songRow = driver.findElement(By.xpath(
                String.format(
                        "//span[normalize-space()=\"%s\"]/parent::div",
                        songTitle
                )
        ));
        WebElement addButton = songRow.findElement(By.xpath(
                ".//button[normalize-space()=\"Add\"]"
        ));
        addButton.click();
    }
}