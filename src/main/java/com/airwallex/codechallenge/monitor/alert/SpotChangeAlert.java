package com.airwallex.codechallenge.monitor.alert;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.json.simple.JSONObject;

public class SpotChangeAlert extends Alert {
  private final Float pctChangeThreshold;
  private final CurrencyConversionRate conversionRate;
  private final double movingAverage;
  private final double pctChange;

  public SpotChangeAlert(
      CurrencyConversionRate conversionRate,
      double pctChange,
      double movingAverage,
      Float pctChangeThreshold) {
    this.pctChangeThreshold = pctChangeThreshold;
    this.conversionRate = conversionRate;
    this.movingAverage = movingAverage;
    this.pctChange = pctChange;
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
}
