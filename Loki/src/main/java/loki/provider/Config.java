package loki.provider;

public class Config {

    private String target;
    private int threadsNumber;
    private int maxRequests;
    private short initMinDelay; // In Minutes
    private short initMaxDelay; // In Minutes
    private short minDelay; // In Minutes
    private short maxDelay; // In Minutes
    private short haltDelay; // In Seconds

    // Defaults
    public Config() {
        this.target = "http://www.eloki.tk";
        this.threadsNumber = 50;
        this.maxRequests = 1000;
        this.initMinDelay = 1;
        this.initMaxDelay = 3;
        this.minDelay = 5;
        this.maxDelay = 10;
        this.haltDelay = 30;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
}
