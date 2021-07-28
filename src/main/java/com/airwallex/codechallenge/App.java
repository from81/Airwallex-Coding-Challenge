package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConfigReader;
import com.airwallex.codechallenge.reader.jsonreader.JsonReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class App {
  public static void main(String[] args) {

    String file = "/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/example/input1.jsonl";
    try {
      JsonReader reader = new JsonReader(file);
      ConfigReader config = new ConfigReader("/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/src/resources/config.properties");
      MovingAverageQueue queue = new MovingAverageQueue(Integer.parseInt(config.get("movingAverageWindow")));

      CurrencyConversionRate conversionRate = reader.readLine();
      System.out.println(conversionRate);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
