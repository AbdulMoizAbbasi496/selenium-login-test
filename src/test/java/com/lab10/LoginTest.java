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

public class LoginTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // WebDriverManager auto-downloads correct chromedriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");              // No GUI needed
        options.addArguments("--no-sandbox");            // Required in Docker/EC2
        options.addArguments("--disable-dev-shm-usage"); // Required in Docker/EC2
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void test_login_with_incorrect_credentials() {
        System.out.println("TEST: Navigating to login page...");
        driver.navigate().to("http://103.139.122.250:4000/");

        System.out.println("TEST: Entering wrong credentials...");
        driver.findElement(By.name("email")).sendKeys("wrong@email.com");
        driver.findElement(By.name("password")).sendKeys("wrongpassword");
        driver.findElement(By.id("m_login_signin_submit")).click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        System.out.println("TEST: Checking for error message...");
        WebElement errorElement = driver.findElement(
            By.xpath("/html/body/div/div/div[1]/div/div/div/div[2]/form/div[1]")
        );
        String errorText = errorElement.getText();
        System.out.println("Error text found: " + errorText);

        assert(errorText.contains("Incorrect email or password")) :
            "Expected error message not found. Actual: " + errorText;

        System.out.println("TEST PASSED: Correct error message shown for wrong credentials.");
    }

    @Test
    void test_login_page_loads_successfully() {
        System.out.println("TEST: Checking if login page loads...");
        driver.navigate().to("http://103.139.122.250:4000/");

        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        // Check that the email field exists (page loaded correctly)
        WebElement emailField = driver.findElement(By.name("email"));
        assert(emailField.isDisplayed()) : "Email field not visible";

        WebElement passwordField = driver.findElement(By.name("password"));
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
