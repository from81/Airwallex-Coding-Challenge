package com.airwallex.codechallenge.monitor.alert;

import org.json.simple.JSONObject;

public abstract class Alert {
  @Override
  public abstract String toString();

  public abstract JSONObject toJson();
}
