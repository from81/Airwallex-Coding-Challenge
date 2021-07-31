package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConversionRateReader;
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
  private final JSONParser parser = new JSONParser();
  private static final Logger logger = LogManager.getLogger();

  FileInputStream inputStream;
  Scanner sc;

  public JsonReader(String path) {
    Path inputPath = FileSystems.getDefault().getPath(path).normalize().toAbsolutePath();
    super.filename = inputPath.getFileName().toString();
    super.inputPath = inputPath;

    if (!super.inputPath.getParent().toFile().exists()) super.inputPath.toFile().mkdirs();

    try {
      this.inputStream = new FileInputStream(super.inputPath.toString());
      this.sc = new Scanner(inputStream, "UTF-8");
      logger.debug("Reader created: " + super.inputPath.toFile().getAbsolutePath());
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
    }
  }

  public Boolean hasNextLine() {
    return sc.hasNextLine();
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
