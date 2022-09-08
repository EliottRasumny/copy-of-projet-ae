package be.vinci.pae.dal;

public interface DalServices {

  /**
   * Set up a connection to send a transaction request to the database.
   */
  void startTransaction();

  /**
   * Commit the transaction request to the database. The connection will be automatically reset.
   */
  void commit();

  /**
   * Rollback the transaction request to the database. The connection will be automatically reset.
   */
  void rollback();

  /**
   * Set up a connection to send a request to the database. The request will be automatically
   * committed. Require the close method usage afterward to ensure that the connection is reset.
   */
  void open();

  /**
   * Reset the connection after having send a request to the database. Usage required after the open
   * method.
   */
  void close();

}
