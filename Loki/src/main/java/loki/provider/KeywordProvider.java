package loki.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class KeywordProvider {

    private static final String KEYWORDS_FOLDER_NAME = "keywords";
    private static final URL KEYWORDS_FOLDER = KeywordProvider.class.getClassLoader().getResource(KEYWORDS_FOLDER_NAME);
    private static final List<String> KEYWORDS = new LinkedList<>();

    static {
        System.out.println("Registering Keywords...");
        File list = new File(Objects.requireNonNull(KEYWORDS_FOLDER).getFile());
        try (FileReader reader = new FileReader(list);
             BufferedReader br = new BufferedReader(reader)) {
            String keyword;
            while ((keyword = br.readLine()) != null)
                KEYWORDS.add(keyword.replace(" ", "+"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Registered " + KEYWORDS.size() + " Keywords.");
    }

    public static String getRandomKeyword() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, KEYWORDS.size());
        return KEYWORDS.get(randomAgentIndex);
    }

    private KeywordProvider() {
    }

}
