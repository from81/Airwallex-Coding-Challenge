package com.airwallex.codechallenge.reader.jsonreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class JsonReaderTest {
  @Test
  @DisplayName("Can open a valid jsonline file and read a line.")
  public void testInit() {
    JsonReader reader = new JsonReader("example/input1.jsonl");
    Assertions.assertTrue(reader.hasNextLine());
  }
}