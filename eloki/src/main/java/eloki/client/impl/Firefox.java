package eloki.client.impl;

import eloki.Config;
import eloki.client.SeleniumClient;
import eloki.provider.impl.*;
import eloki.provider.impl.mouseRecording.MouseRecordingProvider;
import eloki.provider.impl.path.PathProvider;
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
public final class Firefox extends SeleniumClient {

    private static final Logger logger = LoggerFactory.getLogger(Firefox.class);

    public Firefox(PathProvider pathProvider, BrowserProvider browserProvider,
                   Config config, MouseRecordingProvider mouseRecordingProvider) {
        super(pathProvider, browserProvider, config, mouseRecordingProvider);
    }

    @Override
    protected WebDriver setUpWebDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--width=" + this.config.getWindowWidth());
        options.addArguments("--height=1200" + this.config.getWindowHeight());
//        String agent = this.browserProvider.provideRandomElement();
//        options.addPreference("general.useragent.override", agent);

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
