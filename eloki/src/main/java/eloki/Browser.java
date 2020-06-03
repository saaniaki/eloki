package eloki;

public abstract class Browser {
    int ONE_SECOND = 1000;

    protected void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            System.out.println("Error while trying to suspend thread.");
            e.printStackTrace();
        }
    }

    abstract void browse();

}
