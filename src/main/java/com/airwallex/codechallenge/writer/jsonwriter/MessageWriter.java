package com.airwallex.codechallenge.writer.jsonwriter;

import org.json.simple.JSONObject;

import java.nio.file.Path;

abstract class MessageWriter {
  protected String filename;
  protected Path outputPath;
  public abstract void writeLine(JSONObject obj);
}
