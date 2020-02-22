package eecs;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.logging.Level;

public class Main implements Runnable {

    private static final int THREADS = 50;
    private static final String TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com";

    public static void main(String[] args) {
        for (int i = 1; i <= THREADS; i++) {
            Thread t = new Thread(new Main());
            t.start();
        }
    }

    @Override
    public void run() {
        try {
            final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_68);

            webClient.setCssErrorHandler(new SilentCssErrorHandler());
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setTimeout(2000);
            webClient.waitForBackgroundJavaScript(2000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            webClient.getOptions().setUseInsecureSSL(true);
            LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                    "org.apache.commons.logging.impl.NoOpLog");
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("org.apache.commons.httpclient")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit.javascript.StrictErrorReporter")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit.javascript.host.ActiveXObject")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDocument")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit.html.HtmlScript")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("com.gargoylesoftware.htmlunit.javascript.host.WindowProxy")
                    .setLevel(Level.OFF);
            java.util.logging.Logger
                    .getLogger("org.apache")
                    .setLevel(Level.OFF);

            Random rand = new Random();
            HtmlPage page1 = webClient.getPage(TARGET + "/p" + (rand.nextInt(3) + 1) + ".html");
            System.out.println("Loading...");
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref("./index.html");
            Thread.sleep(1000); // To make the GA confused about online users
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            webClient.close();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
