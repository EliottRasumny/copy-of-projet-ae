package be.vinci.pae.dal;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.FatalException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp2.BasicDataSource;

public class DalServicesImpl implements DalServices, DalBackendServices {

  // Map of connections linked with the current thread id
  ThreadLocal<Connection> mapConnections;
  // Connection's pool
  BasicDataSource poolConnections;

  /**
   * Create the connection to the DB.
   */
  public DalServicesImpl(/*Connection conn*/) {
    this.mapConnections = new ThreadLocal<>();
    setUpDataPool();
  }

  /**
   * Create and initialize the connection's pool with all the corrects parameters.
   */
  private void setUpDataPool() {
    this.poolConnections = new BasicDataSource();
    this.poolConnections.setUrl(Config.getProperty("urlDB"));
    this.poolConnections.setUsername(Config.getProperty("user"));
    this.poolConnections.setPassword(Config.getProperty("password"));
    //this.poolConnections.setMinIdle(4);
    //this.poolConnections.setMaxIdle(6);
    this.poolConnections.setMaxOpenPreparedStatements(100);
  }

  @Override
  public PreparedStatement getPreparedStatement(String query) {
    try {
      Connection conn = mapConnections.get();
      if (conn == null) {
        throw new FatalException("You did not start the transaction");
      }
      return conn.prepareStatement(query);
    } catch (SQLException e) {
      throw new FatalException("Error : getPreparedStatement(query) - ", e);
    }
  }

  @Override
  public PreparedStatement getPreparedStatementInsertWithReturnId(String query) {
    try {
      return mapConnections.get()
          .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    } catch (SQLException e) {
      throw new FatalException("Error : getPreparedStatementInsertWithReturnId(query) - ", e);
    }
  }

  @Override
  public void startTransaction() {
    checkAlreadyOpenedConnection();
    try {
      Connection conn = poolConnections.getConnection();
      conn.setAutoCommit(false);
      mapConnections.set(conn);
    } catch (SQLException e) {
      throw new FatalException("Error : startTransaction() - ", e);
    }
  }

  @Override
  public void open() {
    checkAlreadyOpenedConnection();
    try {
      mapConnections.set(poolConnections.getConnection());
    } catch (SQLException e) {
      throw new FatalException("Error : open() - ", e);
    }
  }

  @Override
  public void close() {
    Connection conn = mapConnections.get();
    mapConnections.remove();
    try {
      conn.close();
    } catch (SQLException e) {
      throw new FatalException("Error : close() - ", e);
    }
  }

  @Override
  public void commit() {
    Connection conn = mapConnections.get();
    try {
      conn.commit();
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      throw new FatalException("Error : commit() - ", e);
    } finally {
      close();
    }
  }

  @Override
  public void rollback() {
    Connection conn = mapConnections.get();
    try {
      conn.rollback();
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      throw new FatalException("Error : rollback() - ", e);
    } finally {
      close();
    }
  }

  /**
   * Check if the connection is already open. And if it was opened, close it.
   */
  private void checkAlreadyOpenedConnection() {
    if (mapConnections.get() != null) {
      close();
    }
  }
}
