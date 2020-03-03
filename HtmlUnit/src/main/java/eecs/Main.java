package eecs;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Main implements Runnable {

    private static final int NUMBER_OF_THREADS = 50; // 50
    private static final int MAX_REQUESTS = 1000; // 1000
    private static final int MI = (int) Math.pow(2, MAX_REQUESTS / (3.0 * NUMBER_OF_THREADS)); // log2(x^3) * THREADS
    private static final double MILLI = 60 * 1000;
    private static volatile boolean INITIALIZED = false;
    private static final String TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com";
    private static final List<String> AGENTS = new LinkedList<>();
    private static List<ProxyConfig> PROXIES = new LinkedList<>();
    private static final int NUMBER_OF_PROXIES = 10; // 25

    /**
     * http://spys.one/free-proxy-list/CA/
     * for (let item of document.querySelectorAll("tr[onmouseover] > td:first-child > font")) console.log(item.innerText);
     */
    private static void registerProxies() {
        File proxyList = new File(
                Objects.requireNonNull(Main.class.getClassLoader().getResource("proxies")).getFile()
        );

        try (FileReader reader = new FileReader(proxyList);
             BufferedReader br = new BufferedReader(reader)) {
            String socket;
            while ((socket = br.readLine()) != null)
                PROXIES.add(new ProxyConfig(socket.split(":")[0], Integer.parseInt(socket.split(":")[1])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ProxyConfig> validated = new LinkedList<>();
        for (ProxyConfig proxy : PROXIES) {
            final WebClient webClient = prepareWebClient(proxy);
            try {
                webClient.getPage("https://google.com");
                validated.add(proxy);
                System.out.println(proxy.getProxyHost() + ":" + proxy.getProxyPort() + " is valid.");
            } catch (Exception e) {
                System.out.println(proxy.getProxyHost() + ":" + proxy.getProxyPort() + " is not valid.");
            }
            if (validated.size() == NUMBER_OF_PROXIES)
                break;
        }

        PROXIES = validated;
    }

    private static void registerAgents() throws IOException {
        File dir = new File(
                Objects.requireNonNull(Main.class.getClassLoader().getResource("agents")).getFile()
        );
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File agentList : directoryListing) {
                if (agentList == null) continue;
                try (FileReader reader = new FileReader(agentList);
                     BufferedReader br = new BufferedReader(reader)) {
                    String agent;
                    while ((agent = br.readLine()) != null)
                        AGENTS.add(agent);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Registering Agents...");
        registerAgents();

        System.out.println("Registering Proxies...");
        registerProxies();

        System.out.println("Initializing Threads...");
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
        String restFormat;
        for (int i = 1; i <= MI; i++) {
            if (i == 1) {
//                rest = 0;
                rest = ThreadLocalRandom.current().nextInt(60000, 2 * 60000 + 1); // one to three minutes
                restFormat = String.format("%.2f", Math.floor(rest / MILLI * 100) / 100);
                System.out.println("Starts in " + restFormat + " minuets");
            } else {
                rest = ThreadLocalRandom.current().nextInt(5 * 60000, 10 * 60000 + 1); // five to ten minutes
                restFormat = String.format("%.2f", Math.floor(rest / MILLI * 100) / 100);
                max = this.log2((int) Math.pow(i, 3));
                min = (max * 3) / 4;
                int requestsInRound = ThreadLocalRandom.current().nextInt(min, max + 1);
                System.out.println("Firing " + requestsInRound + " requests...");
                for (int j = 1; j <= requestsInRound; j++)
                    this.browse(); // FIRE
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
            final WebClient webClient = prepareWebClient(pickRandomProxy());
            Random rand = new Random();

            String url = TARGET + "/p" + (rand.nextInt(3) + 1) + ".html";
            String referrer = "http://www.google.com/search?q=eecs+4480";
            WebRequest request = new WebRequest(new URL(url));
            request.setAdditionalHeader("Referer", referrer);
            HtmlPage page1 = webClient.getPage(request);

//            HtmlPage page1 = webClient.getPage(TARGET + "/p" + (rand.nextInt(3) + 1) + ".html");
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref("./index.html");
            Thread.sleep(30000); // To make the GA confused about online users, 30 seconds
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            webClient.close();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Trying again!");
            this.browse();
        }
    }

    private static WebClient prepareWebClient(ProxyConfig proxyConfig) {
        final WebClient webClient = new WebClient(pickRandomAgent());

        if (proxyConfig != null)
            webClient.getOptions().setProxyConfig(proxyConfig);

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

    private static BrowserVersion pickRandomAgent() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, AGENTS.size());
        return new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_68)
                .setUserAgent(AGENTS.get(randomAgentIndex))
                .build();
    }

    private static ProxyConfig pickRandomProxy() {
        return PROXIES.get(ThreadLocalRandom.current().nextInt(0, PROXIES.size()));
    }

    private int log2(int x) {
        return (int) Math.floor(Math.log(x) / Math.log(2));
    }
}
