package com.airwallex.codechallenge.writer.jsonwriter;

import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class JsonWriter extends MessageWriter {
  PrintWriter writer;

  public JsonWriter() throws IOException {
    String timestamp = Instant.now().toString();
    if (timestamp.contains("T")) {
      timestamp = timestamp.substring(0, timestamp.indexOf("T"));
    }
    super.filename = String.format("%s.jsonl", timestamp);
    Path baseDir = Paths.get(System.getProperty("user.dir"));
    super.outputPath = Paths.get(baseDir.toString(), "output", super.filename);


    try {
      // file is created if doesn't exist
      File file = new File(String.valueOf(super.outputPath));
      Boolean append = false;

      if(!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }
      else append = true;

      this.writer = new PrintWriter(new FileOutputStream(file, append));
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public void writeLine(JSONObject obj) {
    this.writer.write(obj.toJSONString());
    this.writer.flush();
  }

  public void close() {
    this.writer.close();
  }
}