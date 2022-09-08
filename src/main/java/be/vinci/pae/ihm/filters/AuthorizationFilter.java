package be.vinci.pae.ihm.filters;

import static be.vinci.pae.utils.Constants.USER_ROLE;
import static be.vinci.pae.utils.Constants.USER_ROLE_ADMIN;

import be.vinci.pae.utils.exceptions.ForbiddenException;
import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Priority(2)
@Provider
@Authorize
public class AuthorizationFilter implements ContainerRequestFilter {

  /**
   * Check if the role of the user contained in the header of the request is 'admin'.
   *
   * @param requestContext the request the role of the user.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (!requestContext.getHeaders().get(USER_ROLE).get(0).equals(USER_ROLE_ADMIN)) {
      throw new ForbiddenException("You are not allowed to access this resource.");
    }
  }
}
