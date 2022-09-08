package be.vinci.pae.dal.interest;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.interest.InterestDto;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InterestDaoImpl implements InterestDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;

  @Inject
  private OfferDao offerDao;

  @Override
  public void addOne(InterestDto interest) {
    String query = "INSERT INTO project.interests VALUES(DEFAULT,?,?,?,?,FALSE,NULL,FALSE)";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)) {
      // Set data to each field.
      ps.setInt(1, interest.getOffer().getIdOffer());
      ps.setInt(2, interest.getInterestedMember().getIdUser());
      ps.setString(3, interest.getDate());
      ps.setBoolean(4, interest.isAnswerCall());
      // Execute the request.
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (!rs.next()) {
          throw new FatalException("Error : addOne(idOffer, idUser, date, answerCall)");
        }
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne(idOffer, idUser, date, answerCall) - ", e);
    }
  }

  @Override
  public List<InterestDto> getInterests(int idOffer) {
    String query = "SELECT i.id_interest, i.date, i.answers_call, "
        + "i.recipient_chosen, i.has_come, u.id_user, u.username, u.lastname, u.surname,"
        + " u.phone_number, u.state, i.offer"
        + " FROM project.interests i, project.users u WHERE i.offer = ? "
        + "AND u.id_user = i.interested_member";
    List<InterestDto> interests = new ArrayList<>();
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set data to each field.
      ps.setInt(1, idOffer);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          InterestDto interest = getInterestDto(rs);
          interests.add(interest);
        }
        return interests;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getInterests(idOffer) - ", e);
    }
  }

  private InterestDto getInterestDto(ResultSet rs) throws SQLException {
    // set user info
    UserDto user = factory.getUser();
    user.setIdUser(rs.getInt(6));
    user.setUsername(rs.getString(7));
    user.setLastname(rs.getString(8));
    user.setSurname(rs.getString(9));
    user.setPhoneNumber(rs.getString(10));
    user.setState(rs.getString(11));

    //set offer info
    OfferDto offer = offerDao.getOne(rs.getInt(12));

    // set interest info
    InterestDto interest = factory.getInterest();
    interest.setIdInterest(rs.getInt(1));
    interest.setOffer(offer);
    interest.setInterestedMember(user);
    interest.setDate(rs.getString(2));
    interest.setAnswerCall(rs.getBoolean(3));
    interest.setRecipentChosen(rs.getBoolean(4));
    interest.setHasCome(rs.getBoolean(5));
    return interest;
  }

  @Override
  public void updateRecipientChosen(int idOffer, int idInterestedMember, boolean recipientChosen) {
    String query = "UPDATE project.interests SET recipient_chosen = ? , read = FALSE"
        + " WHERE offer = ? AND interested_member = ? ";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setBoolean(1, recipientChosen);
      ps.setInt(2, idOffer);
      ps.setInt(3, idInterestedMember);
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new FatalException(
            "Error : updateRecipientChosen(idOffer, idInterestedMember, recipientChosen)");
      }
    } catch (SQLException e) {
      throw new FatalException(
          "Error : updateRecipientChosen(idOffer, idInterestedMember, recipientChosen) - ", e);
    }
  }

  /**
   * Get the recipient of an offer.
   *
   * @param idOffer id of the offer.
   * @return the interest of the recipient
   */
  @Override
  public InterestDto getRecipient(int idOffer) {
    String query = "SELECT i.id_interest, i.date, i.answers_call, "
        + "i.recipient_chosen, i.has_come, u.id_user, u.username, u.lastname, u.surname,"
        + " u.phone_number, u.state, i.offer"
        + " FROM project.interests i, project.users u WHERE i.offer = ? "
        + "AND u.id_user = i.interested_member AND i.recipient_chosen = true";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set data to each field.
      ps.setInt(1, idOffer);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return getInterestDto(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getRecipient(idOffer) - ", e);
    }
  }

  @Override
  public void updateRecipientHasCome(int idOffer, int idRecipient, boolean hasCome) {
    String query = "UPDATE project.interests SET has_come = ? "
        + " WHERE offer = ? AND interested_member = ? ";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setBoolean(1, hasCome);
      ps.setInt(2, idOffer);
      ps.setInt(3, idRecipient);
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new FatalException(
            "Error : updateRecipientHasCome(idOffer, idInterestedMember)");
      }
    } catch (SQLException e) {
      throw new FatalException(
          "Error : updateRecipientHasCome(idOffer, idInterestedMember) - ", e);
    }
  }

  @Override
  public boolean isInterested(int idOffer, int idMember) {
    String query = "SELECT i.id_interest "
        + " FROM project.interests i WHERE i.offer = ? "
        + "AND i.interested_member = ?";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set data to each field.
      ps.setInt(1, idOffer);
      ps.setInt(2, idMember);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new FatalException("Error : isInterested(idOffer, idMember) - ", e);
    }
  }

  @Override
  public List<InterestDto> getInterestedAndRecipeint(int idUser) {
    String query = "SELECT i.id_interest, i.offer, i.interested_member"
        + " FROM project.offers of, project.interests i, project.objects ob "
        + " WHERE (i.recipient_chosen = true AND i.interested_member = ? "
        + " OR i.recipient_chosen = false AND ob.offeror = ?) "
        + " AND of.object = ob.id_object "
        + " AND i.offer = of.id_offer "
        + " AND i.read = FALSE";
    List<InterestDto> interests = new ArrayList<>();
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      // Set data to each field.
      ps.setInt(1, idUser);
      ps.setInt(2, idUser);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        InterestDto interest = factory.getInterest();
        interest.setIdInterest(rs.getInt(1));
        OfferDto offer = offerDao.getOne(rs.getInt(2));
        UserDto user = factory.getUser();
        user.setIdUser(rs.getInt(3));
        interest.setOffer(offer);
        interest.setInterestedMember(user);
        interests.add(interest);
      }
      return interests;
    } catch (SQLException e) {
      throw new FatalException("Error : getInterestedAndRecipeint(idUser) - ", e);
    }
  }

  @Override
  public void setInterestedToRead(int idUser) {
    String query = "UPDATE project.interests i SET read = TRUE "
        + " FROM project.offers of, project.objects ob "
        + " WHERE (i.recipient_chosen = true AND i.interested_member = ? "
        + " OR i.recipient_chosen = false AND ob.offeror = ?) "
        + " AND of.object = ob.id_object "
        + " AND i.offer = of.id_offer "
        + " AND i.read = FALSE ";

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(query)) {
      ps.setInt(1, idUser);
      ps.setInt(2, idUser);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException("Error : setInterestedToRead(int idUser) ");
    }
  }

}
