package eloki.provider.impl.path;

public class PathInfo {

    private String path;
    private short haltDelay = 0; // In seconds
    private String jsFinishStatement;

    public PathInfo(String path) {
        this.path = path;
    }

    public PathInfo(String path, short haltDelay) {
        this.path = path;
        this.haltDelay = haltDelay;
    }

    public PathInfo(String path, short haltDelay, String jsFinishStatement) {
        this.path = path;
        this.haltDelay = haltDelay;
        this.jsFinishStatement = jsFinishStatement;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public short getHaltDelay() {
        return haltDelay;
    }

    public void setHaltDelay(short haltDelay) {
        this.haltDelay = haltDelay;
    }

    public boolean isJsFinishStatementAvailable() {
        return jsFinishStatement != null && !jsFinishStatement.isEmpty();
    }

    public String getJsFinishStatement() {
        return jsFinishStatement;
    }

    public void setJsFinishStatement(String jsFinishStatement) {
        this.jsFinishStatement = jsFinishStatement;
    }
}
