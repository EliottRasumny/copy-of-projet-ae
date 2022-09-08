package be.vinci.pae.dal.user;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.address.AddressDao;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;
  @Inject
  private AddressDao addressDao;

  @Override
  public UserDto getOne(String username) {
    String query = "SELECT * FROM project.users us "
        + " WHERE REPLACE(us.username, ' ', '') ~* REPLACE(CONCAT('^', ?, '$'), ' ', '')";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return setUserInfo(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(username) - ", e);
    }
  }

  @Override
  public UserDto getOne(int id) {
    String query = "SELECT us.id_user, us.role, us.username, us.lastname, us.surname, "
        + " us.address, us.phone_number, us.password, us.refusal_reason, us.state, us.version "
        + " FROM project.users us WHERE us.id_user = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return setUserInfo(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(id) - ", e);
    }
  }

  @Override
  public UserDto addOne(UserDto user) {
    String query = "INSERT INTO project.users VALUES (DEFAULT,?,?,?,?,?,?,?,?,?, 1)";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)
    ) {
      ps.setString(1, user.getRole());
      ps.setString(2, user.getUsername());
      ps.setString(3, user.getLastname());
      ps.setString(4, user.getSurname());
      ps.setInt(5, user.getAddress().getIdAddress());
      ps.setString(6, user.getPhoneNumber());
      ps.setString(7, user.getPassword());
      ps.setString(8, user.getRefusalReason());
      ps.setString(9, user.getState());

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          user.setIdUser(rs.getInt(1));
          return user;
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne - ", e);
    }
  }

  @Override
  public List<UserDto> getUsers(String state) {
    String query = "SELECT us.id_user, us.role, us.username, us.lastname, us.surname, us.address, "
        + " us.phone_number, us.password, us.refusal_reason, us.state, us.version "
        + " FROM project.users us WHERE us.state= ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, state);

      List<UserDto> users = new ArrayList<>();

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          UserDto user = setUserInfo(rs);
          users.add(user);
        }
        return users;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getUsers(state) - ", e);
    }
  }

  /**
   * Set the user infos from the request.
   *
   * @param rs result set from the request
   * @return a user
   */
  private UserDto setUserInfo(ResultSet rs) throws SQLException {
    UserDto user = factory.getUser();
    user.setIdUser(rs.getInt(1));
    user.setRole(rs.getString(2));
    user.setUsername(rs.getString(3));
    user.setLastname(rs.getString(4));
    user.setSurname(rs.getString(5));
    user.setAddress(addressDao.getOne(rs.getInt(6)));
    user.setPhoneNumber(rs.getString(7));
    user.setPassword(rs.getString(8));
    user.setRefusalReason(rs.getString(9));
    user.setState(rs.getString(10));
    user.setVersion(rs.getInt(11));
    return user;
  }

  @Override
  public void updatePhoneNumberOnInterest(int userId, String number) {
    String query = "UPDATE project.users"
        + " SET phone_number = ?"
        + " WHERE id_user = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, number);
      ps.setInt(2, userId);
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new FatalException("Error : updatePhoneNumber(userId, number)");
      }
    } catch (SQLException e) {
      throw new FatalException("Error : updatePhoneNumber(userId, number) - ", e);
    }
  }

  @Override
  public UserDto modify(UserDto user) {
    String query;
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      query = "UPDATE project.users SET "
          + " username = ?, "
          + " lastname = ?, "
          + " surname = ?, "
          + " phone_number = ?, "
          + " password = ?,"
          + " version=version+1 "
          + " WHERE id_user = ?"
          + " AND version = ?";
    } else {
      query = "UPDATE project.users SET "
          + " username = ?, "
          + " lastname = ?, "
          + " surname = ?, "
          + " phone_number = ?,"
          + " version=version+1 "
          + " WHERE id_user = ?"
          + " AND version = ?";
    }
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getLastname());
      ps.setString(3, user.getSurname());
      ps.setString(4, user.getPhoneNumber());
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        ps.setString(5, user.getPassword());
        ps.setInt(6, user.getIdUser());
        ps.setInt(7, user.getVersion());
      } else {
        ps.setInt(5, user.getIdUser());
        ps.setInt(6, user.getVersion());
      }
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new ConflictException("Someone already updated check again");
      }
      return getOne(user.getIdUser());
    } catch (SQLException e) {
      throw new FatalException("Error :  updateUserRole(role, id) - ", e);
    }
  }

  @Override
  public void update(UserDto newUser) {
    String query = "UPDATE project.users SET "
        + " role = ?, "
        + " username = ?, "
        + " lastname = ?, "
        + " surname = ?, "
        + " address = ?, "
        + " phone_number = ?, "
        + " password = ?, "
        + " refusal_reason = ?, "
        + " state = ?,"
        + " version=version+1 "
        + " WHERE id_user = ?"
        + " AND version = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, newUser.getRole());
      ps.setString(2, newUser.getUsername());
      ps.setString(3, newUser.getLastname());
      ps.setString(4, newUser.getSurname());
      ps.setInt(5, newUser.getAddress().getIdAddress());
      ps.setString(6, newUser.getPhoneNumber());
      ps.setString(7, newUser.getPassword());
      ps.setString(8, newUser.getRefusalReason());
      ps.setString(9, newUser.getState());
      ps.setInt(10, newUser.getIdUser());
      ps.setInt(11, newUser.getVersion());
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new ConflictException("Error - update user : not right version");
      }
    } catch (SQLException e) {
      throw new FatalException("Error :  updateUserRole(role, id) - ", e);
    }
  }

  @Override
  public List<UserDto> getAllUsers(String value, String type) {

    String query;
    switch (type) {
      case Constants.MEMBER_FILTER_TYPE_BY_LASTNAME:
        query = "SELECT * FROM project.users us WHERE us.lastname LIKE CONCAT('%', ?, '%')";
        break;
      case Constants.MEMBER_FILTER_TYPE_BY_POSTCODE:
        query = "SELECT * FROM project.users us, project.addresses ad "
            + "WHERE us.address = ad.id_address AND ad.postcode LIKE CONCAT('%', ?, '%')";
        break;
      case Constants.MEMBER_FILTER_TYPE_BY_COMMUNE:
        query = "SELECT * FROM project.users us, project.addresses ad "
            + "WHERE us.address = ad.id_address AND ad.commune LIKE CONCAT('%', ?, '%')";
        break;
      default:
        throw new FatalException(
            "Error : getAllUsers(value, string), the default shouldn't be reached.");

    }
    List<UserDto> list = new ArrayList<>();
    UserDto user;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      ps.setString(1, value);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          user = setUserInfo(rs);
          list.add(user);
        }
        return list;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getAllUsers(value, string)", e);
    }
  }
}