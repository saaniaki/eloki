package eloki.provider.impl.mouseRecording;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

public class MouseRightClickEvent implements MouseEvent {

    @Override
    public void executeJs(WebDriver driver) {

    }

    @Override
    public void buildActions(PointerInput pointerInput, Actions actions) {
        actions.tick(pointerInput.createPointerDown(PointerInput.MouseButton.RIGHT.asArg()));
        actions.tick(pointerInput.createPointerUp(PointerInput.MouseButton.RIGHT.asArg()));
    }
}
