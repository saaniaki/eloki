package loki;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import loki.provider.BrowserProvider;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;

public class CustomWebClient extends WebClient {

    public CustomWebClient(ProxyConfig proxyConfig) {
        this();
        if (proxyConfig != null)
            this.getOptions().setProxyConfig(proxyConfig);
    }

    public CustomWebClient() {
        super(BrowserProvider.getRandomBrowser());

        this.setCssErrorHandler(new SilentCssErrorHandler());
        this.getCookieManager().setCookiesEnabled(true);
        this.getOptions().setJavaScriptEnabled(true);
        this.getOptions().setTimeout(2000);
        this.waitForBackgroundJavaScript(2000);
        this.getOptions().setThrowExceptionOnScriptError(false);
        this.getOptions().setPrintContentOnFailingStatusCode(false);
        this.getOptions().setUseInsecureSSL(true);
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
    }


}
