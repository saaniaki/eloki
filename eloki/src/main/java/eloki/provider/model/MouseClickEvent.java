package eloki.provider.model;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

public class MouseClickEvent implements MouseEvent {

    @Override
    public void executeJs(JavascriptExecutor javascriptExecutor) {

    }

    @Override
    public Actions buildActions(PointerInput pointerInput, Actions actions) {
        return actions.click();
    }
}
