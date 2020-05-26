package loki;

import loki.provider.Config;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        System.out.println("Initializing Threads...");
        List<Loki> lokiList = new ArrayList<>();
        for (int i = 1; i <= config.getThreadsNumber(); i++) {
            Loki loki = new Loki(config);
            lokiList.add(loki);
            Thread t = new Thread(loki);
            t.start();
        }
        System.out.println("Initialized " + config.getThreadsNumber() + " threads.");
        for (Loki loki : lokiList)
            loki.setInitialized(true);
    }
}
