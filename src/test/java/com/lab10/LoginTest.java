package com.lab10;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        // Page load timeout
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @Test
    void test_login_with_incorrect_credentials() {
        System.out.println("TEST: Navigating to login page...");
        driver.navigate().to("http://103.139.122.250:4000/");

        // Explicit wait — wait up to 15 seconds for email field to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("TEST: Waiting for email field...");
        WebElement emailField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );

        System.out.println("TEST: Entering wrong credentials...");
        emailField.sendKeys("wrong@email.com");

        WebElement passwordField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.name("password"))
        );
        passwordField.sendKeys("wrongpassword");

        WebElement loginBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("m_login_signin_submit"))
        );
        loginBtn.click();

        System.out.println("TEST: Waiting for error message...");
        WebElement errorElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div[1]/div/div/div/div[2]/form/div[1]")
            )
        );

        String errorText = errorElement.getText();
        System.out.println("Error text found: " + errorText);

        assert(errorText.contains("Incorrect email or password")) :
            "Expected error message not found. Actual: " + errorText;

        System.out.println("TEST PASSED: Correct error message shown.");
    }

    @Test
    void test_login_page_loads_successfully() {
        System.out.println("TEST: Checking if login page loads...");
        driver.navigate().to("http://103.139.122.250:4000/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement emailField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.name("email"))
        );
        assert(emailField.isDisplayed()) : "Email field not visible";

        WebElement passwordField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.name("password"))
        );
        assert(passwordField.isDisplayed()) : "Password field not visible";

        System.out.println("TEST PASSED: Login page loaded with all elements.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
