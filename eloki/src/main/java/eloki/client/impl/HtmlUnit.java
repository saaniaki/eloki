package eloki.client.impl;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import eloki.Config;
import eloki.client.Client;
import eloki.provider.impl.AnchorProvider;
import eloki.provider.impl.BrowserProvider;
import eloki.provider.impl.KeywordProvider;
import eloki.provider.impl.path.PathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.URL;
import java.util.logging.Level;

//@Component
//@Scope("prototype")
public class HtmlUnit extends Client {

    private static final Logger logger = LoggerFactory.getLogger(HtmlUnit.class);

    private final AnchorProvider anchorProvider;
    private final KeywordProvider keywordProvider;
    private final PathProvider pathProvider;
    private final BrowserProvider browserProvider;
    private final Config config;

    private final WebClient webClient;

    public HtmlUnit(AnchorProvider anchorProvider, KeywordProvider keywordProvider,
                    PathProvider pathProvider, BrowserProvider browserProvider, Config config) {

        this.anchorProvider = anchorProvider;
        this.keywordProvider = keywordProvider;
        this.pathProvider = pathProvider;
        this.browserProvider = browserProvider;
        this.config = config;

        this.webClient = new WebClient(new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_68)
                .setUserAgent(this.browserProvider.provideRandomElement())
                .build());

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
            WebRequest request = new WebRequest(new URL(this.config.getTarget() + this.pathProvider.provideRandomElement().getPath()));
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
            logger.error("WARN: Could not use Tor, either open Tor browser or set the program to not use it.", socketException);
        } catch (Exception e) {
            logger.error("Request didn't go through, trying again...", e);
            this.browse();
        }
    }

}
