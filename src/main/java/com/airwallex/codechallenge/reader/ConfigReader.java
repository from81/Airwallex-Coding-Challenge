package com.airwallex.codechallenge.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
  Properties prop;

  public ConfigReader(String path) throws IOException {
    prop = new Properties();
    prop.load(new FileInputStream(path));
  }

  public String get(String field) {
    return prop.getProperty(field);
  }
}
