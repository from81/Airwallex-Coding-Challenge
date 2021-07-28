package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConfigReader;
import com.airwallex.codechallenge.reader.jsonreader.JsonReader;
import com.airwallex.codechallenge.writer.jsonwriter.JsonWriter;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    System.setProperty("logfilename", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

    long startTime = System.nanoTime();
    if (args.length == 0) {
      throw new IndexOutOfBoundsException("Supply input file as an argument.");
    }
    logger.warn("hello");
    // String file = "/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/example/input1.jsonl";
    // String file = "/Users/Kai/Dropbox/Documents/Code/airwallex-exchange-rate-monitor/input/10min_single_curr.jsonl";
    String file = args[0];
    int nDataPoints = 0;
    try {
      // create reader, movingaverage queue, writer
      JsonReader reader = new JsonReader(file);
      ConfigReader config = new ConfigReader("/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/src/resources/config.properties");
      JsonWriter writer = new JsonWriter();

      // read parameters
      int windowSize = Integer.parseInt(config.get("moving_average_window"));
      MovingAverageQueue movingAverageQueue = new MovingAverageQueue(windowSize);
      Float pctChangeThreshold = Float.valueOf(config.get("pct_change_threshold"));
      MapMessage msg = new MapMessage();
      msg.put("Message", "Program starting with parameters:");
      msg.put("Input File", reader.getPath());
      msg.put("Output File", writer.getPath());
      msg.put("Moving Average Window Size", String.valueOf(windowSize));
      msg.put("Percent Change Threshold", String.format("%.2f", pctChangeThreshold));
      logger.info(msg);

      // create output directory if it does not exist
      Path baseDir = Paths.get(System.getProperty("user.dir"));
      File directory = new File(String.valueOf(Paths.get(baseDir.toString(), "output")));
      if (! directory.exists()) directory.mkdir();
      assert directory.exists();

      while (reader.hasNextLine()) {
        nDataPoints++;
        CurrencyConversionRate conversionRate = reader.readLine();
        Double movingAverage = movingAverageQueue.insert(conversionRate);

        movingAverage = movingAverageQueue.getCurrentMovingAverage(conversionRate.getCurrencyPair());
        Double pctChange = (conversionRate.getRate() - movingAverage) / movingAverage;

        if (pctChange >= pctChangeThreshold) {
          // write to log
          System.out.printf("Percent change beyond acceptance threshold (%.2f) detected: %.2f\n",
                  pctChangeThreshold,
                  pctChange);

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
      System.out.printf("%d data points processed in %.6f seconds\n", nDataPoints, elapsedTime);
      System.exit(0);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
