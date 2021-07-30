package com.airwallex.codechallenge.monitor;

import com.airwallex.codechallenge.monitor.alert.Alert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.monitor.alert.SpotChangeAlert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class Monitor {
  public abstract Class<SpotChangeAlert> getAlertType();
  public abstract Monitor processRow(CurrencyConversionRate conversionRate);
  private static final Logger logger = LogManager.getLogger();
  public abstract void run();

  @SuppressWarnings("unchecked")
  public ArrayList<Alert> getAlertsIfAny() {
    ArrayList<Alert> alerts = new ArrayList<>();
    Arrays.stream(this.getClass().getDeclaredMethods()).parallel().forEach(
            checkAlertMethodMaybe -> {
              if (checkAlertMethodMaybe.getName().startsWith("checkAlert"))
                try {
                  Optional<Alert> maybeAlert = (Optional<Alert>) checkAlertMethodMaybe.invoke(this);
                  maybeAlert.ifPresent(alerts::add);
                } catch (IllegalAccessException | InvocationTargetException e) {
                  e.printStackTrace();
                }
            }
    );
    return alerts;
  }
}
