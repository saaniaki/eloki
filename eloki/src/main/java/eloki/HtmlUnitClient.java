package eloki;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import eloki.provider.*;

import java.net.SocketException;
import java.net.URL;
import java.util.logging.Level;

//@Component
//@Scope("prototype")
public class HtmlUnitClient extends Browser {

    private AnchorProvider anchorProvider;
    private KeywordProvider keywordProvider;
    private PathProvider pathProvider;
    private BrowserProvider browserProvider;
    private Config config;

    private WebClient webClient;

    public HtmlUnitClient(AnchorProvider anchorProvider, KeywordProvider keywordProvider,
                          PathProvider pathProvider, BrowserProvider browserProvider, Config config) {

        this.webClient = new WebClient(new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_68)
                .setUserAgent(browserProvider.provideRandomElement())
                .build());

        this.anchorProvider = anchorProvider;
        this.keywordProvider = keywordProvider;
        this.pathProvider = pathProvider;
        this.browserProvider = browserProvider;
        this.config = config;

        if (this.config.useTor())
            this.webClient.getOptions().setProxyConfig(new ProxyConfig("127.0.0.1", 9150, true));

        this.webClient.setCssErrorHandler(new SilentCssErrorHandler());
        this.webClient.getCookieManager().setCookiesEnabled(true);
        this.webClient.getOptions().setJavaScriptEnabled(true);
        this.webClient.getOptions().setTimeout(2000);
        this.webClient.waitForBackgroundJavaScript(2000);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        this.webClient.getOptions().setUseInsecureSSL(true);
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
//                "org.apache.commons.logging.impl.NoOpLog");
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
    }

    @Override
    public void browse() {
        try {
            String referrer = "https://www.google.com/search?q=" + this.keywordProvider.provideRandomElement();
            WebRequest request = new WebRequest(new URL(this.config.getTarget() + this.pathProvider.provideRandomElement()));
            request.setAdditionalHeader("Referer", referrer);
            HtmlPage page1 = this.webClient.getPage(request);
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != '" + this.config.getGAToken() + "') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref(this.anchorProvider.provideRandomElement());
            this.safeSleep(this.config.getHaltDelay() * ONE_SECOND); // To make the GA confused about online users, 30 seconds
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != '" + this.config.getGAToken() + "') {}; var x = true; x;");
            this.webClient.close();
        } catch (SocketException socketException) {
            System.out.println("WARN: Could not use Tor, either open Tor browser or set the program to not use it.");
        } catch (Exception e) {
            System.out.println("Request didn't go through, trying again...");
//            e.printStackTrace();
            this.browse();
        }
    }


}
