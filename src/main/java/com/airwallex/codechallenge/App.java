package com.airwallex.codechallenge;

import com.airwallex.codechallenge.monitor.Monitor;
import com.airwallex.codechallenge.monitor.MovingAverageMonitor;
import com.airwallex.codechallenge.reader.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class App {
  private static final Logger logger = LogManager.getLogger("App");
  private static final Path baseDir = Paths.get(System.getProperty("user.dir"));
  private static final ArrayList<Monitor> MONITORS = new ArrayList<>();

  public static void main(String[] args) {
    // initialize log4j2 config
    Configurator.initialize(null, "src/resources/log4j2.properties");

    if (args.length == 0) {
      throw new IndexOutOfBoundsException("Supply input file as an argument.");
    }
    try {
      // open config file
      Path configDir = Paths.get(System.getProperty("user.dir"), "/src/resources/config.properties");
      ConfigReader config = new ConfigReader(configDir.toString());

      // create output directory if it does not exist
      File directory = new File(Paths.get(baseDir.toString(), "output").toString());
      if (directory.mkdir()) logger.debug("Directory created: " + directory.getAbsolutePath());

      // add data stores here
      MONITORS.add(new MovingAverageMonitor(args[0], config));

       (MONITORS).parallelStream().forEach(Monitor::run);
      // MONITORS.forEach(Monitor::run);

      System.exit(0);

    } catch (IOException e) {
      logger.error(e.getMessage());
      System.exit(1);
    }
  }
}
