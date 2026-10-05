package tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ConfigReader;
import utils.DriverFactory;

/**
 * Base class for all tests: starts one browser per test class and always closes it.
 */
public class BaseTest {

    protected WebDriver driver;
    protected String browserName;

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("") String browserFromTestng) {
        browserName = ConfigReader.getBrowser(browserFromTestng);
        driver = DriverFactory.createDriver(browserName);

        System.out.println("==================================================");
        System.out.println("Requested browser : " + browserName);
        System.out.println("Execution mode    : " + ConfigReader.get("execution"));
        if (driver instanceof RemoteWebDriver remote) {
            Capabilities caps = remote.getCapabilities();
            System.out.println("Grid URL          : " + ConfigReader.get("gridUrl"));
            System.out.println("Session id        : " + remote.getSessionId());
            System.out.println("Actual browser    : " + caps.getBrowserName() + " " + caps.getBrowserVersion());
            System.out.println("Platform          : " + caps.getPlatformName());
        }
        System.out.println("==================================================");
    }

    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() != ITestResult.FAILURE || driver == null) {
            return;
        }
        try {
            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);
            Path file = folder.resolve(browserName + "_" + result.getMethod().getMethodName() + ".png");
            Files.write(file, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            System.out.println("Failure screenshot saved: " + file);
        } catch (Exception e) {
            System.out.println("Could not save failure screenshot: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
