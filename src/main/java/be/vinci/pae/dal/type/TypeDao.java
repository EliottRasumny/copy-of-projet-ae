package be.vinci.pae.dal.type;

import be.vinci.pae.biz.type.TypeDto;

public interface TypeDao {


  /**
   * Give the type with the corresponding label.
   *
   * @param label of the type we want
   * @return the type that has the corresponding label, null if there isn't
   */
  TypeDto getOne(String label);

  /**
   * Give the type with the corresponding id.
   *
   * @param id of the type we want
   * @return the type that has the corresponding id, null if there isn't
   */
  TypeDto getOne(int id);


  /**
   * Add the type to the database.
   *
   * @param label given by the user.
   * @return the type that has been added in the database.
   */
  TypeDto addOne(String label);

}
