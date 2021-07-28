package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConfigReader;
import com.airwallex.codechallenge.reader.jsonreader.JsonReader;
import com.airwallex.codechallenge.writer.jsonwriter.JsonWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
  public static void main(String[] args) {
    long startTime = System.nanoTime();
    String file = "/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/example/input1.jsonl";
    int nDataPoints = 0;
    try {
      JsonReader reader = new JsonReader(file);
      ConfigReader config = new ConfigReader("/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/src/resources/config.properties");
      MovingAverageQueue movingAverageQueue = new MovingAverageQueue(Integer.parseInt(config.get("moving_average_window")));
      JsonWriter writer = new JsonWriter();

      // create output directory if it does not exist
      Path baseDir = Paths.get(System.getProperty("user.dir"));
      File directory = new File(String.valueOf(Paths.get(baseDir.toString(), "output")));
      if (! directory.exists()) directory.mkdir();
      assert directory.exists();

      while (reader.hasNextLine()) {
        nDataPoints++;
        CurrencyConversionRate conversionRate = reader.readLine();
        Double movingAverage = movingAverageQueue.insert(conversionRate);
        System.out.println(conversionRate);
        System.out.println(movingAverage);
        Float pctChangeThreshold = Float.valueOf(config.get("pct_change_threshold"));
        System.out.println(pctChangeThreshold);

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
          obj.put("timestamp", conversionRate.getTimestamp());
          obj.put("alert", "spotChange");
          writer.writeLine(obj);
        }
      }
      writer.close();
      double elapsedTime = (double) ((System.nanoTime() - startTime) / 1_000_000_000);
      System.out.printf("%d data points processed in %.4f seconds\n", nDataPoints, elapsedTime);
      System.exit(0);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }


}
