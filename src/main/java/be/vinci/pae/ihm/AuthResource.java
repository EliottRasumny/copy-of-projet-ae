package be.vinci.pae.ihm;

import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.ihm.filters.LoggedIn;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.token.TokenManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/auths")
public class AuthResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private UserUcc myUserUcc;
  @Inject
  private TokenManager tokenManager;

  /**
   * Check all ihm-checks and then, try to log in the user.
   *
   * @param user : the user who want to log in
   * @return An ObjectNode with the user and a token if the login worked. Return only the user
   *      (without a token) if the state of the user is not "valid". Throw an Exception if noting
   *       worked.
   */
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(UserDto user) {
    // Get and check credentials
    if (user == null || user.getUsername() == null || user.getPassword() == null) {
      throw new BadRequestException("Username or password required");
    }
    // Check username and password
    user = myUserUcc.login(user.getUsername(), user.getPassword());
    ObjectNode objectNode = jsonMapper.createObjectNode();
    try {
      objectNode.put(Constants.USER, jsonMapper.writeValueAsString(user));
      // Return a token if the user has a valid state
      if (user.getState().equals(Constants.USER_STATE_VALID)) {
        objectNode.put(Constants.TOKEN, tokenManager.encrypt(user.getIdUser()));
      }
    } catch (JsonProcessingException e) {
      throw new FatalException(
          "Error login() - Impossible to write values in the jsonMapper for the user.");
    }
    return objectNode;
  }

  /**
   * check all ihm-checks and then, try to login the user.
   *
   * @param userFront : the user who want to login
   * @return true if the login worked, throw an Exception if not
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto register(UserDto userFront) {
    // check credentials
    if (userFront == null
        || userFront.getUsername() == null
        || userFront.getSurname() == null
        || userFront.getLastname() == null
        || userFront.getAddress() == null
        || userFront.getAddress().getStreet() == null
        || userFront.getAddress().getBuildingNumber() == null
        || userFront.getAddress().getPostcode() == null
        || userFront.getAddress().getCommune() == null
        || userFront.getPassword() == null) {
      throw new BadRequestException("Missing register information");
    }
    // Try to register
    return myUserUcc.register(userFront);
  }

  /**
   * Check if the token is valid.
   *
   * @return the user's info of the valid token, false if the token is not valid
   */
  @GET
  @Path("token")
  @Produces(MediaType.APPLICATION_JSON)
  @LoggedIn
  public UserDto checkTokenAtLaunch(@Context ContainerRequest request) {
    return (UserDto) request.getProperty(Constants.USER);
  }
}
