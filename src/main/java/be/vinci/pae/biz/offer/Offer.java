package be.vinci.pae.biz.offer;

public interface Offer extends OfferDto {

  /**
   * Check if the user can mark his interest in the offer.
   *
   * @param idUser the id of the user
   * @return true if he can, false otherwise
   */
  boolean canBeMarkedAsInterest(int idUser);

}
