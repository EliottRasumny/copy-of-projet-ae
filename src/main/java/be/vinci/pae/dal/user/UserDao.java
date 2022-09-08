package be.vinci.pae.dal.user;

import be.vinci.pae.biz.user.UserDto;
import java.util.List;

public interface UserDao {

  /**
   * Prepare a request to the database to get the user with the indicated username.
   *
   * @param username the username of the user.
   * @return the user found or null otherwise.
   */
  UserDto getOne(String username);

  /**
   * Prepare a request to the database to get the user with the indicated id.
   *
   * @param id the id of the user.
   * @return the user found or null otherwise.
   */
  UserDto getOne(int id);

  /**
   * Prepare a request to the database to update every attribute of a user.
   *
   * @param newUser the user to update.
   */
  void update(UserDto newUser);

  /**
   * Prepare a request to the database to add a user in the DB.
   *
   * @param user the user to add.
   * @return the user with his id.
   */
  UserDto addOne(UserDto user);

  /**
   * Prepare a request to the database to get a list of all users which have the same state.
   *
   * @param state of the state in common.
   * @return a list of UserDto that contains all the users requested.
   */
  List<UserDto> getUsers(String state);

  /**
   * Prepare a request to the database to update the phone number of a user. Throw a FatalException
   * if the update couldn't be done. Don't need the version of the user because you can not have 2
   * interests on the same offer.
   *
   * @param number the phone number to update.
   * @param userId the id of the user that needs a phone number update.
   */
  void updatePhoneNumberOnInterest(int userId, String number);

  UserDto modify(UserDto user);

  /**
   * Prepre a request to the database to get a list of all users which correspond to the sort.
   *
   * @param value the letters the pseudo of the type of research need to contains
   * @param type  the type of the research
   * @return the users corresponding to the research
   */
  List<UserDto> getAllUsers(String value, String type);
}
