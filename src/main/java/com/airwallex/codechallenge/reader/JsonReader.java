package com.airwallex.codechallenge.reader;


import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Scanner;
import java.util.stream.Stream;

abstract class ConversionRateReader {
  String path;
  public abstract CurrencyConversionRate readLine() throws ParseException, IOException;
}

public class JsonReader extends ConversionRateReader {
  JSONParser parser = new JSONParser();
  Stream<String> stream;

  //  String path;
  FileInputStream inputStream;
  Scanner sc;

  public JsonReader(String path) throws FileNotFoundException {
    this.path = path;
    try {
      this.inputStream = new FileInputStream(path);
      this.sc = new Scanner(inputStream, "UTF-8");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public CurrencyConversionRate readLine() throws ParseException, IOException {
    if (!sc.hasNextLine()) {
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
      if (sc != null) sc.close();
      return null;
    }

    String line = sc.nextLine();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) this.parser.parse(line);
      String currencyPair = (String) jsonObject.get("currencyPair");
      Double rate = (Double) jsonObject.get("rate");
      long ts = ((Double) jsonObject.get("timestamp")).longValue();
      Instant instant = Instant.ofEpochSecond(ts);

      CurrencyConversionRate conversionRate = new CurrencyConversionRate(instant, currencyPair, rate);
      return conversionRate;
    } catch (ParseException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
