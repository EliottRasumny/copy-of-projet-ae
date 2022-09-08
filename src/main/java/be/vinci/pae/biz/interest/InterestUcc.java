package be.vinci.pae.biz.interest;

import java.util.List;

public interface InterestUcc {

  /**
   * Get all interests for an offer.
   *
   * @param idOffer : id of the offer we want the interests
   * @return all interests for an offer
   */
  List<InterestDto> getInterests(int idOffer, int idMember);

  /**
   * Set the recipient (idUser) to the offer (idOffer).
   *
   * @param idOffer       the id of the offer.
   * @param idRecipient   the id of the recipient.
   * @param objectVersion the version of the object
   */
  void addRecipient(int idOffer, int idRecipient, int objectVersion);

  /**
   * Get the interest of the recipient of an offer.
   *
   * @param idOffer  id of the offer.
   * @param idMember id of the offeror.
   * @return the interest
   */
  InterestDto getRecipient(int idOffer, int idMember);

  /**
   * Indicate that the recipient has come.
   *
   * @param idOffer       the offer related.
   * @param idRecipient   the recipient.
   * @param objectVersion the version of the object
   */
  void indicateRecipientHasCome(int idOffer, int idRecipient, int objectVersion);

  /**
   * Is the member interested in this offer.
   *
   * @param idOffer the offer.
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
}
