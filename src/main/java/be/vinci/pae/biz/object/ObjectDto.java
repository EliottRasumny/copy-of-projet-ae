package be.vinci.pae.biz.object;


import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.user.UserDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * DTO of Object object.
 */
@JsonDeserialize(as = ObjectImpl.class)
public interface ObjectDto {

  /**
   * Get the id of the object.
   *
   * @return the id.
   */
  int getIdObject();

  /**
   * Set a new id to the object.
   *
   * @param idObject new id of the object.
   */
  void setIdObject(int idObject);

  /**
   * Get the type of the object.
   *
   * @return the type.
   */
  TypeDto getType();

  /**
   * Set a new type to the object.
   *
   * @param type new id of the object.
   */
  void setType(TypeDto type);

  /**
   * Get the description of the object.
   *
   * @return the description.
   */
  String getDescription();

  /**
   * Set a new description to the object.
   *
   * @param description new id of the object.
   */
  void setDescription(String description);

  /**
   * Get the picture of the object by its name, which is the same as the object id.
   *
   * @return the picture's name. 0 if there is none
   */
  String getPictureName();

  /**
   * Set a new picture to the object. The picture's name is the id of the object. 0 is the same as
   * none.
   *
   * @param pictureName name of the new picture.
   */
  void setPictureName(String pictureName);

  /**
   * Get the timeSlot of the object.
   *
   * @return the timeSlot.
   */
  String getTimeSlot();

  /**
   * Set a new timeSlot to the object.
   *
   * @param timeSlot new id of the object.
   */
  void setTimeSlot(String timeSlot);

  /**
   * Get the offeror of the object.
   *
   * @return the offeror.
   */
  UserDto getOfferor();

  /**
   * Set a new offeror to the object.
   *
   * @param offeror new id of the object.
   */
  void setOfferor(UserDto offeror);

  /**
   * Get the recipient of the object.
   *
   * @return the recipient.
   */
  UserDto getRecipient();

  /**
   * Set a new recipient to the object.
   *
   * @param recipient new id of the object.
   */
  void setRecipient(UserDto recipient);

  /**
   * Get the state of the object.
   *
   * @return the state.
   */
  String getState();

  /**
   * Set a new state to the object.
   *
   * @param state new id of the object.
   */
  void setState(String state);

  int getVersion();

  void setVersion(int version);
}
