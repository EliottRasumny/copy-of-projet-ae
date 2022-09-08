package be.vinci.pae.utils.exceptions;

public class ConflictException extends RuntimeException {

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message information about the exception
   * @param e       previous Exception
   */
  public ConflictException(String message, Exception e) {
    super(message, e);
  }

  /**
   * throw an error with message.
   *
   * @param message information about the exception
   */
  public ConflictException(String message) {
    super(message);
  }
}
