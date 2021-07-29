package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Scanner;

public class JsonReader extends ConversionRateReader {
  JSONParser parser = new JSONParser();
  private static final Logger logger = LogManager.getLogger();

  FileInputStream inputStream;
  Scanner sc;

  public JsonReader(String path) throws FileNotFoundException {
    Path outputPath = FileSystems.getDefault().getPath(path).normalize().toAbsolutePath();

    super.filename = outputPath.getFileName().toString();
    super.inputPath = outputPath;

    this.inputStream = new FileInputStream(super.inputPath.toString());
    this.sc = new Scanner(inputStream, "UTF-8");
  }

  public Boolean hasNextLine() {
    return sc.hasNextLine();
  }

  public String getPath() {
    return super.inputPath.toString();
  }

  @Override
  public Optional<CurrencyConversionRate> readLine() {
    if (!sc.hasNextLine()) {
      try {
        inputStream.close();
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
      if (sc != null) sc.close();
      return Optional.empty();
    }

    String line = sc.nextLine();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) this.parser.parse(line);
      String currencyPair = (String) jsonObject.get("currencyPair");

      long ts =
          (jsonObject.get("timestamp") instanceof Long)
              ? (long) jsonObject.get("timestamp")
              : ((Double) jsonObject.get("timestamp")).longValue();

      return Optional.of(
          new CurrencyConversionRate(Instant.ofEpochSecond(ts), currencyPair, (Double) jsonObject.get("rate"))
      );
    } catch (ParseException e) {
      logger.error(e.getMessage());
      return Optional.empty();
    }
  }
}
