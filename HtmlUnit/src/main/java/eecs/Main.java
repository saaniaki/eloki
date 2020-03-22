package eecs;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class Main implements Runnable {

    public static final String TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com";
    private static final int NUMBER_OF_THREADS = 50; // 50
    private static final int MAX_REQUESTS = 1000; // 1000
    private static final int MI = (int) Math.pow(2, MAX_REQUESTS / (3.0 * NUMBER_OF_THREADS)); // log2(x^3) * THREADS
    private static final double MILLI = 60 * 1000;
    private static volatile boolean INITIALIZED = false;

    public static void main(String[] args) {
        ProxyProvider.INSTANCE.runAgainIfNotCurrentlyRunning();

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

    private synchronized void browse() {
        ProxyConfig proxyConfig = null;
        try {
            proxyConfig = ProxyProvider.pickRandomProxy();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Falling back to no-proxy.");
        }

        try {
            final WebClient webClient = new CustomWebClient(proxyConfig);
            String referrer = "https://www.google.com/search?q=" + KeywordProvider.getRandomKeyword();
            WebRequest request = new WebRequest(new URL(TARGET + PathProvider.getRandomPath()));
            request.setAdditionalHeader("Referer", referrer);
            HtmlPage page1 = webClient.getPage(request);
            // blocks till GA tag is fully initialized
            page1.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            HtmlAnchor htmlAnchor = page1.getAnchorByHref("./index.html");
            Thread.sleep(30000); // To make the GA confused about online users, 30 seconds
            HtmlPage page2 = htmlAnchor.click();
            page2.executeJavaScript("while(dataLayer[1][1] != 'UA-157513426-1') {}; var x = true; x;");
            webClient.close();
        } catch (Exception e) {
            System.out.println("Trying again!");
//            e.printStackTrace();
            ProxyProvider.removeProxy(proxyConfig);
            this.browse();
        }
    }

    private int log2(int x) {
        return (int) Math.floor(Math.log(x) / Math.log(2));
    }
}
