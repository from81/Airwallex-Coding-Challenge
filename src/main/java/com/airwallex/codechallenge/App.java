package com.airwallex.codechallenge;

import com.airwallex.codechallenge.monitor.MovingAverageMonitor;
import com.airwallex.codechallenge.reader.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
  private static final Logger logger = LogManager.getLogger("App");
  private static final Path baseDir = Paths.get(System.getProperty("user.dir"));
  private static final ExecutorService executorService = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    // initialize log4j2 config
    Configurator.initialize(null, "src/resources/log4j2.properties");

    if (args.length == 0) {
      logger.error("Supply input file as an argument.");
      System.exit(1);
    }

    try {
      // open config file
      Path configDir = Paths.get(System.getProperty("user.dir"), "/src/resources/config.properties");
      ConfigReader config = new ConfigReader(configDir.toString());

      // create output directory if it does not exist
      File directory = new File(Paths.get(baseDir.toString(), "output").toString());
      if (directory.mkdir()) logger.debug("Directory created: " + directory.getAbsolutePath());

      // add initialized monitors here
      executorService.execute(new MovingAverageMonitor(args[0], config));
      executorService.execute(new MovingAverageMonitor(args[1], config));

      executorService.shutdown();
      boolean finished = executorService.awaitTermination(10, TimeUnit.MINUTES);
      System.exit((finished) ? 0 : 1);
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
