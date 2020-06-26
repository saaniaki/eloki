package eloki.provider.model;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

public interface MouseEvent {
    void executeJs(JavascriptExecutor javascriptExecutor);

    Actions buildActions(PointerInput pointerInput, Actions actions);
}
