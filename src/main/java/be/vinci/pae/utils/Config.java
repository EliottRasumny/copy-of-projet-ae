package be.vinci.pae.utils;

import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

  private static Properties props;

  /**
   * Load properties of the file.
   *
   * @param file the name of the file that contains properties
   */
  public static void load(String file) {
    props = new Properties();
    try (InputStream input = new FileInputStream(file)) {
      props.load(input);
    } catch (IOException e) {
      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain")
              .build());
    }
  }

  /**
   * gives the property identified by the key.
   *
   * @param key the key of the param
   * @return the property identified by the key
   */
  public static String getProperty(String key) {
    return props.getProperty(key);
  }

  /**
   * gives the int property identified by the key.
   *
   * @param key the key of the param
   * @return the int property identified by the key
   */
  public static Integer getIntProperty(String key) {
    return Integer.parseInt(props.getProperty(key));
  }

  /**
   * gives the bool property identified by the key.
   *
   * @param key the key of the param
   * @return the bool property identified by the key
   */
  public static boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(props.getProperty(key));
  }


  /**
   * Load properties of the file with a certain charset.
   *
   * @param file the name of the file that contains properties
   */
  public static void read(final Path file) {
    final var properties = new Properties();

    try (var in = new InputStreamReader(
        new FileInputStream(file.toFile()), StandardCharsets.UTF_8)) {
      properties.load(in);
      props = properties;
    } catch (IOException e) {
      throw new FatalException("Error load properties");
    }
  }

}

