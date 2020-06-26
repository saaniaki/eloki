package eloki.provider.model;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

import java.time.Duration;

public class MouseMovement implements MouseEvent {

    private short mouseX;
    private short mouseY;
    private float scrollX;
    private float scrollY;

    public MouseMovement(short mouseX, short mouseY, float scrollX, float scrollY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public short getMouseX() {
        return mouseX;
    }

    public void setMouseX(short mouseX) {
        this.mouseX = mouseX;
    }

    public short getMouseY() {
        return mouseY;
    }

    public void setMouseY(short mouseY) {
        this.mouseY = mouseY;
    }

    public float getScrollX() {
        return scrollX;
    }

    public void setScrollX(float scrollX) {
        this.scrollX = scrollX;
    }

    public float getScrollY() {
        return scrollY;
    }

    public void setScrollY(float scrollY) {
        this.scrollY = scrollY;
    }

    @Override
    public String toString() {
        return "MouseCoordination{" +
                "mouseX=" + mouseX +
                ", mouseY=" + mouseY +
                ", scrollX=" + scrollX +
                ", scrollY=" + scrollY +
                '}';
    }

    @Override
    public void executeJs(JavascriptExecutor javascriptExecutor) {
        javascriptExecutor.executeScript("window.scrollTo("
                + (this.getScrollX())
                + ", "
                + (this.getScrollY())
                + ");");
    }

    @Override
    public Actions buildActions(PointerInput pointerInput, Actions actions) {
        return actions.tick(pointerInput.createPointerMove(
                Duration.ofMillis(1),
                PointerInput.Origin.viewport(),
                this.getMouseX(),
                this.getMouseY()
        ));
    }
}
