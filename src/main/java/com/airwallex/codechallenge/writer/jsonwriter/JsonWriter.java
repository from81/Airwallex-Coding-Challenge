package com.airwallex.codechallenge.writer.jsonwriter;

import com.airwallex.codechallenge.writer.MessageWriter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Instant;

public class JsonWriter extends MessageWriter {
  PrintWriter writer;

  public JsonWriter(String path) throws IOException {
    // initialize parameters
    String timestamp = Instant.now().toString();
    if (timestamp.contains("T")) {
      timestamp = timestamp.substring(0, timestamp.indexOf("T"));
    }

    // assume `path` is relative path
    super.filename = String.format("%s.jsonl", timestamp);
    super.outputPath = Paths.get(path, super.filename);

    // file is created if it doesn't exist
    File file = new File(String.valueOf(super.outputPath));
    boolean append = false;
    if (!file.exists()) file.createNewFile();
    else append = true;

    // append if output already exists
    this.writer = new PrintWriter(new FileOutputStream(file, append));
  }

  @Override
  public void writeLine(JSONObject obj) {
    this.writer.write(obj.toJSONString() + "\n");
    this.writer.flush();
  }

  public String getPath() {
    return super.outputPath.toString();
  }

  public void close() {
    this.writer.close();
  }
}
