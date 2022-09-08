package be.vinci.pae.utils.exceptions;

public class PreconditionException extends RuntimeException {


  /**
   * throw an error with message.
   *
   * @param message the message
   */
  public PreconditionException(String message) {
    super(message);
  }

  /**
   * throw an error with message and the previous Exception.
   *
   * @param message the message
   * @param e       previous Exception
   */
  public PreconditionException(String message, Exception e) {
    super(message, e);
  }

}
