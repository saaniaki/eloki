package eloki;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Main configurations of the application.
 * This Configuration class scans all values provided in
 * the `eloki.properties` file and can be injected to all
 * other Spring beans.
 */
@Configuration
@PropertySource("classpath:eloki.properties")
public class Config {

    @Value("${target:http://www.eloki.tk}")
    private String target;

    @Value("${GAToken:UA-171069833-1}")
    private String GAToken;

    @Value("${threadsNumber:50}")
    private int threadsNumber;

    @Value("${maxRequests:1000}")
    private int maxRequests;

    @Value("${initMinDelay:1}")
    private short initMinDelay; // In Minutes

    @Value("${initMaxDelay:3}")
    private short initMaxDelay; // In Minutes

    @Value("${minDelay:5}")
    private short minDelay; // In Minutes

    @Value("${maxDelay:10}")
    private short maxDelay; // In Minutes

    @Value("${haltDelay:0}")
    private short haltDelay; // In Seconds (global)

    @Value("${useTor:false}")
    private boolean useTor;

    @Value("${geckoDriverPath:}")
    private String geckoDriverPath;

    @Value("${chromeDriverPath:}")
    private String chromeDriverPath;

    public Config() { }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getGAToken() {
        return GAToken;
    }

    public void setGAToken(String GAToken) {
        this.GAToken = GAToken;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public short getInitMinDelay() {
        return initMinDelay;
    }

    public void setInitMinDelay(short initMinDelay) {
        this.initMinDelay = initMinDelay;
    }

    public short getInitMaxDelay() {
        return initMaxDelay;
    }

    public void setInitMaxDelay(short initMaxDelay) {
        this.initMaxDelay = initMaxDelay;
    }

    public short getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(short minDelay) {
        this.minDelay = minDelay;
    }

    public short getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(short maxDelay) {
        this.maxDelay = maxDelay;
    }

    public short getHaltDelay() {
        return haltDelay;
    }

    public void setHaltDelay(short haltDelay) {
        this.haltDelay = haltDelay;
    }

    public boolean useTor() {
        return useTor;
    }

    public void setUseTor(boolean useTor) {
        this.useTor = useTor;
    }

    public String getGeckoDriverPath() {
        return geckoDriverPath;
    }

    public void setGeckoDriverPath(String geckoDriverPath) {
        this.geckoDriverPath = geckoDriverPath;
    }

    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public void setChromeDriverPath(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }
}
