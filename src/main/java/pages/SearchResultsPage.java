package pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for the Amazon search results page.
 */
public final class SearchResultsPage extends BasePage {

    // Amazon marks every product card with this attribute; it is much more stable than CSS class names.
    private static final By RESULT_CARDS = By.cssSelector("div[data-component-type='s-search-result']");
    private static final By RESULT_TITLES = By.cssSelector("div[data-component-type='s-search-result'] h2");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        waitForResultsOrBotCheck();
    }

    private void waitForResultsOrBotCheck() {
        // Wait for EITHER product cards OR the robot-check page, then react honestly.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(RESULT_CARDS),
                ExpectedConditions.presenceOfElementLocated(BOT_CHECK_FIELD)));
        failIfBotCheck();
    }

    public int getResultCount() {
        return driver.findElements(RESULT_CARDS).size();
    }

    /** Returns up to 'max' non-empty product titles from the top of the results. */
    public List<String> getFirstProductTitles(int max) {
        List<String> titles = new ArrayList<>();
        for (WebElement heading : driver.findElements(RESULT_TITLES)) {
            String text = heading.getText();
            if (text == null || text.isBlank()) {
                text = heading.getAttribute("textContent");
            }
            if (text != null && !text.isBlank()) {
                titles.add(text.trim());
            }
            if (titles.size() >= max) {
                break;
            }
        }
        return titles;
    }
}
