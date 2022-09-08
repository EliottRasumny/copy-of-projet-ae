package be.vinci.pae.utils.exceptions;

public class ForbiddenException extends RuntimeException {

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message information about the exception
   * @param e       previous Exception
   */
  public ForbiddenException(String message, Exception e) {
    super(message, e);
  }

  /**
   * throw an error with message.
   *
   * @param message information about the exception
   */
  public ForbiddenException(String message) {
    super(message);
  }
}
