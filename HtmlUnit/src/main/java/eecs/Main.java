package eecs;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Main implements Runnable {

    private static final int NUMBER_OF_THREADS = 50;
    private static final int MAX_REQUESTS = 1000;
    private static final int MI = (int) Math.pow(2, MAX_REQUESTS / (3.0 * NUMBER_OF_THREADS)); // log2(x^3) * THREADS
    private static final double MILLI = 60 * 1000;
    private static volatile boolean INITIALIZED = false;
    private static final String TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com";

    public static void main(String[] args) {
        System.out.println("Initializing...");
        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            Thread t = new Thread(new Main());
            t.start();
        }
        System.out.println("Initialized " + NUMBER_OF_THREADS + " threads.");
        INITIALIZED = true;
    }

    @Override
    public void run() {
        while (!INITIALIZED) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int min;
        int max;
        int rest;
        for (int i = 1; i <= MI; i++) {
            rest = ThreadLocalRandom.current().nextInt(5 * 60000, 10 * 60000 + 1); // five to ten minutes
            String restFormat = String.format("%.2f", Math.floor(rest / MILLI * 100) / 100);
            if (i == 1)
                System.out.println("Starts in " + restFormat + " minuets");
            else {
                max = this.log2((int) Math.pow(i, 3));
                min = (max * 3) / 4;
                int requestsInRound = ThreadLocalRandom.current().nextInt(min, max + 1);
                for (int j = 1; j <= requestsInRound; j++)
                    browse(); // FIRE
                System.out.println(LocalTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm:ss"))
                        + "\tRequests made: " + requestsInRound
                        + "\tRest for: ~" + restFormat + " minuets");
            }

            try {
                Thread.sleep(rest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void browse() {
        try {
            final WebClient webClient = this.prepareWebClient();
            Random rand = new Random();
            HtmlPage page1 = webClient.getPage(TARGET + "/p" + (rand.nextInt(3) + 1) + ".html");
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref("./index.html");
            Thread.sleep(1000); // To make the GA confused about online users
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            webClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebClient prepareWebClient() {
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

        return webClient;
    }

    public int log2(int x) {
        return (int) Math.floor(Math.log(x) / Math.log(2));
    }
}
