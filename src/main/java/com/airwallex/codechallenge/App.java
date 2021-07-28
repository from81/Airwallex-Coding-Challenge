package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.JsonReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class App {
  public static void main(String[] args) {

    String file = "/Users/Kai/Dropbox/Documents/Code/airwallex-code-challenge/example/input1.jsonl";
    try {
      JsonReader reader = new JsonReader(file);
      CurrencyConversionRate conversionRate = reader.readLine();
      System.out.println(conversionRate);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
