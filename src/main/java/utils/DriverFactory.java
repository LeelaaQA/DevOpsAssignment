package utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates the WebDriver for the chosen browser.
 *   execution=grid  -> RemoteWebDriver talking to the Selenium Grid (default)
 *   execution=local -> a browser installed on this PC (Selenium Manager finds the driver)
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(String browser) {
        String execution = ConfigReader.get("execution");
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver;
        if ("local".equalsIgnoreCase(execution)) {
            driver = createLocalDriver(browser, headless);
        } else {
            driver = createGridDriver(browser, headless);
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        try {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        } catch (WebDriverException ignored) {
            // Window size is a convenience only; continue if the browser refuses it.
        }
        return driver;
    }

    // ---------- Selenium Grid (RemoteWebDriver) ----------
    private static WebDriver createGridDriver(String browser, boolean headless) {
        String gridUrl = ConfigReader.get("gridUrl");
        Capabilities options = switch (browser) {
            case "chrome" -> chromeOptions(headless);
            case "firefox" -> firefoxOptions(headless);
            case "edge" -> edgeOptions(headless);
            default -> throw unsupported(browser);
        };

        try {
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid gridUrl: " + gridUrl, e);
        } catch (WebDriverException e) {
            throw new IllegalStateException("Could not start a '" + browser + "' session on the Selenium Grid at "
                    + gridUrl + ". Check that Docker Grid is running (docker compose -f docker/docker-compose.yml up -d)"
                    + " and that the Chrome, Firefox and Edge nodes are listed at " + gridUrl + "/ui", e);
        }
    }

    // ---------- Local browser ----------
    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw unsupported(browser);
        };
    }

    // ---------- Browser options ----------
    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--lang=en-US", "--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addPreference("intl.accept_languages", "en-US");
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--lang=en-US", "--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static IllegalArgumentException unsupported(String browser) {
        return new IllegalArgumentException("Unsupported browser '" + browser + "'. Use chrome, firefox or edge.");
    }
}
