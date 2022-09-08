package be.vinci.pae.dal.offer;

import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.user.UserDto;
import java.util.List;

public interface OfferDao {

  /**
   * Look in the database if there is already an interest for the given offer and user.
   *
   * @param idOffer the id of the offer the user is interested in.
   * @param idUser  the id of the interested user.
   * @return true if there is no such interest, else otherwise.
   */
  boolean isAlreadyInterested(int idOffer, int idUser);

  /**
   * Check if the offeror is the same than the user.
   *
   * @param idOffer the id of the offer the user is interested in.
   * @param idUser  the id of teh interested user.
   * @return true if the offeror and given user is the same, else otherwise.
   */
  boolean offerIsMine(int idOffer, int idUser);

  OfferDto getOne(int idOffer);

  OfferDto getOne(ObjectDto objectDto);

  /**
   * Prepare a request to the database to get a list of all objects, sorted as per requested.
   *
   * @param sortedBy      sort in which the objects need to be listed.
   * @param userRequester the user that makes the request, 0 if none.
   * @param filterType    the nature of the filter applied on the offers' list.
   * @param filterValue   the value of the filter.
   * @return a list of ObjectDto that contains all the objects.
   */
  List<OfferDto> getAll(String sortedBy, UserDto userRequester, String filterType,
      String filterValue);

  /**
   * Prepare a request to the database to get a list of all objects of a certain user.
   *
   * @param idUser the relevant user.
   */
  List<OfferDto> getAllAdmin(int idUser);

  /**
   * This is oly for non-Authenticated users. Prepare a request to the database to get a list of all
   * objects, the newest first.
   *
   * @return a list of ObjectDto that contains all the objects.
   */
  List<OfferDto> getAllDisconnected();

  /**
   * Add the offer to the database.
   *
   * @param object given by the user.
   * @return the offer that has been added in the database.
   */
  OfferDto addOne(ObjectDto object);

  /**
   * Put the canceled field off the offer to true.
   *
   * @param idOffer the id of the offer.
   */
  void updateOfferState(int idOffer, String newState);

  boolean addRecipient(int idOffer, int idUser);

  /**
   * Update the state of all the offers of the user that are not outdated.
   *
   * @param idUser   the id of the user.
   * @param newState the state to apply.
   */
  void updateOffersAvailability(int idUser, String newState);
}
