package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConfigReader;
import com.airwallex.codechallenge.reader.jsonreader.JsonReader;
import com.airwallex.codechallenge.writer.jsonwriter.JsonWriter;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class App {
  private static final Logger logger = LogManager.getLogger("App");

  public static void main(String[] args) {
    Configurator.initialize(null, "src/resources/log4j2.properties");
    int nDataPoints = 0;
    long startTime = System.nanoTime();
    if (args.length == 0) {
      throw new IndexOutOfBoundsException("Supply input file as an argument.");
    }
    String file = args[0];

    try {
      // create reader, movingaverage queue, writer
      JsonReader reader = new JsonReader(file);
      ConfigReader config = new ConfigReader("/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/src/resources/config.properties");
      JsonWriter writer = new JsonWriter();

      // read parameters
      int windowSize = Integer.parseInt(config.get("moving_average_window"));
      MovingAverageQueue movingAverageQueue = new MovingAverageQueue(windowSize);
      Float pctChangeThreshold = Float.valueOf(config.get("pct_change_threshold"));
      String msg =
          String.format(
              "Program starting with parameters:\n\tInput File: %s\n\tOutput File: %s\n\tMoving Average Window Size: %d\n\tPercent Change Threshold: %.2f",
              reader.getPath(), writer.getPath(), windowSize, pctChangeThreshold);
      logger.debug(msg);

      // create output directory if it does not exist
      Path baseDir = Paths.get(System.getProperty("user.dir"));
      File directory = new File(String.valueOf(Paths.get(baseDir.toString(), "output")));
      if (! directory.exists()) directory.mkdir();
      assert directory.exists();

      while (reader.hasNextLine()) {
        nDataPoints++;
        Optional<CurrencyConversionRate> conversionRateMaybe = reader.readLine();
        CurrencyConversionRate conversionRate = conversionRateMaybe.get();
        movingAverageQueue.insert(conversionRate);

        Optional<Double> movingAverageMaybe = movingAverageQueue.getCurrentMovingAverage(conversionRate.getCurrencyPair());
        Double pctChange;
        Double movingAverage;

        if (movingAverageMaybe.isPresent()) {
          movingAverage = movingAverageMaybe.get();
          pctChange = (conversionRate.getRate() - movingAverage) / movingAverage;
        } else {
          movingAverage = conversionRate.getRate();
          pctChange = 0.0;
        }

        if (pctChange >= pctChangeThreshold) {
          // write to log
          logger.info(
              String.format(
                  "Significant rate change (>= %.2f) recorded:\n\tCurrency Pair  : %s\n\tAverage rate   : %.6f\n\tNew spot rate  : %.6f\n\tPercent change : %.2f%%",
                  pctChangeThreshold,
                  conversionRate.getCurrencyPair(),
                  movingAverage,
                  conversionRate.getRate(),
                  pctChange));

          // write to json
          JSONObject obj = new JSONObject();
          obj.put("currencyPair", conversionRate.getCurrencyPair());
          obj.put("timestamp", conversionRate.getTimestamp().getEpochSecond() + conversionRate.getTimestamp().getNano());
          obj.put("alert", "spotChange");
          writer.writeLine(obj);
        }
      }
      writer.close();
      double elapsedTime = (double) ((System.nanoTime() - startTime) / 1_000_000_000.0);
      logger.info(String.format("%d data points processed in %.6f seconds.", nDataPoints, elapsedTime));
      System.exit(0);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
