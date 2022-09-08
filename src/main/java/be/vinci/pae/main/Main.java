package be.vinci.pae.main;

import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.LoggerManager;
import be.vinci.pae.utils.WebExceptionMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main class.
 */
public class Main {

  // Base URI the Grizzly HTTP server will listen on
  public static final String BASE_URI;

  static {
    Config.read(Paths.get("dev.properties"));
    BASE_URI = Config.getProperty("BaseUri");
  }

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in be.vinci package
    final ResourceConfig rc = new ResourceConfig().packages("be.vinci.pae.ihm")
        .register(JacksonFeature.class)
        .register(WebExceptionMapper.class)
        .register(MultiPartFeature.class)
        .register(ApplicationBinder.class);

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  /**
   * Main method.
   *
   * @param args args parameter
   * @throws IOException if the connection to the server failed
   */
  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    final LoggerManager lm = new LoggerManager();
    loggInfo();
    if (System.in.read() > 0) {
      server.shutdownNow();
      lm.close();
    }
  }

  private static void loggInfo() {
    Logger logger = Logger.getLogger(LoggerManager.LOGGER_NAME);
    logger.log(Level.INFO, "START SERVER");
    System.out.println("Jersey app started with endpoints available at "
        + String.format("%s%nPress enter to stop it...", BASE_URI));
  }
}
