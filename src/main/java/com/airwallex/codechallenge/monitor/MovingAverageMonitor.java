package com.airwallex.codechallenge.monitor;

import com.airwallex.codechallenge.monitor.alert.Alert;
import com.airwallex.codechallenge.monitor.alert.ExecutableAlert;
import com.airwallex.codechallenge.monitor.alert.SpotChangeAlert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;
import com.airwallex.codechallenge.reader.ConfigReader;
import com.airwallex.codechallenge.reader.jsonreader.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import java.io.IOException;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Optional;
import java.util.PriorityQueue;

public class MovingAverageMonitor extends Monitor implements Runnable {
  private static final Logger logger = LogManager.getLogger();
  private static final Class<SpotChangeAlert> alertType = SpotChangeAlert.class;
  private final Hashtable<String, PriorityQueue<Pair<Instant, CurrencyConversionRate>>> queues = new Hashtable<>();
  private final Hashtable<String, Pair<Double, Integer>> queueInfo = new Hashtable<>();
  private final String inputFile;
  private final ConfigReader config;
  private final int queueSize;
  private final float pctChangeThreshold;
  private CurrencyConversionRate lastData = null;

  public MovingAverageMonitor(String inputFile, ConfigReader config) {
    this.inputFile = inputFile;
    this.config = config;
    queueSize = Integer.parseInt(config.get("moving_average_window"));
    pctChangeThreshold = Float.parseFloat(config.get("pct_change_threshold"));
  }

  @Override
  public Class<SpotChangeAlert> getAlertType() {
    return alertType;
  }

  @Override
  public Monitor processRow(CurrencyConversionRate conversionRate) {
    lastData = conversionRate;
    String currencyPair = conversionRate.getCurrencyPair();

    // get or create priority queue for the currency pair
    PriorityQueue<Pair<Instant, CurrencyConversionRate>> q;
    q = queues.getOrDefault(currencyPair, new PriorityQueue<>(queueSize));

    // get cumulative sum of the last n entries, and current queue size
    Pair<Double, Integer> currencyInfo = queueInfo.getOrDefault(currencyPair, Pair.with(0.0, 0));
    double cumsum = currencyInfo.getValue0();
    int currentQueueSize = currencyInfo.getValue1();

    // if queue is full, remove one item and subtract its value from cumsum
    // if not full, update current queue size
    if (currentQueueSize == queueSize) {
      Pair<Instant, CurrencyConversionRate> expiredData = q.remove();
      cumsum -= expiredData.getValue1().getRate();
    } else {
      currentQueueSize += 1;
    }

    // update queue and queueInfo
    q.add(Pair.with(conversionRate.getTimestamp(), conversionRate));
    queues.put(currencyPair, q);
    queueInfo.put(currencyPair, Pair.with(cumsum + conversionRate.getRate(), currentQueueSize));

    return this;
  }

  public Optional<Alert> checkAlertMovingAverage() throws IOException {
    if (lastData == null) return Optional.empty();

    Optional<Double> movingAverageMaybe = this.getCurrentMovingAverage(lastData.getCurrencyPair());

    if (!movingAverageMaybe.isPresent()) return Optional.empty();

    double movingAverage = movingAverageMaybe.get();
    double pctChange = (lastData.getRate() - movingAverage) / movingAverage;

    if (pctChange < pctChangeThreshold) return Optional.empty();
    else return Optional.of(new SpotChangeAlert(lastData, pctChange, movingAverage, pctChangeThreshold));
  }

  public Optional<Double> getCurrentMovingAverage(String currencyPair) {
    Pair<Double, Integer> currencyInfo = queueInfo.getOrDefault(currencyPair, null);
    return (currencyInfo != null) ? Optional.of(currencyInfo.getValue0() / currencyInfo.getValue1()) : Optional.empty();
  }

  @Override
  public void run() {
    Integer nDataPoints = 0;
    long startTime = System.nanoTime();

    // read parameters
    int windowSize = Integer.parseInt(config.get("moving_average_window"));
    float pctChangeThreshold = Float.parseFloat(config.get("pct_change_threshold"));

    // create reader and writer
    JsonReader reader = new JsonReader(inputFile);

    // log params
    String msg = String.format(
            "Program starting with parameters:\n\tMoving Average Window Size: %d\n\tPercent Change Threshold: %.2f",
            windowSize,
            pctChangeThreshold
    );
    logger.debug(msg);

    // while input file has a next line, process each data point.
    // log and execute any alerts that might be returned
    while (reader.hasNextLine()) {
      Optional<CurrencyConversionRate> conversionRateMaybe = reader.readLine();

      if (conversionRateMaybe.isPresent()) {
        CurrencyConversionRate conversionRate = conversionRateMaybe.get();
        this.processRow(conversionRate).getAlertsIfAny().parallelStream().forEach(
                alert -> {
                      logger.info(alert);
                      if (alert instanceof ExecutableAlert) ((ExecutableAlert) alert).execute();
                });
        nDataPoints++;
      }
    }

    // performance stats
    String result = String.format(
            "%d data points processed in %.6f seconds.",
            nDataPoints,
            (System.nanoTime() - startTime) / 1_000_000_000.0
    );
    logger.debug(result);
  }
}
