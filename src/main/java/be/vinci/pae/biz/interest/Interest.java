package be.vinci.pae.biz.interest;

import be.vinci.pae.biz.offer.OfferDto;

public interface Interest extends InterestDto {

  void prepareToAdd(OfferDto offer, int idUser, String date, String phoneNumber);
}
