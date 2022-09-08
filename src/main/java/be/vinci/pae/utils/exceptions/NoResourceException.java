package be.vinci.pae.utils.exceptions;

public class NoResourceException extends RuntimeException {

  /**
   * throw an error with message.
   *
   * @param message the message
   */
  public NoResourceException(String message) {
    super(message);
  }

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message the message
   * @param e       previous Exception
   */
  public NoResourceException(String message, Exception e) {
    super(message, e);
  }

}
