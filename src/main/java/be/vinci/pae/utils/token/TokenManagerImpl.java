package be.vinci.pae.utils.token;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.TokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;

public class TokenManagerImpl implements TokenManager {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auth0")
      .build();

  @Override
  public int decrypt(String token) {
    try {
      return jwtVerifier.verify(token).getClaim("user").asInt();
    } catch (Exception e) {
      throw new TokenException("Error : decrypt(token) " + e.getMessage());
    }
  }

  @Override
  public String encrypt(int idUser) {
    try {
      return JWT.create().withIssuer("auth0")
          .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))//24h
          .withClaim("user", idUser).sign(this.jwtAlgorithm);
    } catch (IllegalArgumentException e) {
      throw new TokenException("Error : crypt(token) " + e.getMessage());
    }
  }
}
