package eloki;

import eloki.provider.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Interaction;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

@Component
@Scope("prototype")
public class Chrome extends Browser {

    private AnchorProvider anchorProvider;
    private KeywordProvider keywordProvider;
    private PathProvider pathProvider;
    private BrowserProvider browserProvider;
    private Config config;
    private MouseRecordingProvider mouseRecordingProvider;

    private WebDriver driver;

    public Chrome(AnchorProvider anchorProvider, KeywordProvider keywordProvider,
                  PathProvider pathProvider, BrowserProvider browserProvider, Config config,
                  MouseRecordingProvider mouseRecordingProvider) {
        this.anchorProvider = anchorProvider;
        this.keywordProvider = keywordProvider;
        this.pathProvider = pathProvider;
        this.browserProvider = browserProvider;
        this.config = config;
        this.mouseRecordingProvider = mouseRecordingProvider;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1200", "--headless"); //"--auto-open-devtools-for-tabs", "--headless"

        DesiredCapabilities caps = DesiredCapabilities.chrome();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        caps.setCapability("goog:loggingPrefs", logPrefs);
        options.setCapability(ChromeOptions.CAPABILITY, caps);

        this.driver = new ChromeDriver(options);
    }

    @Override
    public void browse() {
        try {
            // Navigate to Url
            this.driver.get(this.config.getTarget() + this.pathProvider.provideRandomElement());

            //Creating the JavascriptExecutor interface object by Type casting
            JavascriptExecutor jsEngine = (JavascriptExecutor) this.driver;
            jsEngine.executeScript("while(dataLayer[1][1] != '" + this.config.getGAToken() + "') {}; var x = true; x;");

            this.injectMouseLogger(jsEngine);

            Actions builder = new Actions(this.driver);
            PointerInput p = new PointerInput(PointerInput.Kind.MOUSE, "MyMouse");
            List<MouseCoordination> randomRecording = this.mouseRecordingProvider.provideRandomElement();

            float lastScrollX = 0, lastScrollY = 0;
            for (MouseCoordination mouseCoordination : randomRecording) {
                jsEngine.executeScript("window.scrollBy("
                        + (mouseCoordination.getScrollX() - lastScrollX)
                        + ", "
                        + (mouseCoordination.getScrollY() - lastScrollY)
                        + ");");
                lastScrollX = mouseCoordination.getScrollX();
                lastScrollY = mouseCoordination.getScrollY();
                Interaction interaction = p.createPointerMove(
                        Duration.ofMillis(1),
                        PointerInput.Origin.viewport(),
                        mouseCoordination.getMouseX(),
                        mouseCoordination.getMouseY()
                );
                builder.tick(interaction).perform();
//                System.out.println(mouseCoordination);
            }

            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logEntries) {
                System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                //do something useful with the data
            }

//            this.safeSleep(this.config.getHaltDelay() * ONE_SECOND);
        } finally {
            System.out.println("Done.");
            driver.quit();
        }

    }

    private void injectMouseLogger(JavascriptExecutor jsEngine) {
        jsEngine.executeScript("(function() {\n" +
                "    document.onmousemove = handleMouseMove;\n" +
                "    function handleMouseMove(event) {\n" +
                "        var eventDoc, doc, body;\n" +
                "\n" +
                "        event = event || window.event; // IE-ism\n" +
                "\n" +
                "        // If pageX/Y aren't available and clientX/Y are,\n" +
                "        // calculate pageX/Y - logic taken from jQuery.\n" +
                "        // (This is to support old IE)\n" +
                "        if (event.pageX == null && event.clientX != null) {\n" +
                "            eventDoc = (event.target && event.target.ownerDocument) || document;\n" +
                "            doc = eventDoc.documentElement;\n" +
                "            body = eventDoc.body;\n" +
                "\n" +
                "            event.pageX = event.clientX +\n" +
                "              (doc && doc.scrollLeft || body && body.scrollLeft || 0) -\n" +
                "              (doc && doc.clientLeft || body && body.clientLeft || 0);\n" +
                "            event.pageY = event.clientY +\n" +
                "              (doc && doc.scrollTop  || body && body.scrollTop  || 0) -\n" +
                "              (doc && doc.clientTop  || body && body.clientTop  || 0 );\n" +
                "        }\n" +
                "\n" +
                "        // Use event.pageX / event.pageY here\n" +
                "        console.log(event.pageX + \", \" + event.pageY);\n" +
                "    }\n" +
                "})();");
    }

}
