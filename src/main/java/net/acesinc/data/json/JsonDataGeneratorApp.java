package net.acesinc.data.json;
import net.acesinc.data.json.generator.JsonDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class JsonDataGeneratorApp {
    private static final Logger log = LogManager.getLogger(JsonDataGenerator.class);

    @Autowired
    static JsonDataGenerator gen;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JsonDataGeneratorApp.class, args);
        final JsonDataGenerator gen = context.getBean(JsonDataGenerator.class);

        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Shutdown Hook Invoked. Shutting Down Loggers");
                gen.stopRunning();
                try {
                    mainThread.join();
                } catch (InterruptedException ex) {
                    //oh well
                }
            }
        });

        gen.startRunning();
        while (gen.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //wakie wakie!
            }
        }
    }
}
