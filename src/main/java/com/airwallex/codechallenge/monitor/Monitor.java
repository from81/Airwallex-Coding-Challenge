package com.airwallex.codechallenge.monitor;

import com.airwallex.codechallenge.monitor.alert.Alert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.monitor.alert.SpotChangeAlert;

import java.util.ArrayList;

public abstract class Monitor {
  public abstract Class<SpotChangeAlert> getAlertType();
  public abstract Monitor processRow(CurrencyConversionRate conversionRate);
  public abstract ArrayList<Alert> getAlertsIfAny();
}
