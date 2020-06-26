package eloki.client.impl;

import eloki.Config;
import eloki.client.SeleniumClient;
import eloki.provider.impl.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
@Scope("prototype")
public class FireFox extends SeleniumClient {

    private static final Logger logger = LoggerFactory.getLogger(FireFox.class);

    public FireFox(AnchorProvider anchorProvider, KeywordProvider keywordProvider, PathProvider pathProvider,
                   BrowserProvider browserProvider, Config config, MouseRecordingProvider mouseRecordingProvider) {
        super(anchorProvider, keywordProvider, pathProvider, browserProvider, config, mouseRecordingProvider);
    }

    @Override
    protected WebDriver setUpWebDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless"); // "--auto-open-devtools-for-tabs"
        options.addArguments("--width=1920");
        options.addArguments("--height=1200");
        options.addPreference("general.useragent.override", this.browserProvider.provideRandomElement());

        if (this.config.useTor()) {
            options.addPreference("network.proxy.socks", "127.0.0.1");
            options.addPreference("network.proxy.socks_port", 9150);
            options.addPreference("network.proxy.type", 1);
        }

        if (logger.isDebugEnabled())
            options.addPreference("devtools.console.stdout.content", true);

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

        return new FirefoxDriver(options);
    }

}
