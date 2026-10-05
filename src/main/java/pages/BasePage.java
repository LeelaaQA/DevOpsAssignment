package pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

/**
 * Common helpers shared by all page objects (explicit waits, bot-check detection).
 */
public abstract class BasePage {

    /** Amazon's robot-check (CAPTCHA) page contains this field/form. */
    protected static final By BOT_CHECK_FIELD =
            By.cssSelector("input#captchacharacters, form[action*='validateCaptcha']");

    private static final By CONTINUE_SHOPPING_BUTTON =
            By.xpath("//button[normalize-space()='Continue shopping' or @alt='Continue shopping']");

    private static final By COOKIE_ACCEPT_BUTTON = By.cssSelector("#sp-cc-accept");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")));
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Clicks Amazon's plain "Continue shopping" interstitial button if it is shown. */
    protected void dismissContinueShoppingIfPresent() {
        try {
            if (isPresent(CONTINUE_SHOPPING_BUTTON)) {
                driver.findElement(CONTINUE_SHOPPING_BUTTON).click();
            }
        } catch (NoSuchElementException ignored) {
            // The button disappeared between the check and the click - nothing to do.
        }
    }

    /** Accepts the cookie banner if the region shows one. */
    protected void dismissCookieBannerIfPresent() {
        try {
            if (isPresent(COOKIE_ACCEPT_BUTTON)) {
                driver.findElement(COOKIE_ACCEPT_BUTTON).click();
            }
        } catch (NoSuchElementException ignored) {
            // No banner - nothing to do.
        }
    }

    /**
     * Stops the test with a clear message if Amazon shows a robot check.
     * The CAPTCHA is NOT solved or bypassed; the test is reported as failed honestly.
     */
    protected void failIfBotCheck() {
        String title = driver.getTitle() == null ? "" : driver.getTitle().toLowerCase();
        if (isPresent(BOT_CHECK_FIELD) || title.contains("robot check")) {
            throw new IllegalStateException(
                    "Amazon is showing a robot check (CAPTCHA) to this automated browser, so the test cannot "
                            + "continue. It is NOT bypassed. See docs/execution-guide.md, section 'Amazon limitations'.");
        }
    }
}
