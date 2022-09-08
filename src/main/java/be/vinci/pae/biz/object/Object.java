package be.vinci.pae.biz.object;

import be.vinci.pae.biz.user.User;

public interface Object extends ObjectDto {

  /**
   * Check if the state of the object is one of the possible states.
   *
   * @param state of the object that needs to be checked.
   * @return true if the state exist/is valid, false otherwise.
   */
  boolean isStateOk(String state);

  /**
   * Check if the offer of an object can be marked as interest.
   *
   * @return true if it can
   */
  boolean isStateMarkable();


  /**
   * Check if the object can be assigned and modify the object to update it in the DB.
   *
   * @param recipient the recipient chosen for the offer
   * @return true it can be assigned
   */
  boolean checkAndModifyToAddARecipient(User recipient);
}
