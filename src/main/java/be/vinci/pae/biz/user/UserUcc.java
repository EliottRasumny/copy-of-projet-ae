package be.vinci.pae.biz.user;

import java.util.List;

public interface UserUcc {

  /**
   * check user unique and delegate the following things : hash password, set state and insert user
   * into db.
   *
   * @param userDto : user to registered
   * @return user that has been added in the db
   */
  UserDto register(UserDto userDto);

  /**
   * Try to log in the user by testing its username and its password. Don't check the state of the
   * user because we need to return the refusalReason of the user
   *
   * @param username of the user to log in.
   * @param password of the user to log in.
   * @return the user if the log is valid.
   */
  UserDto login(String username, String password);

  /**
   * Get the user with the given id.
   *
   * @param id of the user that needs to be retrieved.
   * @return the user with the corresponding id.
   */
  UserDto getUser(int id);

  /**
   * Confirm the inscription of a user.
   *
   * @param id         of the user which inscription needs to be accepted.
   * @param futurAdmin if the user will be an admin
   * @param version    the version of the user
   * @return true if the inscription is validated, false otherwise.
   */
  UserDto confirmInscription(int id, Boolean futurAdmin, int version);

  /**
   * Refuse the inscription of a user.
   *
   * @param id            of the user which inscription needs to be revoked.
   * @param refusalReason the refusal reason
   * @param version       the version of the user
   * @return true if the inscription is revoked, false otherwise.
   */
  UserDto refuseInscription(int id, String refusalReason, int version);

  /**
   * Retrieve all the users that are waiting for inscription approval.
   *
   * @return a list of all the corresponding users.
   */
  List<UserDto> getInscriptionRequests();

  /**
   * Retrieve all the users that are not approved.
   *
   * @return a list of all the corresponding users.
   */
  List<UserDto> getDeniedInscriptionRequests();

  UserDto modify(UserDto userDto);

  /**
   * Get all users corresponding to the sort.
   *
   * @param value the letters contain by the type
   * @param type  the type of the research
   * @return all the corresponding users
   */
  List<UserDto> getAllUsers(String value, String type);

  /**
   * Promote a user to be an admin.
   *
   * @param id      : the id of the user we want to promote
   * @param version the version of the user
   * @return the user if the confirmation worked, throw an exception otherwise
   */
  UserDto promoteUser(int id, int version);

  /**
   * Change the state of the user to 'unavailable'.
   *
   * @param idUser  the id of the user.
   * @param version the version of the user
   * @return true if the operation is done.
   */
  boolean setUserStateToUnavailable(int idUser, int version);
}
