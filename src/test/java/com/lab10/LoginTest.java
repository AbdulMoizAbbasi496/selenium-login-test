import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void test_login_with_incorrect_credentials() {
        driver.navigate().to("http://103.139.122.250:4000/login");
        
        // Wait for page to fully load
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
        
        // Find email field by ID
        WebElement emailField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        
        // Find password field by ID
        WebElement passwordField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        
        // Find Sign In button
        WebElement loginButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        
        // Fill form
        emailField.sendKeys("qasim@malik.com");
        passwordField.sendKeys("abcdefg");
        loginButton.click();
        
        // Wait a bit for API response
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Check for error message on page
        String pageSource = driver.getPageSource();
        
        // Debug: print page source if needed
        // System.out.println("Page source: " + pageSource);
        
        assertTrue(
            pageSource.contains("Incorrect") || 
            pageSource.contains("Failed") || 
            pageSource.contains("Invalid") ||
            pageSource.contains("error") ||
            pageSource.contains("Error"),
            "Expected error message not found. Actual page contains: " + pageSource.substring(0, Math.min(500, pageSource.length()))
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
