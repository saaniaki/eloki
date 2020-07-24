package eloki;

import eloki.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@Scope("prototype")
public final class ELoki implements Runnable, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ELoki.class);

    int ONE_MINUTE = 60 * 1000;

    private final Config config;
    private boolean initialized;

    private ApplicationContext applicationContext;

    public ELoki(Config config) {
        this.config = config;
        this.initialized = false;

        // Registering the path of the Selenium Web Drivers
        System.setProperty("webdriver.chrome.driver", this.config.getChromeDriverPath());
        System.setProperty("webdriver.gecko.driver", this.config.getGeckoDriverPath());
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
            logger.error("Error while trying to suspend eLoki thread.", e);
        }
    }


    private synchronized void browse() {
        final Client client = this.applicationContext.getBean(Client.class);
        client.browse();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * TODO needs cleanup
     * This method is in charge of distributing the requests over time. The goals here
     * is to make a long-term increasing pattern which fluctuates in short-term periods.
     */
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
                logger.info("Starts in " + restFormat + " minuets");
            } else {
                rest = ThreadLocalRandom.current().nextInt(this.config.getMinDelay() * ONE_MINUTE, this.config.getMaxDelay() * ONE_MINUTE + 1);
                restFormat = String.format("%.2f", Math.floor(rest / (double) ONE_MINUTE * 100) / 100);
                max = this.log2((int) Math.pow(i, 3));
                min = (max * 3) / 4;
                int requestsInRound = ThreadLocalRandom.current().nextInt(min, max + 1);
                logger.info("Firing " + requestsInRound + " requests...");
                for (int j = 1; j <= requestsInRound; j++)
                    this.browse(); // FIRE Request
                logger.info("Requests made: " + requestsInRound + "\tRest for: ~" + restFormat + " minuets");
            }

            this.safeSleep(rest);
        }
    }

}
