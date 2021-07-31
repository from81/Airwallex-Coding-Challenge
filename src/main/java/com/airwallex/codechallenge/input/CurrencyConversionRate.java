package com.airwallex.codechallenge.input;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class CurrencyConversionRate implements Comparable<CurrencyConversionRate> {
  private final Instant timestamp;
  private final String currencyPair;
  private final Double rate;

  public CurrencyConversionRate(Instant timestamp, String currencyPair, Double rate) {
    this.timestamp = timestamp;
    this.currencyPair = currencyPair;
    this.rate = rate;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getCurrencyPair() {
    return currencyPair;
  }

  public Double getRate() {
    return rate;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    CurrencyConversionRate that = (CurrencyConversionRate) obj;
    return Objects.equals(this.timestamp, that.timestamp) &&
            Objects.equals(this.currencyPair, that.currencyPair) &&
            Objects.equals(this.rate, that.rate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, currencyPair, rate);
  }

  @Override
  public String toString() {
    return "CurrencyConversionRate[" +
            "timestamp=" + timestamp + ", " +
            "currencyPair=" + currencyPair + ", " +
            "rate=" + rate + ']';
  }

  @Override
  public int compareTo(@NotNull CurrencyConversionRate that) {
    return this.getTimestamp().compareTo(that.getTimestamp());
  }
}