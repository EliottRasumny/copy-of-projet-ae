package be.vinci.pae.biz;

import be.vinci.pae.biz.address.Address;
import be.vinci.pae.biz.address.AddressImpl;
import be.vinci.pae.biz.interest.InterestDto;
import be.vinci.pae.biz.interest.InterestImpl;
import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.object.ObjectImpl;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.offer.OfferImpl;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.biz.rating.RatingImpl;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.type.TypeImpl;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.biz.user.UserImpl;

public class FactoryImpl implements Factory {

  @Override
  public User getUser() {
    return new UserImpl();
  }

  @Override
  public Address getAddress() {
    return new AddressImpl();
  }

  @Override
  public ObjectDto getObject() {
    return new ObjectImpl();
  }

  @Override
  public OfferDto getOffer() {
    return new OfferImpl();
  }

  @Override
  public InterestDto getInterest() {
    return new InterestImpl();
  }

  @Override
  public TypeDto getType() {
    return new TypeImpl();
  }

  @Override
  public RatingDto getRating() {
    return new RatingImpl();
  }

}
