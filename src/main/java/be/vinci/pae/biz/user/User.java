package be.vinci.pae.biz.user;

/**
 * Interface of User object.
 */
public interface User extends UserDto {

  /**
   * check the business conditions, hash the password and add a state to the user.
   *
   * @param user : user to register
   * @return user after register
   */
  User register(User user);

  /**
   * Hash a password.
   *
   * @param user the user with a non-hashed pwd
   * @return the user with a hasshed pwd
   */
  User hashPwd(User user);

  /**
   * Check if the password of the user is the same as the one passed on parameter.
   *
   * @param pwHashed to compare to the password of the user.
   * @return true if the password matches, false otherwise.
   */
  boolean checkPassword(String pwHashed);

  /**
   * Check if the user is not already confirmed and if it can, modify the state, the refusalReason
   * and the role of the user.
   *
   * @param role the new role of the user
   * @return true if the user is not already confirmed
   */
  boolean confirmInscription(String role);

  /**
   * Check if the user can be refused and if it can, modify the state and the refusalReason.
   *
   * @param refusalReason the reason of the refusal
   * @return true if the user can be refused
   */
  boolean refuseInscription(String refusalReason);

  enum UserState {
    VALID("valid"),
    REGISTERED("registered"),
    DENIED("denied"),
    ;
    private final String userState;

    UserState(String sort) {
      this.userState = sort;
    }

    public String get() {
      return userState;
    }
  }
}
