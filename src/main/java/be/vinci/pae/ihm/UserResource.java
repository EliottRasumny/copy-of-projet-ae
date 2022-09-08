package be.vinci.pae.ihm;

import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.ihm.filters.Authenticated;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.ForbiddenException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/users")
public class UserResource {

  @Inject
  private UserUcc myUserUcc;

  /**
   * Check all ihm-checks and then, try to get all informations of a user.
   */
  @GET
  @Path("{id}")
  @Authenticated
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto getOne(@PathParam("id") int id, @Context ContainerRequest requester) {
    UserDto userRequester = (UserDto) requester.getProperty(Constants.USER);
    if (userRequester.getIdUser() != id && !userRequester.getRole()
        .equals(Constants.USER_ROLE_ADMIN)) {
      throw new ForbiddenException("You have not the right to access this resource.");
    }
    UserDto user = myUserUcc.getUser(id);
    if (user == null) {
      throw new FatalException("Impossible to retrieve data.");
    }
    return user;
  }

  /**
   * Check all QueryParam and call the good request to have all users with the good filter.
   *
   * @return All users with the good filter.
   */
  @GET
  @Authenticated
  @Authorize
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<UserDto> getAll(@QueryParam(Constants.OFFER_FILTER_VALUE) String value,
      @QueryParam(Constants.OFFER_FILTER_TYPE) String type) {
    return myUserUcc.getAllUsers(value, type);
  }

  /**
   * Confirm the inscription of a user.
   *
   * @param idOfUserToConfirm : the id of the user we want to valid
   * @param body              : the body that contain the boolean admin
   * @return the user if the confirmation worked, throw an exception otherwise
   */
  @PATCH
  @Path("{id}/confirm")
  @Authenticated
  @Authorize
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto confirmInscription(JsonNode body, @PathParam("id") int idOfUserToConfirm) {
    // Check credentials
    if (idOfUserToConfirm == 0
        || !body.hasNonNull(Constants.USER_FUTURE_ADMIN)
        || !body.hasNonNull(Constants.USER_VERSION)) {
      throw new BadRequestException("Missing information about the user to confirm.");
    }
    boolean futurAdmin = body.get(Constants.USER_FUTURE_ADMIN).asBoolean();
    int version = body.get(Constants.USER_VERSION).asInt();
    return myUserUcc.confirmInscription(idOfUserToConfirm, futurAdmin, version);
  }

  /**
   * Promote a user to be an admin.
   *
   * @param idOfUserToPromote : the id of the user we want to promote
   * @param version           the version of the user
   * @return the user if the confirmation worked, throw an exception otherwise
   */
  @PATCH
  @Path("{id}/promote")
  @Authenticated
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto confirmInscription(@PathParam("id") int idOfUserToPromote,
      @QueryParam(Constants.USER_VERSION) int version) {
    // Check credentials
    if (idOfUserToPromote == 0 || version == 0) {
      throw new BadRequestException("Missing information about the user to promote.");
    }
    return myUserUcc.promoteUser(idOfUserToPromote, version);
  }

  /**
   * Refuse the inscription of a user.
   *
   * @param idOfUserToRefuse : the id of the user we want to refuse
   * @param body             : the body that contain the refusalReason
   */
  @PATCH
  @Path("{id}/refuse")
  @Authenticated
  @Authorize
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto refuseInscription(JsonNode body, @PathParam("id") int idOfUserToRefuse) {
    // Check credentials
    if (idOfUserToRefuse == 0
        || !body.hasNonNull(Constants.USER_REFUSAL_REASON)
        || !body.hasNonNull(Constants.USER_VERSION)) {
      throw new BadRequestException("Missing informations.");
    }
    String refusalReason = body.get(Constants.USER_REFUSAL_REASON).asText();
    int version = body.get(Constants.USER_VERSION).asInt();

    return myUserUcc.refuseInscription(idOfUserToRefuse, refusalReason, version);
  }

  /**
   * modify user info.
   *
   * @param userFront : the user with his new info
   */
  @PUT
  @Path("{id}/modify")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDto modifyUserInfo(UserDto userFront, @PathParam("id") int idUser) {
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
        || userFront.getAddress().getVersionAddress() == 0
        || userFront.getVersion() == 0
    ) {
      throw new BadRequestException("Missing information");
    }
    userFront.setIdUser(idUser);
    return myUserUcc.modify(userFront);
  }

  /**
   * Set the user's state to "unavailable".
   *
   * @param idUser  the id of the user.
   * @param version the version of the user
   * @return true if the change worked, false if not.
   */
  @PATCH
  @Path("{id}/unavailable")
  @Authenticated
  @Authorize
  @Consumes(MediaType.APPLICATION_JSON)
  public boolean setUserStateToUnavailable(@PathParam("id") int idUser,
      @QueryParam(Constants.USER_VERSION) int version) {
    if (idUser <= 0 || version == 0) {
      throw new BadRequestException("The user parameter is not valid.");
    }
    return myUserUcc.setUserStateToUnavailable(idUser, version);
  }

  /**
   * Check all ihm-checks and then, try to get all the inscriptions requests.
   */
  @GET
  @Path("inscriptions")
  @Authenticated
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public List<UserDto> displayAllInscriptionRequests() {

    List<UserDto> list = myUserUcc.getInscriptionRequests();
    if (list == null) {
      throw new FatalException("Impossible to retrieve data.");
    }
    return list;
  }

  /**
   * Check all ihm-checks and then, try to get all the denied users.
   */
  @GET
  @Path("refusals")
  @Authenticated
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public List<UserDto> displayDeniedInscriptionRequests() {
    List<UserDto> list = myUserUcc.getDeniedInscriptionRequests();
    if (list == null) {
      throw new FatalException("Impossible to retrieve data.");
    }
    return list;
  }
}
