package be.vinci.pae.biz.user;

import be.vinci.pae.biz.address.AddressDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * DTO of User object.
 */
@JsonDeserialize(as = UserImpl.class)
public interface UserDto {

  /**
   * Get the id of the user.
   *
   * @return the id.
   */
  int getIdUser();

  /**
   * Set a new id to the user.
   *
   * @param idUser new id of the user.
   */
  void setIdUser(int idUser);

  /**
   * Get the role of the user.
   *
   * @return the role.
   */
  String getRole();

  /**
   * Set a new role to the user.
   *
   * @param role new role of the user.
   */
  void setRole(String role);

  /**
   * Get the username of the user.
   *
   * @return the username.
   */
  String getUsername();

  /**
   * Set a new username to the user.
   *
   * @param username new username of the user.
   */
  void setUsername(String username);

  /**
   * Get the lastname of the user.
   *
   * @return the lastname.
   */
  String getLastname();

  /**
   * Set a new lastname to the user.
   *
   * @param lastname new lastname of the user.
   */
  void setLastname(String lastname);

  /**
   * Get the surname of the user.
   *
   * @return the surname.
   */
  String getSurname();

  /**
   * Set a new surname to the user.
   *
   * @param surname new surname of the user.
   */
  void setSurname(String surname);

  /**
   * Get the address of the user.
   *
   * @return the address.
   */
  AddressDto getAddress();

  /**
   * Set a new address to the user.
   *
   * @param address new address of the user.
   */
  void setAddress(AddressDto address);

  /**
   * Get the phoneNumber of the user.
   *
   * @return the phoneNumber.
   */
  String getPhoneNumber();

  /**
   * Set a new phoneNumber to the user.
   *
   * @param phoneNumber new phoneNumber of the user.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the password of the user.
   *
   * @return the password.
   */
  String getPassword();

  /**
   * Set a new password to the user.
   *
   * @param password new password of the user.
   */
  void setPassword(String password);

  /**
   * Get the refusalReason of the user.
   *
   * @return the refusalReason.
   */
  String getRefusalReason();

  /**
   * Set a new refusalReason to the user.
   *
   * @param refusalReason new refusalReason of the user.
   */
  void setRefusalReason(String refusalReason);

  /**
   * Get the state of the user.
   *
   * @return the state.
   */
  String getState();

  /**
   * Set a new state to the user.
   *
   * @param state new state of the user.
   */
  void setState(String state);

  int getVersion();

  void setVersion(int version);

}
