package net.acesinc.data.json;
import net.acesinc.data.json.generator.JsonDataGenerator;
import net.acesinc.data.json.generator.log.EventLogger;
import net.acesinc.data.json.generator.log.FileLogger;
import net.acesinc.data.json.generator.log.Log4JLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class JsonDataGeneratorApp implements CommandLineRunner{
    private static final Logger log = LogManager.getLogger(JsonDataGenerator.class);

    @Autowired
    private JsonDataGenerator gen;

//    public static void main(String[] args) {
//        ConfigurableApplicationContext context = SpringApplication.run(JsonDataGeneratorApp.class, args);
//
//        final JsonDataGenerator gen = context.getBean(JsonDataGenerator.class);
//
//        final Thread mainThread = Thread.currentThread();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                log.info("Shutdown Hook Invoked. Shutting Down Loggers");
//                gen.stopRunning();
//                try {
//                    mainThread.join();
//                } catch (InterruptedException ex) {
//                    //oh well
//                }
//            }
//        });
//
//        gen.startRunning();
//        while (gen.isRunning()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                //wakie wakie!
//            }
//        }
//    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JsonDataGeneratorApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        gen.startRunning();
    }

    @Bean(name = "logger")
    private EventLogger getLog4JLogger() {
        return new Log4JLogger();
    }

    @Bean(name = "file")
    private EventLogger getFileLogger() {
        return new FileLogger();
    }

}
