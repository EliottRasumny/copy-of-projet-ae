package be.vinci.pae.dal.offer;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.type.TypeDao;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OfferDaoImpl implements OfferDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;
  @Inject
  private ObjectDao objectDao;
  @Inject
  private TypeDao typeDao;

  @Override
  public boolean isAlreadyInterested(int idOffer, int idUser) {
    String query = "SELECT * FROM project.interests WHERE offer = ? AND interested_member = ?";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set the data for the request to their corresponding fields.
      ps.setInt(1, idOffer);
      ps.setInt(2, idUser);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new FatalException("Error : CheckNoInterest(idOffer, idUser) - ", e);
    }
  }

  @Override
  public boolean offerIsMine(int idOffer, int idUser) {
    String query = "SELECT ob.offeror FROM project.offers of, project.objects ob "
        + "WHERE of.id_offer = ? AND of.object = ob.id_object";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set the data for the request to their corresponding fields.
      ps.setInt(1, idOffer);
      int idOfferor;
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          idOfferor = rs.getInt(1);
        } else {
          throw new FatalException("No object retrieved");
        }
      } catch (SQLException e) {
        throw new FatalException("Error : OfferIsMine(idOffer, idUser)", e);
      }
      return idUser == idOfferor;
    } catch (SQLException e) {
      throw new FatalException("Error : OfferIsMine(idOffer, idUser)", e);
    }
  }

  @Override
  public OfferDto getOne(int idOffer) {
    String query =
        "SELECT of.id_offer, of.object, of.offer_date, of.state, count(i.*) AS NBR_INTERESTS "
            + "FROM project.offers of LEFT OUTER JOIN project.interests i ON i.offer = of.id_offer "
            + "WHERE of.id_offer = ? GROUP BY of.id_offer";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      // Set the data to its corresponding field.
      ps.setInt(1, idOffer);
      OfferDto offer = factory.getOffer();
      try (ResultSet rs = ps.executeQuery()) {
        if (setOfferInfo(rs, offer)) {
          return offer;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(idOffer) - ", e);
    }
  }

  @Override
  public OfferDto getOne(ObjectDto objectDto) {
    String query = "SELECT of.id_offer, of.object, of.offer_date, of.state, "
        + "count(i.*) AS NBR_INTERESTS "
        + "FROM project.offers of LEFT OUTER JOIN project.interests i ON i.offer = of.id_offer "
        + "WHERE object = ? GROUP BY of.id_offer, i.id_interest ORDER BY of.offer_date DESC";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      // Set the data to its corresponding field.
      ps.setInt(1, objectDto.getIdObject());
      OfferDto offer = factory.getOffer();
      try (ResultSet rs = ps.executeQuery()) {
        if (setOfferInfo(rs, offer)) {
          return offer;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(idOffer) - ", e);
    }
  }

  /**
   * Get the data from the result of a request and save it in an offer object.
   *
   * @param rs    the resultSet containing all the data.
   * @param offer in which the data need to be inserted.
   * @return the offer with all the data.
   */
  private boolean setOfferInfo(ResultSet rs, OfferDto offer) {
    try {
      if (rs.next()) {
        // Set the received data for the offer passed in parameter from the corresponding ResultSet.
        offer.setIdOffer(rs.getInt(1));
        ObjectDto objectDto = objectDao.getOne(rs.getInt(2));
        offer.setObject(objectDto);
        offer.setDate(rs.getTimestamp(3));
        offer.setState(rs.getString(4));
        offer.setNbrInterests(rs.getInt(5));
        return true;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : setObjectInfo(resultSet, offer) - ", e);
    }
    return false;
  }

  @Override
  public List<OfferDto> getAll(String sortedBy, UserDto userRequester, String filterType,
      String filterValue) {
    String query;
    // Get all offers, done by the user
    if (filterType != null && filterType.equals(Constants.OBJECT_FILTER_TYPE_MINE)
        && filterValue.equals(Constants.OBJECT_FILTER_VALUE_ALL)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type, "
              + " of.state, ob.state, count(i.*) AS NBR_INTERESTS "
              + " FROM project.objects ob, project.offers of "
              + " LEFT OUTER JOIN project.interests i ON i.offer = of.id_offer "
              + " WHERE of.object = ob.id_object AND ob.offeror = ? AND of.offer_date IN\n"
              + "                                                     (SELECT MAX(of2.offer_date)\n"
              + "                                                     FROM project.offers of2\n"
              + "                                                     WHERE of.object = of2.object)"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture,"
              + " ob.type, of.state, ob.state "
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
      // Get all offers with an offeror's username = filterValue,
      // for the current user = userRequester
      // All the offers have a state of 'donated', 'assignable' or 'assigned'
    } else if (filterType != null && filterType.equals(Constants.OBJECT_FILTER_TYPE_BY_NAME)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.objects ob, project.users u, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND (ob.state = 'donated' OR ob.state = 'assignable')"
              + " AND u.id_user = ob.offeror"
              + " AND u.lastname LIKE CONCAT('%" + filterValue + "%')"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
      // Get all offers with a state = filterValue, for the current user = userRequester
      // All the offers have a state of 'donated', 'assignable' or 'assigned'
    } else if (filterType != null && filterType.equals(Constants.OBJECT_FILTER_TYPE_BY_STATE)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND (ob.state = 'donated' OR ob.state = 'assignable')"
              + " AND ob.state LIKE CONCAT('%" + filterValue + "%')"
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
      // Get all offers with a state = filterValue, for the current user = userRequester
      // All the offers have a state of 'donated', 'assignable' or 'assigned'
    } else if (filterType != null && filterType.equals(Constants.OBJECT_FILTER_TYPE_BY_TYPE)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.types ty, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND (ob.state = 'donated' OR ob.state = 'assignable')"
              + " AND ty.id_type = ob.type AND ty.label LIKE CONCAT('%" + filterValue + "%')"
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
    } else if (filterType != null && filterType
        .equals(Constants.OBJECT_FILTER_VALUE_GIVEN_ASSIGNED)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND ob.state = '" + filterValue + "'"
              + " AND ob.recipient = " + userRequester.getIdUser()
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
    } else if (filterType != null && filterType
        .equals(Constants.OBJECT_FILTER_VALUE_RECEIVED)) {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND ob.state = 'given'"
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
    } else if (filterType != null && filterType.equals(Constants.OBJECT_FILTER_TYPE_BY_DATE)) {
      String[] dates = filterValue.split("_");
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE (of.offer_date <= '" + dates[1] + "' AND of.offer_date >= '" + dates[0]
              + "')"
              + " AND ob.id_object = of.object"
              + " AND (ob.state = 'assignable' OR ob.state = 'donated')"
              + " AND of.state != 'outdated'"
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
    } else {
      query =
          "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.type,"
              + " of.state, ob.state, u.id_user, u.lastname, count(i1.*) AS NBR_INTERESTS,"
              + " count(i2.*) AS MY_INTEREST"
              + " FROM project.users u, project.objects ob, project.offers of"
              + " LEFT OUTER JOIN project.interests i1 ON i1.offer = of.id_offer"
              + " LEFT OUTER JOIN project.interests i2 ON i2.offer = of.id_offer"
              + " AND i2.interested_member = ?"
              + " WHERE of.state != 'outdated' AND of.object = ob.id_object"
              + " AND (ob.state = 'donated' OR ob.state = 'assignable')"
              + " AND ob.offeror = u.id_user"
              + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description,"
              + " ob.picture, ob.type, of.state, ob.state, u.id_user, u.lastname"
              + " ORDER BY " + sortedBy + ", of.id_offer DESC;";
    }
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set the data for the request to its corresponding field.
      ps.setInt(1, userRequester.getIdUser());
      // Create the array that will contain all the offers included in the request.
      List<OfferDto> offers = new ArrayList<>();
      // Execute the request
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          // Set the received data for each offer.
          ObjectDto object = factory.getObject();
          OfferDto offer = factory.getOffer();
          UserDto user = factory.getUser();
          offer.setIdOffer(rs.getInt(1));
          object.setIdObject(rs.getInt(2));
          offer.setDate(rs.getTimestamp(3));
          object.setDescription(rs.getString(4));
          object.setPictureName(rs.getString(5));
          TypeDto typeDto = typeDao.getOne(rs.getInt(6));
          object.setType(typeDto);
          offer.setState(rs.getString(7));
          object.setState(rs.getString(8));
          if (filterType == null || !filterType.equals(Constants.OBJECT_FILTER_TYPE_MINE)) {
            user.setIdUser(rs.getInt(9));
            user.setLastname(rs.getString(10));
            object.setOfferor(user);
            offer.setNbrInterests(rs.getInt(11));
            offer.setMyInterest(rs.getInt(12) != 0);
          } else {
            offer.setNbrInterests(rs.getInt(9));
          }
          offer.setObject(object);
          offers.add(offer);
        }
        return offers;
      }
    } catch (SQLException e) {
      throw new FatalException(
          "Error : OfferDaoImpl.getAll(sortedBy, researchQuery, idRequester, mine) - " + e
              .getMessage(), e);
    }
  }

  @Override
  public List<OfferDto> getAllAdmin(int idUser) {
    String query = "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture,"
        + " ob.type, of.state, ob.state, count(i.*) AS NBR_INTERESTS "
        + " FROM project.objects ob, project.offers of "
        + " LEFT OUTER JOIN project.interests i ON i.offer = of.id_offer "
        + " WHERE of.object = ob.id_object AND ob.offeror = ?"
        + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture,"
        + " ob.type, of.state, ob.state "
        + " ORDER BY of.offer_date DESC, of.id_offer DESC;";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Create the array that will contain all the offers included in the request.
      List<OfferDto> offers = new ArrayList<>();
      // Set the data for the request to its corresponding field.
      ps.setInt(1, idUser);
      // Execute the request
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          // Set the received data for each offer.
          ObjectDto object = factory.getObject();
          object.setIdObject(rs.getInt(2));
          OfferDto offer = factory.getOffer();
          offer.setIdOffer(rs.getInt(1));
          object.setPictureName(rs.getString(5));
          offer.setDate(rs.getTimestamp(3));
          object.setDescription(rs.getString(4));
          TypeDto typeDto = typeDao.getOne(rs.getInt(6));
          offer.setState(rs.getString(7));
          object.setType(typeDto);
          object.setState(rs.getString(8));
          offer.setNbrInterests(rs.getInt(9));
          offer.setObject(object);
          offers.add(offer);
        }
        return offers;
      }
    } catch (SQLException e) {
      throw new FatalException(
          "Error : OfferDaoImpl.getAll(sortedBy, researchQuery, idRequester, mine) - " + e
              .getMessage(), e);
    }
  }

  @Override
  public List<OfferDto> getAllDisconnected() {
    String query =
        "SELECT of.id_offer, ob.id_object, of.offer_date, ob.description, ob.picture, ob.state"
            + " FROM project.objects ob, project.offers of "
            + " LEFT OUTER JOIN project.interests i ON i.offer = of.id_offer "
            + " WHERE of.state != 'outdated' AND of.object = ob.id_object AND (ob.state = 'donated'"
            + " OR ob.state = 'assignable')"
            + " GROUP BY of.id_offer, ob.id_object, of.offer_date, ob.description, "
            + " ob.picture, ob.state"
            + " ORDER BY of.offer_date DESC, of.id_offer DESC;";

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      List<OfferDto> offers = new ArrayList<>();
      // Execute the request
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          // Set the received data for each offer.
          OfferDto offer = factory.getOffer();
          // Only insert the bare minimum data in the offer. So that a user can not, by any mean,
          // access to unwanted data.
          ObjectDto object = factory.getObject();
          offer.setIdOffer(rs.getInt(1));
          object.setIdObject(rs.getInt(2));
          offer.setDate(rs.getTimestamp(3));
          object.setDescription(rs.getString(4));
          object.setPictureName(rs.getString(5));
          object.setState(rs.getString(6));
          offer.setObject(object);
          offers.add(offer);
        }
        return offers;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : OfferDaoImpl.getAll() - " + e.getMessage(), e);
    }
  }

  @Override
  public OfferDto addOne(ObjectDto object) {
    OfferDto offer = factory.getOffer();
    String query = "INSERT INTO project.offers VALUES (DEFAULT,?,?,'available')";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      // Set the data for the request to their corresponding fields.
      ps.setInt(1, object.getIdObject());
      ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      // Execute the request.
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          // Set the received data for the offer. If there is none, it means that it does not exist.
          offer.setIdOffer(rs.getInt(1));
          offer.setObject(objectDao.getOne(rs.getInt(2)));
          offer.setDate(rs.getTimestamp(3));
          return offer;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne() - ", e);
    }
  }

  @Override
  public void updateOfferState(int idOffer, String newState) {
    String query = "UPDATE project.offers SET state = ? WHERE id_offer = ?";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      // Set the data for the request to its corresponding field.
      ps.setString(1, newState);
      ps.setInt(2, idOffer);
      // Execute the request.
      ps.executeUpdate();
      // If there is a response to the request, it means that it was a success. Otherwise, it is a
      // failure and there will not be any response.
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
      }
    } catch (SQLException e) {
      throw new FatalException("Error : updateOfferState() - ", e);
    }
  }

  @Override
  public void updateOffersAvailability(int idUser, String state) {
    String query = "UPDATE project.offers SET state = ?"
        + " WHERE state != 'outdated' AND object IN ("
        + " SELECT id_object FROM project.objects WHERE offeror = ?)";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set the data for the request to its corresponding field.
      ps.setString(1, state);
      ps.setInt(2, idUser);
      // Execute the request.
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException("Error : updateOffersAvailability() - ", e);
    }
  }

  @Override
  public boolean addRecipient(int idOffer, int idUser) {
    String query = "UPDATE project.offers SET recipient_chosen = true "
        + " WHERE id_offer = ? AND interested_member = ?";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      // Set the data for the request to its corresponding field.
      ps.setInt(1, idOffer);
      ps.setInt(1, idUser);
      // Execute the request.
      ps.executeUpdate();
      // If there is a response to the request, it means that it was a success. Otherwise, it is a
      // failure and there will not be any response.
      try (ResultSet rs = ps.getGeneratedKeys()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addRecipient() - ", e);
    }
  }
}
