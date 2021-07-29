package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public abstract class ConversionRateReader {
  protected String filename;
  protected Path inputPath;

  public abstract Optional<CurrencyConversionRate> readLine() throws ParseException, IOException;
}
