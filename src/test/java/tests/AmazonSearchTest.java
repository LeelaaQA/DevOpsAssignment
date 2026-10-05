package tests;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;

/**
 * Searches for a product on the public Amazon site and checks the results page.
 */
public class AmazonSearchTest extends BaseTest {

    private SearchResultsPage searchForConfiguredTerm() {
        return new HomePage(driver).open().searchFor(ConfigReader.get("searchTerm"));
    }

    @Test(description = "Searching shows a results page with products")
    public void searchShowsResultsPage() {
        String term = ConfigReader.get("searchTerm");
        SearchResultsPage results = searchForConfiguredTerm();

        System.out.println("[" + browserName + "] Results URL: " + results.getCurrentUrl());
        System.out.println("[" + browserName + "] Result cards found: " + results.getResultCount());

        Assert.assertTrue(results.getCurrentUrl().toLowerCase().contains(term.toLowerCase()),
                "Results URL should contain the search term '" + term + "'");
        Assert.assertTrue(results.getResultCount() > 0, "At least one product result should be shown");
    }

    @Test(description = "Top search results have readable product titles")
    public void searchResultsHaveProductTitles() {
        SearchResultsPage results = searchForConfiguredTerm();
        List<String> titles = results.getFirstProductTitles(3);

        titles.forEach(t -> System.out.println("[" + browserName + "] Product: " + t));
        Assert.assertFalse(titles.isEmpty(), "At least one product title should be readable");
    }
}
