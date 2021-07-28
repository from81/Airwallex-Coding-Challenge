package com.airwallex.codechallenge.reader.jsonreader;


import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class JsonReader extends ConversionRateReader {
  JSONParser parser = new JSONParser();
  Stream<String> stream;

  FileInputStream inputStream;
  Scanner sc;

  public JsonReader(String path) throws FileNotFoundException {
    super.path = path;
    try {
      this.inputStream = new FileInputStream(super.path);
      this.sc = new Scanner(inputStream, "UTF-8");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public Boolean hasNextLine() {
    return sc.hasNextLine();
  }

  public String getPath() { return super.path; }

  @Override
  public Optional<CurrencyConversionRate> readLine() throws ParseException, IOException {
    if (!sc.hasNextLine()) {
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
      if (sc != null) sc.close();
      return Optional.empty();
    }

    String line = sc.nextLine();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) this.parser.parse(line);
      String currencyPair = (String) jsonObject.get("currencyPair");
      Double rate = (Double) jsonObject.get("rate");

      long ts;
      if (jsonObject.get("timestamp") instanceof Long) {
        ts = (long) jsonObject.get("timestamp");
      } else {
        ts = ((Double) jsonObject.get("timestamp")).longValue();
      }
      Instant instant = Instant.ofEpochSecond(ts);

      CurrencyConversionRate conversionRate = new CurrencyConversionRate(instant, currencyPair, rate);
      return Optional.of(conversionRate);
    } catch (ParseException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
