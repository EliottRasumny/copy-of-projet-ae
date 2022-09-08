package be.vinci.pae.biz.object;

import be.vinci.pae.biz.offer.OfferDto;
import java.io.InputStream;

public interface ObjectUcc {

  /**
   * check in the database if the object exists.
   *
   * @param id : id of the object we looking for
   * @return object with the corresponding id, throw an Exception if he doesn't exist
   */
  ObjectDto getObject(int id);

  /**
   * modify the object in the DB.
   *
   * @param objectFront object given by the user
   * @param idOffer     the id of the offer
   * @param file        the file of the picture given by the user
   * @param filename    the name of the file
   * @return return the offer with the modified object
   */
  OfferDto modifyObject(ObjectDto objectFront, int idOffer, InputStream file, String filename);

}
