package com.airwallex.codechallenge;

import com.airwallex.codechallenge.input.CurrencyConversionRate;

import java.util.PriorityQueue;

public class MovingAverageQueue {
  private static PriorityQueue<CurrencyConversionRate> pq;

  public MovingAverageQueue(int windowSize) {
    pq = new PriorityQueue<>(windowSize);
  }
}
