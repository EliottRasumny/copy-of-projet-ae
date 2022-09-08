package be.vinci.pae.biz.offer;

import be.vinci.pae.biz.object.ObjectDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.sql.Timestamp;

/**
 * DTO of Offer offer.
 */
@JsonDeserialize(as = OfferImpl.class)
public interface OfferDto {

  int getIdOffer();

  void setIdOffer(int idOffer);

  String getUsernameOfferor();

  void setUsernameOfferor(String usernameOfferor);

  ObjectDto getObject();

  void setObject(ObjectDto object);

  Timestamp getDate();

  void setDate(Timestamp date);

  String getState();

  void setState(String state);

  /**
   * This is an attribute that is NOT in the DB. This is only to transport data from layer to
   * layer.
   *
   * @return the number of interest that the offer has
   */
  int getNbrInterests();

  /**
   * This is an attribute that is NOT in the DB. This is only to transport data from layer to
   * layer.
   */
  void setNbrInterests(int nbrInterests);


  /**
   * This is an attribute that is NOT in the DB. This is only to transport data from layer to layer
   *
   * @return true if the requester is interested in the offer
   */
  boolean isMyInterest();

  /**
   * This is an attribute that is NOT in the DB. This is only to transport data from layer to
   * layer.
   */
  void setMyInterest(boolean myInterest);
}
