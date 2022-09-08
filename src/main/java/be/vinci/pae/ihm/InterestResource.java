package be.vinci.pae.ihm;

import static be.vinci.pae.utils.Constants.OFFER;
import static be.vinci.pae.utils.Constants.USER;

import be.vinci.pae.biz.interest.InterestDto;
import be.vinci.pae.biz.interest.InterestUcc;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.ihm.filters.Authenticated;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.BadRequestException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
@Path("/interests")
public class InterestResource {

  @Inject
  private InterestUcc myInterestUcc;

  /**
   * Check all QueryParam and call the good request to have all users with the good filter.
   *
   * @return All users with the good filter.
   */
  @GET
  @Authenticated
  @Produces(MediaType.APPLICATION_JSON)
  public List<InterestDto> getAll(@QueryParam("idOffer") int idOffer,
      @QueryParam("idMember") int idMember) {
    return myInterestUcc.getInterests(idOffer, idMember);
  }

  /**
   * Check all QueryParam and call the good request to have all users with the good filter.
   *
   * @return All users with the good filter.
   */
  @GET
  @Authenticated
  @Path("get-recipient")
  @Produces(MediaType.APPLICATION_JSON)
  public InterestDto getRecipient(@QueryParam("idOffer") int idOffer,
      @QueryParam("idMember") int idMember) {
    return myInterestUcc.getRecipient(idOffer, idMember);
  }

  /**
   * Add a receiver for an offer.
   *
   * @param idOffer     the object the user is interested in
   * @param idRecipient the recipient
   */
  @PUT
  @Path("add-recipient")
  @Authenticated
  public void addRecipient(@QueryParam(USER) int idRecipient,
      @QueryParam(OFFER) int idOffer, @QueryParam(Constants.VERSION) int objectVersion) {
    // Check credentials
    if (idOffer == 0 || idRecipient == 0 || objectVersion == 0) {
      throw new BadRequestException("Missing information.");
    }
    myInterestUcc.addRecipient(idOffer, idRecipient, objectVersion);
  }

  /**
   * Indicate that the recipient has come.
   *
   * @param idOffer       the offer related.
   * @param idRecipient   the recipient.
   * @param objectVersion the version of the object
   */
  @PATCH
  @Path("indicate-has-come")
  @Authenticated
  public void indicateRecipientHasCome(@QueryParam(USER) int idRecipient,
      @QueryParam(OFFER) int idOffer, @QueryParam(Constants.VERSION) int objectVersion) {
    // Check credentials
    if (idOffer == 0 || idRecipient == 0 || objectVersion == 0) {
      throw new BadRequestException("Missing information.");
    }
    myInterestUcc.indicateRecipientHasCome(idOffer, idRecipient, objectVersion);
  }

  /**
   * is member interested.
   *
   * @return true if the user is interested, false otherwise.
   */
  @GET
  @Authenticated
  @Path("{id}/is-interested")
  public boolean isInterested(@PathParam("id") int idOffer,
      @Context ContainerRequest request) {
    int idUser = ((UserDto) request.getProperty(USER)).getIdUser();
    return myInterestUcc.isInterested(idOffer, idUser);
  }

  /**
   * Get the interests not read where the user is the offeror of the offer or where he has been
   * chosen as receiver.
   *
   * @return the list of all the interests corresponding to the request
   */
  @GET
  @Authenticated
  @Path("notification")
  @Produces(MediaType.APPLICATION_JSON)
  public List<InterestDto> getInterestedAndRecipeint(@Context ContainerRequest request) {
    int idUser = ((UserDto) request.getProperty(USER)).getIdUser();
    return myInterestUcc.getInterestedAndRecipeint(idUser);
  }

}
