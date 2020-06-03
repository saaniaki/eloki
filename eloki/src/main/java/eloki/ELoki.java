package eloki;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import eloki.provider.AnchorProvider;
import eloki.provider.Config;
import eloki.provider.KeywordProvider;
import eloki.provider.PathProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Scope("prototype")
public class ELoki implements Runnable, ApplicationContextAware {

    int ONE_MINUTE = 60 * 1000;

    private Config config;
    private boolean initialized;

    private AnchorProvider anchorProvider;
    private KeywordProvider keywordProvider;
    private PathProvider pathProvider;

    private ApplicationContext applicationContext;

    public ELoki(Config config, AnchorProvider anchorProvider,
                 KeywordProvider keywordProvider, PathProvider pathProvider) {
        this.config = config;
        this.anchorProvider = anchorProvider;
        this.keywordProvider = keywordProvider;
        this.pathProvider = pathProvider;
        this.initialized = false;
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

//            this.safeSleep(rest);
        }
    }

    private synchronized void browse() {
        final Browser browser = applicationContext.getBean(Browser.class);
        browser.browse();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
