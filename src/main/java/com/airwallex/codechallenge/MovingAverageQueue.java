package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.javatuples.Pair;

import java.time.Instant;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class MovingAverageQueue {
  private static HashSet<String> knownCurrencies = new HashSet<String>();
  private static Hashtable<String, PriorityQueue<Pair<Instant, CurrencyConversionRate>>> queues = new Hashtable<>();
  private static Hashtable<String, Pair<Double, Integer>> queueInfo = new Hashtable<>();
  private static int queueSize;

  public MovingAverageQueue(int windowSize) {
    queueSize = windowSize;
  }

  public Double insert(CurrencyConversionRate conversionRate) {
    String currencyPair = conversionRate.getCurrencyPair();
    Double rate = conversionRate.getRate();
    Instant ts = conversionRate.getTimestamp();

    PriorityQueue<Pair<Instant, CurrencyConversionRate>> q;

    knownCurrencies.add(currencyPair);

    // get or create priority queue for the currency pair
    q = queues.getOrDefault(currencyPair, new PriorityQueue<Pair<Instant, CurrencyConversionRate>>(queueSize));

    // get cumulative sum of the last n entries, and current queue size
    Pair currencyInfo = queueInfo.getOrDefault(currencyPair, Pair.with(0.0, 0));
    Double cumsum = (Double) currencyInfo.getValue0();
    int currentQueueSize = (int) currencyInfo.getValue1();

    // if queue is full, remove one item and subtract its value from cumsum
    // if not full, update currenct queue size
    if(currentQueueSize == queueSize) {
      Pair<Instant, CurrencyConversionRate> expiredData = q.remove();
      cumsum -= expiredData.getValue1().getRate();
    } else {
      currentQueueSize += 1;
    }
    cumsum += rate;
    q.add(Pair.with(ts, conversionRate));
    queueInfo.put(currencyPair, Pair.with(cumsum, currentQueueSize));

    // calculate and return moving average
    return cumsum / currentQueueSize;
  }

  public Double getCurrentMovingAverage(String currencyPair) {
    Pair<Double, Integer> currencyInfo = queueInfo.getOrDefault(currencyPair, Pair.with(0.0, 0));
    Double cumsum = currencyInfo.getValue0();
    int currentQueueSize = currencyInfo.getValue1();
    return cumsum / currentQueueSize;
  }
}
