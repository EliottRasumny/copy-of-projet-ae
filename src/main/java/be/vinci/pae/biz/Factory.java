package be.vinci.pae.biz;

import be.vinci.pae.biz.address.AddressDto;
import be.vinci.pae.biz.interest.InterestDto;
import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.user.UserDto;

public interface Factory {

  /**
   * Create a new User.
   *
   * @return the newly created user.
   */
  UserDto getUser();

  /**
   * Create a new Address.
   *
   * @return the newly created address.
   */
  AddressDto getAddress();

  /**
   * Create a new Object.
   *
   * @return the newly created object.
   */
  ObjectDto getObject();

  /**
   * Create a new Offer.
   *
   * @return the newly created offer.
   */
  OfferDto getOffer();

  /**
   * Create a new Interest.
   *
   * @return the newly created interest.
   */
  InterestDto getInterest();

  /**
   * Create a new Type.
   *
   * @return the newly created type.
   */
  TypeDto getType();

  /**
   * Create a new Rating.
   *
   * @return the newly created rating.
   */
  RatingDto getRating();
}
