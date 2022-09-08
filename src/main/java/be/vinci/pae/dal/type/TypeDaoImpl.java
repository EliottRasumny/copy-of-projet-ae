package be.vinci.pae.dal.type;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeDaoImpl implements TypeDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;

  @Override
  public TypeDto getOne(String label) {
    String query =
        "SELECT ty.id_type, ty.label FROM project.types ty WHERE REPLACE(ty.label,' ','') ~*"
            + " REPLACE(?,' ','')";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      ps.setString(1, label);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return setTypeInfo(rs);
        } else {
          return addOne(label);
        }
      }
    } catch (SQLException e) {
      throw new FatalException("Error : type -> getOne()", e);
    }
  }

  @Override
  public TypeDto getOne(int id) {
    String query = "SELECT ty.id_type, ty.label FROM project.types ty WHERE ty.id_type = ?";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return setTypeInfo(rs);
        }
      }
      return null;
    } catch (SQLException e) {
      throw new FatalException("Error : type -> getOne()", e);
    }
  }

  @Override
  public TypeDto addOne(String label) {
    String query = "INSERT INTO project.types VALUES (DEFAULT,?)";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      ps.setString(1, label);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return setTypeInfo(rs);
        }
      }
      throw new FatalException("Error : type -> addOne()");
    } catch (SQLException e) {
      throw new FatalException("Error : type -> addOne()", e);
    }
  }

  /**
   * Set the type infos from the request.
   *
   * @param rs result set from the request
   * @return the type with the corresponding infos
   * @throws SQLException if there is a problem with the result set
   */
  public TypeDto setTypeInfo(ResultSet rs) throws SQLException {
    TypeDto type = factory.getType();
    type.setIdType(rs.getInt(1));
    type.setLabel(rs.getString(2));
    return type;
  }

}
