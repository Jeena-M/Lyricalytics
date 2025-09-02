package edu.usc.csci310.project;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverHelper {
    private static WebDriver driver; // Declare a static variable WebDriver

    public static WebDriver getDriver() {
        if (driver == null) { // If driver has not been instantiated thus far:
            driver = new ChromeDriver(); // Instantiate driver
        }
        return driver; // Return driver
    }

    public static void quit() {
        if (driver != null) { // If driver has not already been quit:
            driver.quit(); // Quit driver
            driver = null; // Set driver equal to null (for getDriver() condition)
        }
    }
}
