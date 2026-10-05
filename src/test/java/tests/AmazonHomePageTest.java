package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;

/**
 * Safe public-page checks on the Amazon home page (no login, no purchase).
 */
public class AmazonHomePageTest extends BaseTest {

	@Test(description = "Amazon home page opens successfully")
	public void homePageTitleContainsAmazon() {
		HomePage home = new HomePage(driver).open();

		String title = home.getTitle();
		String url = home.getCurrentUrl();

		System.out.println("[" + browserName + "] Page title: " + title);
		System.out.println("[" + browserName + "] Current URL: " + url);

		Assert.assertTrue(url.toLowerCase().contains("amazon."), "Expected Amazon URL but was: " + url);
	}

	@Test(description = "The browser is on an Amazon web address")
	public void urlBelongsToAmazon() {
		HomePage home = new HomePage(driver).open();
		String url = home.getCurrentUrl();
		System.out.println("[" + browserName + "] Current URL: " + url);
		Assert.assertTrue(url.contains("amazon."), "Expected an amazon address but was: " + url);
	}

	@Test(description = "The search box is visible and enabled")
	public void searchBoxIsAvailable() {
		HomePage home = new HomePage(driver).open();
		Assert.assertTrue(home.isSearchBoxDisplayed(), "Search box should be displayed");
		Assert.assertTrue(home.isSearchBoxEnabled(), "Search box should be enabled");
	}
}
