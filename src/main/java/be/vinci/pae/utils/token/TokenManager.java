package be.vinci.pae.utils.token;

public interface TokenManager {

  /**
   * Decrypt a given token and give the id hidden in it.
   *
   * @param token that needs to be decrypted.
   * @return the hidden id of the token's user.
   */
  int decrypt(String token);

  /**
   * Encrypt a token with the id of a user.
   *
   * @param idUser the id that needs to be embedded in it.
   * @return the token as a string.
   */
  String encrypt(int idUser);
}
