package com.airwallex.codechallenge.monitor;

import com.airwallex.codechallenge.monitor.alert.Alert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;

import java.util.ArrayList;

public abstract class Monitor {
  public abstract Alert getAlertType();
  public abstract void processRow(CurrencyConversionRate conversionRate);

  public abstract ArrayList<Alert> checkAllAlerts();
}
