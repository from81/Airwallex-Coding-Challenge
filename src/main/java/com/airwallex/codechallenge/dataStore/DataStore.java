package com.airwallex.codechallenge.dataStore;

import com.airwallex.codechallenge.dataStore.alert.Alert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;

import java.util.Optional;

public abstract class DataStore {
  public abstract void insert(CurrencyConversionRate conversionRate);

  public abstract Optional<Alert> insertMaybeAlert(CurrencyConversionRate conversionRate);
}
