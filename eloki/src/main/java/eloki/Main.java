package eloki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class acts as an initializer of all eLoki threads and the application context.
 */
@ComponentScan(basePackages = "eloki")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Configuring application context.");

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        // Registering the main class causes Spring to look into the @ComponentScan and scan the whole eloki package
        ctx.register(Main.class);
        ctx.refresh();

        Config config = ctx.getBean(Config.class);
        List<ELoki> lokiList = new ArrayList<>();
        for (int i = 1; i <= config.getThreadsNumber(); i++) {
            // ELoki class have @Scope("prototype") and therefore a new instance is being injected evey time.
            ELoki loki = ctx.getBean(ELoki.class);
            lokiList.add(loki);
            Thread t = new Thread(loki);
            t.start();
        }
        logger.info("Initialized " + config.getThreadsNumber() + " threads.");
        // To make sure all threads start to work at the same time, they wait for the following signal.
        for (ELoki loki : lokiList)
            loki.setInitialized(true);
    }

}
