package be.vinci.pae.dal.object;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.type.TypeDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectDaoImpl implements ObjectDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private UserDao userDao;
  @Inject
  private Factory factory;
  @Inject
  private TypeDao typeDao;


  @Override
  public ObjectDto getOne(int id) {
    String query = "SELECT * FROM project.objects ob WHERE ob.id_object = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, id);
      ObjectDto object = factory.getObject();
      try (ResultSet rs = ps.executeQuery()) {
        if (setObjectInfo(rs, object)) {
          return object;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(id) - ", e);
    }
  }


  /**
   * Set the database fields in a DTO object.
   *
   * @param rs     resultset of the query done before
   * @param object where we will add the information to it
   * @return true ifresultset has a next, false otherwise
   */
  private boolean setObjectInfo(ResultSet rs, ObjectDto object) {
    try {
      if (rs.next()) {
        object.setIdObject(rs.getInt(1));
        TypeDto typeDto = typeDao.getOne(rs.getInt(2));
        object.setType(typeDto);
        object.setDescription(rs.getString(3));
        object.setPictureName(rs.getString(4));
        object.setTimeSlot(rs.getString(5));
        UserDto user;
        user = userDao.getOne(rs.getInt(6));
        object.setOfferor(user);
        user = userDao.getOne(rs.getInt(7));
        object.setRecipient(user);
        object.setState(rs.getString(8));
        object.setVersion(rs.getInt(9));
        return true;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : setObjectInfo(ResultSet, ObjectDto) - ", e);
    }
    return false;
  }

  @Override
  public ObjectDto addOne(ObjectDto object, int idUser) {
    String query =
        "INSERT INTO project.objects "
            + " (id_object, type ,description, time_slot, offeror, state, picture, version)"
            + " VALUES (DEFAULT,?,?,?,?,?,?,1)";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      TypeDto type = typeDao.getOne(object.getType().getLabel());
      ps.setInt(1, type.getIdType());
      ps.setString(2, object.getDescription());
      ps.setString(3, object.getTimeSlot());
      ps.setInt(4, idUser);
      ps.setString(5, object.getState());
      ps.setString(6, object.getPictureName());

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          object.setIdObject(rs.getInt(1));
          object.setOfferor(userDao.getOne(idUser));
          object.setType(typeDao.getOne(rs.getInt(2)));
          return object;
        }
        return null;

      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne(object, id) - ", e);
    }
  }

  @Override
  public void updateState(int id, String state, int version) {
    String query = "UPDATE project.objects "
        + " SET state = ?, version = version + 1 "
        + " WHERE id_object = ? AND version = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, state);
      ps.setInt(2, id);
      ps.setInt(3, version);
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new ConflictException("Error : updateState : version not up to date");
      }
    } catch (SQLException e) {
      throw new FatalException("Error : updateState(id, state) - ", e);
    }
  }


  @Override
  public void update(ObjectDto newObject) {
    String query = "UPDATE project.objects SET "
        + " type = ?, "
        + " description = ?, "
        + " picture = ?, "
        + " time_slot = ?, "
        + " offeror = ?, "
        + " recipient = ?, "
        + " state = ?,"
        + " version = version + 1 "
        + " WHERE id_object = ?"
        + " AND version = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, newObject.getType().getIdType());
      ps.setString(2, newObject.getDescription());
      ps.setString(3, newObject.getPictureName());
      ps.setString(4, newObject.getTimeSlot());
      ps.setInt(5, newObject.getOfferor().getIdUser());
      if (newObject.getRecipient() == null) {
        ps.setObject(6, null);
      } else {
        ps.setInt(6, newObject.getRecipient().getIdUser());
      }
      ps.setString(7, newObject.getState());
      ps.setInt(8, newObject.getIdObject());
      ps.setInt(9, newObject.getVersion());
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new ConflictException("Error : update version not up to date");
      }
    } catch (SQLException e) {
      throw new FatalException("Error :  update(ObjectDto) - ", e);
    }
  }

  @Override
  public void removeRecipient(int idObject, int version) {

    String query = "UPDATE project.objects"
        + " SET version= version + 1 ,"
        + "    recipient = NULL"
        + " WHERE id_object = ?"
        + "  AND version = ?;";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, idObject);
      ps.setInt(2, version);
      int res = ps.executeUpdate();

      if (res == 0) {
        throw new ConflictException("Error : removeRecipient version not up to date");
      }
    } catch (SQLException e) {
      throw new FatalException("Error :  removeRecipient(idObject) - ", e);
    }
  }

}
