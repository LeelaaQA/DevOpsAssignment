package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ConfigReader;

/**
 * Page object for the Amazon home page.
 */
public final class HomePage extends BasePage {

    // Two locators in one CSS list: the long-standing id, plus the field name as a fallback.
    private static final By SEARCH_BOX = By.cssSelector("#twotabsearchtextbox, input[name='field-keywords']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** Opens the Amazon site configured in baseUrl. */
    public HomePage open() {
        driver.get(ConfigReader.get("baseUrl"));
        dismissContinueShoppingIfPresent();
        dismissCookieBannerIfPresent();
        failIfBotCheck();
        return this;
    }

    public boolean isSearchBoxDisplayed() {
        try {
            return waitForVisible(SEARCH_BOX).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSearchBoxEnabled() {
        return waitForVisible(SEARCH_BOX).isEnabled();
    }

    /** Types the term in the search box, presses Enter and returns the results page. */
    public SearchResultsPage searchFor(String term) {
        WebElement box = waitForVisible(SEARCH_BOX);
        box.clear();
        box.sendKeys(term);
        box.sendKeys(Keys.ENTER);
        return new SearchResultsPage(driver);
    }
}
