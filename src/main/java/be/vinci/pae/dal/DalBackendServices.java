package be.vinci.pae.dal;

import java.sql.PreparedStatement;

public interface DalBackendServices {

  /**
   * Prepare a statement.
   *
   * @param query : the query to be prepared
   * @return the preparedStatement related to the query
   */
  PreparedStatement getPreparedStatement(String query);

  /**
   * Prepare a statement for an insert in the database and returns the id of the inserted object.
   *
   * @param query : the query to be prepared
   * @return the preparedStatement that request for the ResultSet to return the id.
   */
  PreparedStatement getPreparedStatementInsertWithReturnId(String query);

}
