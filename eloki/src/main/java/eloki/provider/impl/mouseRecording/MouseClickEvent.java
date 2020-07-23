package eloki.provider.impl.mouseRecording;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

public class MouseClickEvent implements MouseEvent {

    @Override
    public void executeJs(JavascriptExecutor javascriptExecutor) {

    }

    @Override
    public Actions buildActions(PointerInput pointerInput, Actions actions) {
        actions.tick(pointerInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        actions.tick(pointerInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return actions;
    }
}
