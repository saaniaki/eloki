package eloki.provider;

import org.springframework.stereotype.Component;

@Component
public class Config {

    private String target;
    private String GAToken;
    private int threadsNumber;
    private int maxRequests;
    private short initMinDelay; // In Minutes
    private short initMaxDelay; // In Minutes
    private short minDelay; // In Minutes
    private short maxDelay; // In Minutes
    private short haltDelay; // In Seconds
    private boolean useTor;

    // Defaults
    public Config() {
        this.target = "http://www.eloki.tk";
        this.GAToken = "UA-157513426-1";
        this.threadsNumber = 2;
        this.maxRequests = 10;
        this.initMinDelay = 0;
        this.initMaxDelay = 1;
        this.minDelay = 5;
        this.maxDelay = 10;
        this.haltDelay = 30;
        this.useTor = false;
    }

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
}
