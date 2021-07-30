package com.airwallex.codechallenge.writer;

import org.json.simple.JSONObject;

import java.nio.file.Path;

public abstract class MessageWriter {
  protected String filename;
  protected Path outputPath;

  public abstract void writeLine(JSONObject obj);
}
