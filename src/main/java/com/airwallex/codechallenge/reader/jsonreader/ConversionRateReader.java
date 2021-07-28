package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

abstract class ConversionRateReader {
  protected String path;
  public abstract Optional<CurrencyConversionRate> readLine() throws ParseException, IOException;
}