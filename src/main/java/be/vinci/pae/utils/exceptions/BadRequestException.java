package be.vinci.pae.utils.exceptions;

public class BadRequestException extends RuntimeException {

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message information about the exception
   * @param e       previous Exception
   */
  public BadRequestException(String message, Exception e) {
    super(message, e);
  }

  /**
   * throw an error with message.
   *
   * @param message information about the exception
   */
  public BadRequestException(String message) {
    super(message);
  }
}
