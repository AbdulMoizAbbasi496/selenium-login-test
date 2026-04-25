package com.lab10;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void test_login_with_incorrect_credentials() {

        driver.get("http://103.139.122.250:4000/");

        driver.findElement(By.id("email")).sendKeys("qasim@malik.com");
        driver.findElement(By.id("password")).sendKeys("abcdefg");

        // safer click (button may not always have same id in real DOM builds)
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement errorText = driver.findElement(
                By.xpath("//form//div[contains(text(),'Incorrect') or contains(text(),'incorrect')]")
        );

        assertTrue(
                errorText.getText().contains("Incorrect email or password"),
                "Error message not found"
        );
    }

    @Test
    void test_login_page_loads() {

        driver.get("http://103.139.122.250:4000/");

        assertTrue(driver.findElement(By.id("email")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
