package eloki.provider.impl.mouseRecording;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

/**
 * To replicate realistic Mouse Events, both Selenium Actions and
 * Selenium JavascriptExecutor are needed since some features are
 * only supported by one of them.
 */
public interface MouseEvent {
    void executeJs(JavascriptExecutor javascriptExecutor);

    Actions buildActions(PointerInput pointerInput, Actions actions);
}
