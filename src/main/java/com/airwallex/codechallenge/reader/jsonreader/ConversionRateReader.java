package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.parser.ParseException;

import java.io.IOException;

abstract class ConversionRateReader {
  protected String path;
  public abstract CurrencyConversionRate readLine() throws ParseException, IOException;
}