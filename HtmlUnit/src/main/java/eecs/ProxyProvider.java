package eecs;

import com.gargoylesoftware.htmlunit.ProxyConfig;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProxyProvider implements Runnable {

    //    private static final String PROXY_RESOURCE = "http://spys.one/free-proxy-list/CA/";
//    private static final String PROXY_RESOURCE = "http://spys.one/en/free-proxy-list/";
//    private static final String PROXY_RESOURCE = "https://free-proxy-list.net/";
    private static final String PROXY_RESOURCE = "http://free-proxy.cz/en/proxylist/country/KH/http/uptime/all";
    private static final List<ProxyConfig> IMPORTED_PROXIES = new LinkedList<>();
    private static final Set<ProxyConfig> VALIDATED_PROXIES = new HashSet<>();
    private static volatile boolean INITIALIZED = false;
    public static final ProxyProvider INSTANCE = new ProxyProvider();
    private static Thread THREAD_INSTANCE = new Thread(INSTANCE);

    @Override
    public void run() {
        System.out.println("Fetching Proxies...");
        CustomWebClient webClient = new CustomWebClient();
//        HtmlPage page;
//        try {
//
////            URL url = new URL(PROXY_RESOURCE);
////            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);
////            requestSettings.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
////            requestSettings.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
////            requestSettings.setRequestBody("xx0=186b7d117ad6b7a16851fa7de9fca08c&xpp=5&xf1=0&xf2=0&xf4=0&xf5=0");
////            page = webClient.getPage(requestSettings);
//
//            page = webClient.getPage(PROXY_RESOURCE);
////            ScriptResult scriptResult = page.executeJavaScript("var result = []; var list = document.querySelectorAll('tr[onmouseover] > td:first-child > font'); for(var i = 0; i< list.length; i++) {result.push(list[i].innerText);} result;");
////            ScriptResult scriptResult = page.executeJavaScript("var result = []; var ip = document.querySelectorAll('tr[role] > td:first-child'); " +
////                    "var port = document.querySelectorAll('tr[role] > td:nth-child(2)'); " +
////                    "for(var i = 0; i< ip.length; i++) {result.push(ip[i].innerText + ':' + port[i].innerText);} result;");
//
//            ScriptResult scriptResult = page.executeJavaScript("var result = []; " +
//                    "var ip = document.querySelectorAll('#proxy_list tr > td.left:nth-child(1)'); " +
//                    "var port = document.querySelectorAll('#proxy_list tr > td:nth-child(2)'); " +
//                    "for(var i = 0; i < ip.length; i++) {result.push(ip[i].innerText + ':' + port[i].innerText);} " +
//                    "result;");
//            NativeArray nativeArray = (NativeArray) scriptResult.getJavaScriptResult();
//            for (Object object : nativeArray) {
////                String socket = (String) object;
//                String socket = ((ConsString) object).toString();
//                IMPORTED_PROXIES.add(new ProxyConfig(socket.split(":")[0], Integer.parseInt(socket.split(":")[1])));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /* FIle reading */
        File proxyList = new File(
                Objects.requireNonNull(Main.class.getClassLoader().getResource("proxies")).getFile()
        );

        try (FileReader reader = new FileReader(proxyList);
             BufferedReader br = new BufferedReader(reader)) {
            String socket;
            while ((socket = br.readLine()) != null)
                IMPORTED_PROXIES.add(new ProxyConfig(socket.split(":")[0], Integer.parseInt(socket.split(":")[1])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File validList = new File("proxies.valid");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(validList, true))) {


            System.out.println("Validating Proxies...");
            for (ProxyConfig proxy : IMPORTED_PROXIES) {
                try {
                    webClient = new CustomWebClient(proxy);
//                String url = TARGET + "/p1.html";
                    webClient.getPage("https://google.com");
                    synchronized (VALIDATED_PROXIES) {
                        VALIDATED_PROXIES.add(proxy);
                    }
                    System.out.println(proxy.getProxyHost() + ":" + proxy.getProxyPort() + " is valid.");
                    bw.write(proxy.getProxyHost() + ":" + proxy.getProxyPort());
                    bw.newLine();
                } catch (Exception e) {
                    System.out.println(proxy.getProxyHost() + ":" + proxy.getProxyPort() + " is not valid.");
                }
                webClient.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        IMPORTED_PROXIES.clear();
        INITIALIZED = true;
        System.out.println("Proxies has been initialized.");
    }

    public synchronized void runAgainIfNotCurrentlyRunning() {
        if (!THREAD_INSTANCE.isAlive()) {
            System.out.println("Registering Proxies...");
            INITIALIZED = false;
            THREAD_INSTANCE = new Thread(INSTANCE);
            THREAD_INSTANCE.start();
        }

    }

    public synchronized static ProxyConfig pickRandomProxy() throws Exception {
//        while (VALIDATED_PROXIES.size() < 5) {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (VALIDATED_PROXIES.size() == 0 && INITIALIZED)
//                throw new Exception("Couldn't find a valid proxy!");
//        }

        if(!VALIDATED_PROXIES.isEmpty()) {
            int randIndex = ThreadLocalRandom.current().nextInt(0, VALIDATED_PROXIES.size());
            int i = 0;
            for (ProxyConfig proxyConfig : VALIDATED_PROXIES) {
                if (i == randIndex)
                    return proxyConfig;
                i++;
            }
        }
        throw new Exception("Couldn't return a random valid proxy!");

//        return VALIDATED_PROXIES.get(ThreadLocalRandom.current().nextInt(0, VALIDATED_PROXIES.size()));
    }

    public synchronized static void removeProxy(ProxyConfig proxyConfig) {
        if (proxyConfig == null) return;
        VALIDATED_PROXIES.remove(proxyConfig);
        System.out.println(VALIDATED_PROXIES.size());
        if (VALIDATED_PROXIES.size() < 10)
            INSTANCE.runAgainIfNotCurrentlyRunning();
    }

    ProxyProvider() {
    }
}
