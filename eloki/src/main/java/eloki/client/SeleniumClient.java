package eloki.client;

import eloki.Config;
import eloki.provider.impl.*;
import eloki.provider.model.MouseEvent;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class has been created as an abstract class to avoid duplication and ease of extension.
 * `SeleniumClient` uses `org.openqa.selenium.WebDriver` via composition and implements `Client`
 * interface. To mark a `Client` implementation to be used, it should be annotated with `@Component`
 * and `@Scope("prototype")`. Only one inherited class of `Client` should have these annotation at
 * a time or Spring DI would not be able to determine which bean is in use at runtime.
 */
public abstract class SeleniumClient extends Client {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumClient.class);

    protected PathProvider pathProvider;
    protected BrowserProvider browserProvider;
    protected Config config;
    protected MouseRecordingProvider mouseRecordingProvider;

    protected WebDriver driver;

    public SeleniumClient(PathProvider pathProvider, BrowserProvider browserProvider,
                          Config config, MouseRecordingProvider mouseRecordingProvider) {
        this.pathProvider = pathProvider;
        this.browserProvider = browserProvider;
        this.config = config;
        this.mouseRecordingProvider = mouseRecordingProvider;

        this.driver = this.setUpWebDriver();
    }

    protected abstract WebDriver setUpWebDriver();

    protected void extendBrowsing() {
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
            PointerInput pointerInput = new PointerInput(PointerInput.Kind.MOUSE, "MyMouse");
            List<MouseEvent> randomRecording = this.mouseRecordingProvider.provideRandomElement();

            for (MouseEvent mouseEvent : randomRecording) {
                mouseEvent.executeJs(jsEngine);
                mouseEvent.buildActions(pointerInput, builder);
                builder.perform();
            }

            this.extendBrowsing();
            this.safeSleep(this.config.getHaltDelay() * ONE_SECOND);
        } finally {
            logger.debug("Done.");
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
