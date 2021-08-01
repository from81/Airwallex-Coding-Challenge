package com.airwallex.codechallenge.reader.jsonreader;

import com.airwallex.codechallenge.input.CurrencyConversionRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class JsonReaderTest {
  @Test
  @DisplayName("Can open a valid jsonline file and read a line.")
  public void testInit() {
    try {
      JsonReader reader = new JsonReader("example/input1.jsonl");
      assertTrue(reader.hasNextLine());
      assertTrue(reader.readLine().isPresent());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Specifying a file that does not exist should throw FileNotFoundError")
  public void testInvalidInput() {
    String input = "example/fake.jsonl";
    assertThrows(FileNotFoundException.class, () -> {
      JsonReader reader = new JsonReader(input);
    });
  }

  @Test
  @DisplayName("Can read line from valid file")
  public void testReadLineValidInput() {
    try {
      JsonReader reader = new JsonReader("example/input1.jsonl");
      Optional<CurrencyConversionRate> maybeConversionRate = reader.readLine();
      if (maybeConversionRate.isPresent()) assertEquals(maybeConversionRate.get().getRate(), 0.39281);
      else throw new FileNotFoundException("readline failed");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}