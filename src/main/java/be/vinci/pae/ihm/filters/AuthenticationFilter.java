package be.vinci.pae.ihm.filters;

import static be.vinci.pae.utils.Constants.AUTHORIZATION;
import static be.vinci.pae.utils.Constants.USER;
import static be.vinci.pae.utils.Constants.USER_ROLE;

import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import be.vinci.pae.utils.token.TokenManager;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Priority(1)
@Provider
@Authenticated
public class AuthenticationFilter implements ContainerRequestFilter {

  @Inject
  private UserUcc myUserUcc;
  @Inject
  private TokenManager tokenManager;

  /**
   * Check the token of a user. Throw an exception if the user is not authenticated.
   *
   * @param requestContext the request with the JWT
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    String token = requestContext.getHeaderString(AUTHORIZATION);
    if (token == null) {
      throw new UnauthorizedException("A token is required to access this resource.");
    } else {
      // Try to get a user with the encrypted id in the token.
      UserDto authenticatedUser = myUserUcc.getUser(tokenManager.decrypt(token));
      if (authenticatedUser == null) {
        throw new UnauthorizedException("No access right for this resource.");
      }
      // Store the role value in the header of the request. This way, if we need to check it within
      // another filter, we don't need to remake all the process (like decrypting the token).
      requestContext.getHeaders().add(USER_ROLE, String.valueOf(authenticatedUser.getRole()));
      // Store the user data in the properties so that we can access it after with the @Context.
      requestContext.setProperty(USER, authenticatedUser);
    }
  }
}
