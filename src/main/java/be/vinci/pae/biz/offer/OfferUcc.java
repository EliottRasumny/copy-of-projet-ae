package be.vinci.pae.biz.offer;

import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.biz.user.UserDto;
import java.io.InputStream;
import java.util.List;

public interface OfferUcc {

  /**
   * Retrieve all the currently offered objects.
   *
   * @param sort          option to order the list.
   * @param userRequester the user that made the request.
   * @param filterType    the nature of the filter applied on the offers' list.
   * @param filterValue   the value of the filter.
   * @return a list of all the objects, sorted according to the sort choice.
   */
  List<OfferDto> getAll(String sort, UserDto userRequester, String filterType, String filterValue);

  /**
   * Retrieve all the object of the user.
   *
   * @param idUser the id of the user.
   * @return a list of all the relevant objects.
   */
  List<OfferDto> getAllAdmin(int idUser);

  /**
   * Mark the interest of the user for the object.
   *
   * @param idOffer       the id of the offer the user is interested in.
   * @param idUser        the id of the interested user.
   * @param date          the date of the interest
   * @param phoneNumber   the phone number of the interested user.
   * @param objectVersion the version of the object
   * @return true if the interest has been marked, false otherwise.
   */
  boolean markInterest(int idOffer, int idUser, String date, String phoneNumber, int objectVersion);

  boolean isInterested(int idOffer, int idUser);

  OfferDto getOne(int idObject);

  /**
   * Create an offer and also the object of the offer.
   *
   * @param objectFront the object to offer
   * @param idUser      the id of the user that create the offer
   * @param file        the file of the picture given by the user
   * @param filename    the name of the file
   * @return the offer created
   */
  OfferDto createOffer(ObjectDto objectFront, int idUser, InputStream file, String filename);

  /**
   * Set the deleted field to true in the database.
   *
   * @param idOffer       the id of the offer.
   * @param idUser        the id of the user.
   * @param objectVersion the version of the object
   * @return true if field has been set to true in database, false otherwise.
   */
  boolean cancelOffer(int idOffer, int idUser, int objectVersion);

  /**
   * Remove the recipient of the object, set has_come of the user to false and set the state of the
   * object to "assignable" or "donated".
   *
   * @param idOffer       the id of the offer
   * @param oldRecipient  the id of the old recipient
   * @param objectVersion the version of the object
   */
  void removeOldRecipient(int idOffer, int oldRecipient, int objectVersion);

  /**
   * Create a new offer based on the old one and put the state of the old one to cancel.
   *
   * @param idOffer       the id of the offer
   * @param oldRecipient  the id of the old recipient
   * @param objectVersion the version of the object
   */
  void offerAgain(int idOffer, int oldRecipient, int objectVersion);

  /**
   * Rate the Offer.
   *
   * @param idOffer the offer to rate
   * @param rating  the rating
   */
  void rateOffer(int idOffer, RatingDto rating);
}
