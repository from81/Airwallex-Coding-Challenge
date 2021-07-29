package com.airwallex.codechallenge.dataStore;

import com.airwallex.codechallenge.dataStore.alert.Alert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class DataStore {
  public abstract void insert(CurrencyConversionRate conversionRate);

  public abstract ArrayList<Alert> checkAllAlerts();
}
