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
import java.util.List;

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
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
    }

    void debugPage() throws InterruptedException {
        System.out.println("PAGE TITLE: " + driver.getTitle());
        System.out.println("CURRENT URL: " + driver.getCurrentUrl());
        System.out.println("PAGE SOURCE SNIPPET:");
        String src = driver.getPageSource();
        System.out.println(src.substring(0, Math.min(src.length(), 3000)));

        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        System.out.println("TOTAL INPUTS FOUND: " + inputs.size());
        for (WebElement el : inputs) {
            System.out.println("  INPUT -> type=" + el.getAttribute("type")
                + " | name=" + el.getAttribute("name")
                + " | id=" + el.getAttribute("id")
                + " | placeholder=" + el.getAttribute("placeholder")
                + " | class=" + el.getAttribute("class"));
        }
    }

    @Test
    void test_login_page_loads_successfully() throws InterruptedException {
        System.out.println("TEST: Navigating...");
        driver.navigate().to("http://103.139.122.250:4000/");

        // Wait 10 seconds for JS to render the page
        Thread.sleep(10000);

        debugPage();

        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        assert inputs.size() > 0 : "No input fields found — page did not render";
        System.out.println("TEST PASSED: Page loaded with " + inputs.size() + " input(s).");
    }

    @Test
    void test_login_with_incorrect_credentials() throws InterruptedException {
        System.out.println("TEST: Navigating...");
        driver.navigate().to("http://103.139.122.250:4000/");

        // Wait 10 seconds for JS to render
        Thread.sleep(10000);

        debugPage();

        // Find first visible text/email input (email field)
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement emailField = null;
        WebElement passwordField = null;

        for (WebElement el : inputs) {
            String type = el.getAttribute("type");
            String id   = el.getAttribute("id");
            String name = el.getAttribute("name");
            System.out.println("Checking input: type=" + type + " id=" + id + " name=" + name);

            if (passwordField == null && "password".equals(type)) {
                passwordField = el;
            } else if (emailField == null &&
                       ("text".equals(type) || "email".equals(type) || type == null)) {
                emailField = el;
            }
        }

        // Also try by id="email" directly
        if (emailField == null) {
            try { emailField = driver.findElement(By.id("email")); } catch (Exception e) {}
        }
        if (passwordField == null) {
            try { passwordField = driver.findElement(By.id("password")); } catch (Exception e) {}
        }

        assert emailField    != null : "Email field not found after 10s wait";
        assert passwordField != null : "Password field not found after 10s wait";

        System.out.println("Typing credentials...");
        emailField.clear();
        emailField.sendKeys("wrong@email.com");
        passwordField.clear();
        passwordField.sendKeys("wrongpassword");

        // Find and click submit button
        WebElement submitBtn = null;
        try { submitBtn = driver.findElement(By.id("m_login_signin_submit")); } catch (Exception e) {}
        if (submitBtn == null) {
            try { submitBtn = driver.findElement(By.cssSelector("button[type='submit']")); } catch (Exception e) {}
        }
        if (submitBtn == null) {
            try { submitBtn = driver.findElement(By.tagName("button")); } catch (Exception e) {}
        }
        assert submitBtn != null : "Submit button not found";
        submitBtn.click();

        // Wait for error message
        Thread.sleep(5000);

        // Print page again after submit
        System.out.println("PAGE AFTER SUBMIT:");
        System.out.println(driver.getPageSource().substring(0, Math.min(driver.getPageSource().length(), 2000)));

        // Try to find any error text
        String errorText = "";
        String[] xpaths = {
            "/html/body/div/div/div[1]/div/div/div/div[2]/form/div[1]",
            "//div[contains(@class,'error')]",
            "//div[contains(@class,'alert')]",
            "//div[contains(@class,'danger')]",
            "//span[contains(@class,'error')]",
            "//p[contains(text(),'incorrect') or contains(text(),'Invalid') or contains(text(),'wrong')]"
        };
        for (String xp : xpaths) {
            try {
                WebElement el = driver.findElement(By.xpath(xp));
                if (!el.getText().isEmpty()) {
                    errorText = el.getText();
                    System.out.println("Found error via xpath: " + xp + " => " + errorText);
                    break;
                }
            } catch (Exception ignored) {}
        }

        System.out.println("Final error text: [" + errorText + "]");
        assert !errorText.isEmpty() : "No error message found after submitting wrong credentials";
        System.out.println("TEST PASSED");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
