package loki;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import loki.provider.AnchorProvider;
import loki.provider.Config;
import loki.provider.KeywordProvider;
import loki.provider.PathProvider;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class Loki implements Runnable {

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * 1000;

    private Config config;
    private boolean initialized;

    public Loki(Config config) {
        this.config = config;
        this.initialized = false;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private int log2(int x) {
        return (int) Math.floor(Math.log(x) / Math.log(2));
    }

    private void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            System.out.println("Error while trying to suspend thread.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!this.initialized)
            this.safeSleep(1);

        int MI = (int) Math.pow(2, this.config.getMaxRequests() / (3.0 * this.config.getThreadsNumber())); // log2(x^3) * THREADS
        int min;
        int max;
        int rest;
        String restFormat;
        for (int i = 1; i <= MI; i++) {
            if (i == 1) {
                rest = ThreadLocalRandom.current().nextInt(this.config.getInitMinDelay() * ONE_MINUTE, this.config.getInitMaxDelay() * ONE_MINUTE + 1);
                restFormat = String.format("%.2f", Math.floor(rest / (double) ONE_MINUTE * 100) / 100);
                System.out.println("Starts in " + restFormat + " minuets");
            } else {
                rest = ThreadLocalRandom.current().nextInt(this.config.getMinDelay() * ONE_MINUTE, this.config.getMaxDelay() * ONE_MINUTE + 1);
                restFormat = String.format("%.2f", Math.floor(rest / (double) ONE_MINUTE * 100) / 100);
                max = this.log2((int) Math.pow(i, 3));
                min = (max * 3) / 4;
                int requestsInRound = ThreadLocalRandom.current().nextInt(min, max + 1);
                System.out.println("Firing " + requestsInRound + " requests...");
                for (int j = 1; j <= requestsInRound; j++)
                    this.browse(); // FIRE Request
                System.out.println(LocalTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm:ss"))
                        + "\tRequests made: " + requestsInRound
                        + "\tRest for: ~" + restFormat + " minuets");
            }

            this.safeSleep(rest);
        }
    }

    private synchronized void browse() {
        ProxyConfig proxyConfig = null;
        try {
            proxyConfig = new ProxyConfig("127.0.0.1", 9150, true);
        } catch (Exception e) {
            System.out.println("Could not use Tor, falling back to no-proxy.");
        }

        try {
            final WebClient webClient = new CustomWebClient(proxyConfig);
            String referrer = "https://www.google.com/search?q=" + KeywordProvider.getRandomKeyword();
            WebRequest request = new WebRequest(new URL(this.config.getTarget() + PathProvider.getRandomPath()));
            request.setAdditionalHeader("Referer", referrer);
            HtmlPage page1 = webClient.getPage(request);
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref(AnchorProvider.getRandomAnchor());
            this.safeSleep(this.config.getHaltDelay() * ONE_SECOND); // To make the GA confused about online users, 30 seconds
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            webClient.close();
        } catch (Exception e) {
            System.out.println("Request didn't go through, trying again...");
            this.browse();
        }
    }

}
