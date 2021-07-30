package com.airwallex.codechallenge.monitor.alert;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.writer.jsonwriter.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpotChangeAlert extends Alert implements ExecutableAlert {
  private static final Logger logger = LogManager.getLogger();
  private static final Path baseDir = Paths.get(System.getProperty("user.dir"));
  private final Float pctChangeThreshold;
  private final CurrencyConversionRate conversionRate;
  private final double movingAverage;
  private final double pctChange;
  private final JsonWriter writer;

  public SpotChangeAlert(
          CurrencyConversionRate conversionRate,
          double pctChange,
          double movingAverage,
          Float pctChangeThreshold) throws IOException {
    this.pctChangeThreshold = pctChangeThreshold;
    this.conversionRate = conversionRate;
    this.movingAverage = movingAverage;
    this.pctChange = pctChange;

    File output = new File(Paths.get(baseDir.toString(), "output/SpotChangeAlert/").toString());
    if (output.mkdirs()) logger.debug("Directory created: " + output.getAbsolutePath());
    this.writer = new JsonWriter(output.toString());
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject toJson() {
    JSONObject obj = new JSONObject();
    obj.put("currencyPair", this.conversionRate.getCurrencyPair());
    obj.put(
        "timestamp",
        this.conversionRate.getTimestamp().getEpochSecond() + this.conversionRate.getTimestamp().getNano()
    );
    obj.put("alert", "spotChange");
    return obj;
  }

  @Override
  public String toString() {
    String msg = "SpotChangeAlert";
    return msg + String.format(
            ": Significant rate change (>= %.2f) recorded:\n\tCurrency Pair  : %s\n\tAverage rate   : %.6f\n\tNew spot rate  : %.6f\n\tPercent change : %.2f%%",
            this.pctChangeThreshold,
            this.conversionRate.getCurrencyPair(),
            this.movingAverage,
            this.conversionRate.getRate(),
            this.pctChange);
  }

  @Override
  public void execute() {
    this.writer.writeLine(this.toJson());
    this.writer.close();
  }
}
