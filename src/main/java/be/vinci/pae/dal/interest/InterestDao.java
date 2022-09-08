package be.vinci.pae.dal.interest;

import be.vinci.pae.biz.interest.InterestDto;
import java.util.List;

public interface InterestDao {


  /**
   * Add the interest of the user in the database. Throw a FatalException if the insert didn't
   * worked.
   *
   * @param interest the interest to add.
   */
  void addOne(InterestDto interest);


  List<InterestDto> getInterests(int idOffer);

  void updateRecipientChosen(int idOffer, int idInterestedMember, boolean recipientChosen);

  /**
   * Get the recipient of an offer.
   *
   * @param idOffer id of the offer.
   * @return the interest of the recipient
   */
  InterestDto getRecipient(int idOffer);

  /**
   * Update the "has_come" field of an interest.
   *
   * @param idOffer     the id of thee offer.
   * @param idRecipient the id of the recipient.
   * @param hasCome     true if the recipient has come, false otherwise.
   */
  void updateRecipientHasCome(int idOffer, int idRecipient, boolean hasCome);

  /**
   * Is the member interested in this offer.
   *
   * @param idOffer  the offer.
   * @param idMember the member.
   * @return true if the member is interested.
   */
  boolean isInterested(int idOffer, int idMember);

  /**
   * Get the interests not read where the user is the offeror of the offer or where he has been
   * chosen as receiver.
   *
   * @param idUser the id of the user we are doing the request for
   * @return the list of all the interests corresponding to the request
   */
  List<InterestDto> getInterestedAndRecipeint(int idUser);

  /**
   * Set the read attribute of the interests not read where the user is the offeror of the offer or
   * where he has been chosen as receiver as read.
   *
   * @param idUser the id of the user we are doing the request for
   */
  void setInterestedToRead(int idUser);
}
