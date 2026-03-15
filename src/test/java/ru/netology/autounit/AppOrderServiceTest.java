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

    @Test
    public void shouldShowErrorForInvalidName() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys("Ivan Ivanov 1");

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys("+79999999999");

        driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .click();

        driver.findElement(By.cssSelector("button.button"))
                .click();

        WebElement nameError = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")));

        Assertions.assertEquals("Имя и Фамилия указаные неверно. " +
                        "Допустимы только русские буквы, пробелы и дефисы.",
                nameError.getText().trim());
    }

    @Test
    public void shouldShowErrorForInvalidPhone() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys("Иван Иванов");

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys("+7999999-9-8");

        driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .click();

        driver.findElement(By.cssSelector("button.button"))
                .click();

        WebElement nameError = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")));

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                nameError.getText().trim());
    }

    @Test
    public void shouldShowErrorForInvalidPhoneNull() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys("Иван Иванов");

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys("");

        driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .click();

        driver.findElement(By.cssSelector("button.button"))
                .click();

        WebElement nameError = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")));

        Assertions.assertEquals("Поле обязательно для заполнения",
                nameError.getText().trim());
    }

    @Test
    public void shouldShowErrorForInvalidNameNull() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys("");

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys("+79999999999");

        driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .click();

        driver.findElement(By.cssSelector("button.button"))
                .click();

        WebElement nameError = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")));

        Assertions.assertEquals("Поле обязательно для заполнения",
                nameError.getText().trim());
    }
}