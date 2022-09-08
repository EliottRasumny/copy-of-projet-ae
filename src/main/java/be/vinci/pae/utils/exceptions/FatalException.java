package be.vinci.pae.utils.exceptions;

public class FatalException extends RuntimeException {

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message the message
   * @param e       previous Exception
   */
  public FatalException(String message, Exception e) {
    super(message, e);
  }

  /**
   * throw an error with message.
   *
   * @param message the message
   */
  public FatalException(String message) {
    super(message);
  }

}
