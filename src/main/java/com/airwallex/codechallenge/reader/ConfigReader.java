package com.airwallex.codechallenge.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigReader {
  Properties prop;

  public ConfigReader(String path) throws IOException {
    prop = new Properties();

    try (InputStream inputStream = new FileInputStream(path)) {
      prop.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public String get(String field) {
    return prop.getProperty(field);
  }
}