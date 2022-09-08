package be.vinci.pae.utils.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TokenException extends WebApplicationException {

  /**
   * throw an error with a message.
   *
   * @param message the error message
   */
  public TokenException(String message) {
    super(message);
  }

  /**
   * throw an error with no message.
   *
   * @param status the status of the exception
   */
  public TokenException(Response.Status status) {
    super(Response.status(status)
        .build());
  }

  /**
   * throw an error with a message.
   *
   * @param message the error message
   * @param status  the status of the exception
   */
  public TokenException(String message, Response.Status status) {
    super(Response.status(status)
        .entity(message)
        .type("text/plain")
        .build());
  }

  /**
   * throw an error with a message.
   *
   * @param cause  the error cause
   * @param status the status of the exception
   */
  public TokenException(Throwable cause, Response.Status status) {
    super(Response.status(status)
        .entity(cause.getMessage())
        .type("text/plain")
        .build());
  }
}

