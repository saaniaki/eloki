package eloki;

import eloki.provider.Config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "eloki")
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(Main.class);
        ctx.refresh();

        Config config = ctx.getBean(Config.class);
        List<ELoki> lokiList = new ArrayList<>();
        for (int i = 1; i <= config.getThreadsNumber(); i++) {
            ELoki loki = ctx.getBean(ELoki.class);
            lokiList.add(loki);
            Thread t = new Thread(loki);
            t.start();
        }
        System.out.println("Initialized " + config.getThreadsNumber() + " threads.");
        for (ELoki loki : lokiList)
            loki.setInitialized(true);
    }

}
