package loki.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class AnchorProvider {

    private static final String ANCHOR_FILE_NAME = "anchors";
    private static final URL ANCHOR_FILE = AnchorProvider.class.getClassLoader().getResource(ANCHOR_FILE_NAME);
    private static final List<String> ANCHORS = new LinkedList<>();

    static {
        System.out.println("Registering Anchors...");
        File list = new File(Objects.requireNonNull(ANCHOR_FILE).getFile());
        try (FileReader reader = new FileReader(list);
             BufferedReader br = new BufferedReader(reader)) {
            String anchor;
            while ((anchor = br.readLine()) != null)
                ANCHORS.add(anchor.replace(" ", "+"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Registered " + ANCHORS.size() + " Anchors.");
    }

    public static String getRandomAnchor() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, ANCHORS.size());
        return ANCHORS.get(randomAgentIndex);
    }

    private AnchorProvider() {
    }

}
