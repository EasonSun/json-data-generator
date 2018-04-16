/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.acesinc.data.json.generator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.acesinc.data.json.generator.config.SimulationConfig;
import net.acesinc.data.json.generator.config.JSONConfigReader;
import net.acesinc.data.json.generator.log.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author andrewserff
 */
@Component
public class JsonDataGenerator {

    private static final Logger log = LogManager.getLogger(JsonDataGenerator.class);

    private SimulationRunner simRunner;

    @Value("${dmConfig}")
    private String simConfigFile;

    @PostConstruct
    public void init() throws Exception{
        try {
            log.debug("Creating Simulation Runner using Simulation Config [ " + simConfigFile + " ]");
            SimulationConfig simConfig = getSimConfig();
            List<EventLogger> loggers = new ArrayList<>();
            // inject this simConfig as well
            for (Map<String, Object> elProps : simConfig.getProducers()) {
                // consider injection and bind by type
                String elType = (String) elProps.get("type");
                log.info("Adding" + elType + "Producer");
                switch (elType) {
                    case "logger": {
                        // should I keep inside each get method?
                        log.info("Adding Log4JLogger Producer");
                        loggers.add(new Log4JLogger());
                        break;
                    }
                    case "file": {
                        log.info("Adding File Logger with properties: " + elProps);
                        loggers.add(new FileLogger(elProps));
                        break;
                    }
                    case "kafka": {
                        log.info("Adding Kafka Producer with properties: " + elProps);
                        loggers.add(new KafkaLogger(elProps));
                        break;
                    }
                    case "tranquility": {
                        log.info("Adding Tranqulity Logger with properties: " + elProps);
                        loggers.add(new TranquilityLogger(elProps));
                        break;
                    }
                    case "nats": {
                        log.info("Adding NATS Logger with properties: " + elProps);
                        loggers.add(new NatsLogger(elProps));
                        break;
                    }
                    case "http-post": {
                        log.info("Adding HTTP Post Logger with properties: " + elProps);
                        try {
                            loggers.add(new HttpPostLogger(elProps));
                        } catch (NoSuchAlgorithmException ex) {
                            log.error("http-post Logger unable to initialize", ex);
                        }
                        break;
                    }
                    case "mqtt": {
                        log.info("Adding MQTT Logger with properties: " + elProps);
                        try {
                            loggers.add(new MqttLogger(elProps));
                        } catch (MqttException ex) {
                            log.error("mqtt Logger unable to initialize", ex);
                        }
                        break;
                    }
                    case "iothub": {
                        log.info("Adding Azure IoT Hub Logger with properties: " + elProps);
                        try {
                            loggers.add(new AzureIoTHubLogger(elProps));
                        } catch (URISyntaxException ex) {
                            log.error("Azure IoT Hub Logger unable to initialize", ex);
                        }
                        break;
                    }
                }
            }
            if (loggers.isEmpty()) {
                throw new IllegalArgumentException("You must configure at least one Producer in the Simulation Config");
            }
            simRunner = new SimulationRunner(simConfig, loggers);
        } catch (IOException ex) {
            log.error("Error getting Simulation Config [ " + simConfigFile + " ]", ex);
            throw new IOException(ex);
        }
    }

    public void startRunning() {
        simRunner.startSimulation();
    }

    public void stopRunning() {
        simRunner.stopSimulation();
    }

    private SimulationConfig getSimConfig() throws IOException {
        return JSONConfigReader.readConfig(this.getClass().getClassLoader().getResourceAsStream(simConfigFile), SimulationConfig.class);
    }

    public boolean isRunning() {
        return simRunner.isRunning();
    }

//    public static void main(String[] args) {
//        String simConfig = "companyInfoConfig.json";
////        String simConfig = "defaultSimConfig.json";
//        if (args.length > 0) {
//            simConfig = args[0];
//            log.info("Overriding Simulation Config file from command line to use [ " + simConfig + " ]");
//        }
//
//        final JsonDataGenerator gen = new JsonDataGenerator(simConfig);
//
//        final Thread mainThread = Thread.currentThread();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                log.info("Shutdown Hook Invoked.  Shutting Down Loggers");
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
//
//    }

    /**
     * @return the simConfigFile
     */
    public String getSimConfigFile() {
        return simConfigFile;
    }

    /**
     * @param simConfigFile the simConfigFile to set
     */
    public void setSimConfigFile(String simConfigFile) {
        this.simConfigFile = simConfigFile;
    }

}