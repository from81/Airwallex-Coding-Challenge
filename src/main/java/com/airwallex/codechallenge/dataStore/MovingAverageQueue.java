package com.airwallex.codechallenge.dataStore;

import com.airwallex.codechallenge.dataStore.alert.Alert;
import com.airwallex.codechallenge.dataStore.alert.SpotChangeAlert;
import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.javatuples.Pair;

import java.time.Instant;
import java.util.Hashtable;
import java.util.Optional;
import java.util.PriorityQueue;

public class MovingAverageQueue extends DataStore {
  private static final Hashtable<String, PriorityQueue<Pair<Instant, CurrencyConversionRate>>> queues = new Hashtable<>();
  private static final Hashtable<String, Pair<Double, Integer>> queueInfo = new Hashtable<>();
  private static int queueSize;
  private static float pctChangeThreshold;
  private static MovingAverageQueue instance = null;

  private MovingAverageQueue(int windowSize, float threshold) {
    queueSize = windowSize;
    pctChangeThreshold = threshold;
  }

  public static MovingAverageQueue create(int windowSize, float pctChangeThreshold) {
    if (instance == null) {
      instance = new MovingAverageQueue(windowSize, pctChangeThreshold);
    }
    return instance;
  }

  public void insert(CurrencyConversionRate conversionRate) {
    String currencyPair = conversionRate.getCurrencyPair();
    double rate = conversionRate.getRate();
    Instant ts = conversionRate.getTimestamp();

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

    // update queue and queueinfo
    q.add(Pair.with(ts, conversionRate));
    queues.put(currencyPair, q);
    queueInfo.put(currencyPair, Pair.with(cumsum+rate, currentQueueSize));
  }

  public Optional<Alert> insertMaybeAlert(CurrencyConversionRate conversionRate) {
    this.insert(conversionRate);

    Optional<Double> movingAverageMaybe = this.getCurrentMovingAverage(conversionRate.getCurrencyPair());

    if (!movingAverageMaybe.isPresent()) throw new ArithmeticException("Couldn't obtain moving average.");

    double movingAverage = movingAverageMaybe.get();
    double pctChange = (conversionRate.getRate() - movingAverage) / movingAverage;

    if (pctChange < pctChangeThreshold) return Optional.empty();
    else return Optional.of(new SpotChangeAlert(conversionRate, pctChange, movingAverage, pctChangeThreshold));
  }

  public Optional<Double> getCurrentMovingAverage(String currencyPair) {
    Pair<Double, Integer> currencyInfo = queueInfo.getOrDefault(currencyPair, null);
    return (currencyInfo != null) ? Optional.of(currencyInfo.getValue0() / currencyInfo.getValue1()) : Optional.empty();
  }
}
