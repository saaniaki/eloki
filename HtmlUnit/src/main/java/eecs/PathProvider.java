package eecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PathProvider {

    private static final String PATHS_FILE_NAME = "paths";
    private static final URL PATHS_FILE = PathProvider.class.getClassLoader().getResource(PATHS_FILE_NAME);
    private static final List<String> PATHS = new LinkedList<>();

    static {
        System.out.println("Registering Paths...");
        File list = new File(Objects.requireNonNull(PATHS_FILE).getFile());
        try (FileReader reader = new FileReader(list);
             BufferedReader br = new BufferedReader(reader)) {
            String keyword;
            while ((keyword = br.readLine()) != null)
                PATHS.add(keyword.replace(" ", "+"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Registered " + PATHS.size() + " Paths.");
    }

    public static String getRandomPath() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, PATHS.size());
        return PATHS.get(randomAgentIndex);
    }

    private PathProvider() {
    }

}
