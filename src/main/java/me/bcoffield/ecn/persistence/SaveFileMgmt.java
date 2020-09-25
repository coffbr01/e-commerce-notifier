package me.bcoffield.ecn.persistence;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

/** This is a terrible hack and I apologize in advance. */
@Slf4j
public class SaveFileMgmt {
  private static final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
  private static final File FILE = new File("saveFile.json");
  private static SaveFile SAVE_FILE;

  public static SaveFile get() {
    try {
      if (SAVE_FILE != null) {
        return SAVE_FILE;
      } else if (FILE.exists()) {
        SAVE_FILE = mapper.readValue(FILE, SaveFile.class);
      } else {
        SAVE_FILE = new SaveFile();
      }
    } catch (IOException e) {
      log.error("Could not read " + FILE.getName(), e);
    }
    return SAVE_FILE;
  }

  public static void save() {
    if (SAVE_FILE == null) {
      log.debug("Not saving null state");
      return;
    }
    // Remove old error statistics
    SAVE_FILE
        .getErrorStatistics()
        .values()
        .forEach(
            errorStatistic -> {
              errorStatistic.setOccurrences(
                  errorStatistic.getOccurrences().stream()
                      .filter(timestamp -> timestamp > System.currentTimeMillis() - 3600000)
                      .collect(Collectors.toList()));
            });
    try {
      mapper.writeValue(FILE, SAVE_FILE);
    } catch (IOException e) {
      log.error("Could not write " + FILE.getName(), e);
    }
  }
}
