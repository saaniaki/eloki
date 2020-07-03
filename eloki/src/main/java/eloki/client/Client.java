package eloki.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * eLoki relies a `Client` implementation to make the requests and fake the
 * browsing behaviour. `Client` interface exposes the `browse` method will
 * be called by eLoki threads.
 */
public abstract class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public int ONE_SECOND = 1000;

    protected void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            logger.error("Error while trying to suspend thread.", e);
        }
    }

    public abstract void browse();

}
