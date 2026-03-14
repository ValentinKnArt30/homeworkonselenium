package ru.netology.autounit;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppOrderServiceTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        String browser = System.getenv().getOrDefault("BROWSER", "chrome").toLowerCase();

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");  // headless для CI
                firefoxOptions.addArguments("--no-sandbox");
                firefoxOptions.addArguments("--disable-dev-shm-usage");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void shouldTestOrderForm() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys("Иван Иванов");

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys("+79999999999");

        driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .click();

        driver.findElement(By.cssSelector("button.button"))
                .click();

        WebElement successMessage = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-test-id='order-success']")));

        String actualMessage = successMessage.getText();
        String expectedMessage = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";

        Assertions.assertEquals(expectedMessage, actualMessage.trim());
    }
}