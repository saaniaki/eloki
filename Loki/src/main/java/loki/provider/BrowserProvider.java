package loki.provider;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BrowserProvider {

    private static final String AGENTS_FOLDER_NAME = "agents";
    private static final URL AGENT_FOLDER = BrowserProvider.class.getClassLoader().getResource(AGENTS_FOLDER_NAME);
    private static final List<String> AGENTS = new LinkedList<>();

    static {
        System.out.println("Registering Browsers...");
        File dir = new File(Objects.requireNonNull(AGENT_FOLDER).getFile());
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File agentList : directoryListing) {
                if (agentList == null) continue;
                try (FileReader reader = new FileReader(agentList);
                     BufferedReader br = new BufferedReader(reader)) {
                    String agent;
                    while ((agent = br.readLine()) != null)
                        AGENTS.add(agent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Registered " + AGENTS.size() + " Browsers.");
    }

    public static BrowserVersion getRandomBrowser() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, AGENTS.size());
        return new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_68)
                .setUserAgent(AGENTS.get(randomAgentIndex))
                .build();
    }

    private BrowserProvider() {
    }

}
