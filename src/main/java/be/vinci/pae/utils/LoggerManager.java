package be.vinci.pae.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerManager {

  public static final String LOGGER_NAME = "MyLog";
  private final FileHandler fh;

  /**
   * Initialize the logger.
   *
   * @throws IOException if logger cannot be create
   */
  public LoggerManager() throws IOException {
    Logger logger = Logger.getLogger(LOGGER_NAME);

    // This block configure the logger with handler and formatter
    fh = new FileHandler("logs/testLog.log", 10000, 10, true);
    logger.addHandler(fh);
    SimpleFormatter formatter = new SimpleFormatter();
    fh.setFormatter(formatter);
  }

  public void close() {
    fh.close();
  }

}
