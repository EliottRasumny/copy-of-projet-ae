package be.vinci.pae.dal.object;

import be.vinci.pae.biz.object.ObjectDto;

public interface ObjectDao {

  /**
   * Get the object of the database.
   *
   * @param id the id of the object we looking for
   * @return object with corresponding id, null if there is no such object
   */
  ObjectDto getOne(int id);

  /**
   * Add the object to the database.
   *
   * @param object given by the user
   * @param idUser of the user that offers the object
   * @return the object that has been added in the database
   */
  ObjectDto addOne(ObjectDto object, int idUser);

  /**
   * Update the state of the Object. Throw a FatalException if it didn't work.
   *
   * @param id      the id
   * @param state   the new state
   * @param version the version
   */
  void updateState(int id, String state, int version);


  /**
   * Update every fields of the object and throw an FatalException if it didn't worked.
   *
   * @param newObject the object to update
   */
  void update(ObjectDto newObject);

  /**
   * Remove the recipient of an object.
   *
   * @param idObject the id of the objects
   * @param version  the version
   */
  void removeRecipient(int idObject, int version);

}
