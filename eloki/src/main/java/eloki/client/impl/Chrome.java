package eloki.client.impl;

import eloki.Config;
import eloki.client.SeleniumClient;
import eloki.provider.impl.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Level;

//@Component
//@Scope("prototype")
public class Chrome extends SeleniumClient {

    private static final Logger logger = LoggerFactory.getLogger(Chrome.class);

    public Chrome(PathProvider pathProvider, BrowserProvider browserProvider,
                  Config config, MouseRecordingProvider mouseRecordingProvider) {
        super(pathProvider, browserProvider, config, mouseRecordingProvider);
    }

    @Override
    protected WebDriver setUpWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // "--auto-open-devtools-for-tabs"
        options.addArguments("--window-size=1920,1200");
        options.addArguments("--user-agent=" + this.browserProvider.provideRandomElement());

        if (this.config.useTor())
            options.addArguments("--proxy-server=socks5://127.0.0.1:9150");

        if (logger.isDebugEnabled()) {
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            capabilities.setCapability("goog:loggingPrefs", logPrefs);
            options.setCapability(ChromeOptions.CAPABILITY, capabilities);
        }

        return new ChromeDriver(options);
    }

    @Override
    protected void extendBrowsing() {
        if (logger.isDebugEnabled()) {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logEntries)
                logger.debug(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }
    }
}
