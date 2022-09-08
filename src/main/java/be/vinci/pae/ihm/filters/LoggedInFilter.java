package be.vinci.pae.ihm.filters;

import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.token.TokenManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Provider
@LoggedIn
public class LoggedInFilter implements ContainerRequestFilter {

  @Inject
  private UserUcc myUserUcc;
  @Inject
  private TokenManager tokenManager;

  /**
   * Check the token of a user. Do not throw an exception if the user is not authenticated.
   *
   * @param requestContext the request with the JWT
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    String token = requestContext.getHeaderString(Constants.AUTHORIZATION);
    // Set the property to null by default. We will change it only if conditions are satisfied.
    requestContext.setProperty(Constants.USER, null);
    if (token != null) {
      // We don't want to trigger a decoding token error if the 'token' received in the request is
      // not even well formatted.
      String[] tokenParts = token.split("\\.");
      if (tokenParts.length == 3) {
        // Try to get a user with the encrypted id in the token.
        UserDto authenticatedUser = myUserUcc.getUser(tokenManager.decrypt(token));
        if (authenticatedUser != null) {
          // All the conditions are satisfied, so we change the 'user' property with the real user.
          requestContext.setProperty(Constants.USER, authenticatedUser);
        }
      }
    }
  }
}
