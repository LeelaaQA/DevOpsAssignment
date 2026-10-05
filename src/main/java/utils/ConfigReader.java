package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings. Priority: command line (-Dkey=value) > config.properties.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties was not found in src/main/resources");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String fromCommandLine = System.getProperty(key);
        if (fromCommandLine != null && !fromCommandLine.isBlank()) {
            return fromCommandLine.trim();
        }
        String fromFile = PROPS.getProperty(key);
        if (fromFile == null || fromFile.isBlank()) {
            throw new IllegalStateException("Missing configuration key: " + key);
        }
        return fromFile.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Browser priority: -Dbrowser (command line) > TestNG "browser" parameter > config.properties.
     */
    public static String getBrowser(String testngParameter) {
        String fromCommandLine = System.getProperty("browser");
        if (fromCommandLine != null && !fromCommandLine.isBlank()) {
            return fromCommandLine.trim().toLowerCase();
        }
        if (testngParameter != null && !testngParameter.isBlank()) {
            return testngParameter.trim().toLowerCase();
        }
        return get("browser").toLowerCase();
    }
}
