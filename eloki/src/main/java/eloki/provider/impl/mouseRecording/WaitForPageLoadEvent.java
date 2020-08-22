package eloki.provider.impl.mouseRecording;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitForPageLoadEvent implements MouseEvent {
    @Override
    public void executeJs(WebDriver driver) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 180);
        webDriverWait.until((ExpectedCondition<Boolean>) wd -> {
            assert wd != null;
            return ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete");
        });
    }

    @Override
    public void buildActions(PointerInput pointerInput, Actions actions) {

    }
}
