package be.vinci.pae.ihm;

import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.offer.OfferUcc;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.ihm.filters.Authenticated;
import be.vinci.pae.ihm.filters.LoggedIn;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.ForbiddenException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/offers")
public class OfferResource {

  //private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private OfferUcc myOfferUcc;

  /**
   * Retrieve all offers sorted in a certain way. Those offers can be filtered.
   *
   * @param sort          the sort required.
   * @param filterType    the nature of the filter applied on the offers' list.
   * @param filterValue   the value of the filter.
   * @param userOfRequest the user that emitted the request.
   * @return all the offers.
   */
  @GET
  @LoggedIn
  @Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
  @Produces(MediaType.APPLICATION_JSON)
  public List<OfferDto> getAll(@QueryParam(Constants.OFFER_SORT) String sort,
      @QueryParam(Constants.OFFER_FILTER_VALUE) String filterValue,
      @QueryParam(Constants.OFFER_FILTER_TYPE) String filterType,
      @Context ContainerRequest userOfRequest) {
    // Check if the query parameter 'sort' exist. It is mandatory for it to have a value.
    if (sort == null || sort.equals("")) {
      throw new BadRequestException("Sort option field is empty.");
    }
    // Get the result of the check to verify if the user is authenticated. If it is authenticated
    // the value contains the user, if not, it is null.
    UserDto user = (UserDto) userOfRequest.getProperty(Constants.USER);
    // If the filter has no relevant value
    if (filterType == null || filterType.equals("") || filterValue == null || filterValue.equals(
        "")) {
      return myOfferUcc.getAll(sort, user, null, null);
    }
    // If the user is not authenticated and want to access other resources than the default one
    // thanks to the filter.
    if (user == null) {
      throw new ForbiddenException("The user is not authenticated and can't access the resource.");
    }
    if (filterType.equals(Constants.USER)) {
      if (user.getRole().equals(Constants.USER_ROLE_ADMIN)) {
        int idUser = Integer.parseInt(filterValue);
        return myOfferUcc.getAllAdmin(idUser);
      } else {
        throw new ForbiddenException("The actual user has not the right to accces this resource.");
      }
    } else {
      return myOfferUcc.getAll(sort, user, filterType, filterValue);
    }
  }


  /**
   * Create an offer that contain a new object in the database.
   *
   * @param object          the object given by the user
   * @param fileDisposition the info of the file including the filename
   * @param file            the file given by the user
   * @return the Offer created in the database
   */
  @POST
  @Path("create")
  @Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public OfferDto createOffer(@FormDataParam(Constants.OBJECT) String object,
      @FormDataParam(Constants.FILE) InputStream file,
      @FormDataParam(Constants.FILE) FormDataContentDisposition fileDisposition,
      @Context ContainerRequest request) {
    JsonMapper mapper = new JsonMapper();
    ObjectDto objectFront;
    try {
      objectFront = mapper.readValue(object, ObjectDto.class);
    } catch (JsonProcessingException e) {
      throw new FatalException("Error : the mapper didn't succeed to transform json into object");
    }
    // check credentials
    if (objectFront == null || objectFront.getType() == null || objectFront.getTimeSlot() == null
        || objectFront.getDescription() == null) {
      throw new BadRequestException("Missing createObject information");
    }

    int id = ((UserDto) request.getProperty(Constants.USER)).getIdUser();
    String filename = null;
    if (fileDisposition != null) {
      filename = fileDisposition.getFileName();
    }

    return myOfferUcc.createOffer(objectFront, id, file, filename);
  }

  /**
   * Remove the recipient of the object, set has_come of the user to false and set the state of the
   * object to "assignable".
   *
   * @param idOffer       the id of the offer
   * @param oldRecipient  the id of the old recipient
   * @param objectVersion the version of the object
   */
  @POST
  @Path("new-recipient")
  @Authenticated
  public void removeOldRecipient(@QueryParam(Constants.OFFER) int idOffer,
      @QueryParam(Constants.OLD_RECIPIENT) int oldRecipient,
      @QueryParam(Constants.VERSION) int objectVersion) {
    // check credentials
    if (idOffer == 0 || oldRecipient == 0) {
      throw new BadRequestException("Missing removeOldRecipient information");
    }
    myOfferUcc.removeOldRecipient(idOffer, oldRecipient, objectVersion);
  }

  /**
   * Create a new offer based on the old one and put the state of the old one to cancel.
   *
   * @param idOffer       the id of the offer
   * @param oldRecipient  the id of the old recipient
   * @param objectVersion the version of the object
   */
  @POST
  @Path("offer-again")
  @Authenticated
  public void offerAgain(@QueryParam(Constants.OFFER) int idOffer,
      @QueryParam(Constants.OLD_RECIPIENT) int oldRecipient,
      @QueryParam(Constants.VERSION) int objectVersion) {
    // check credentials
    if (idOffer == 0 || oldRecipient == 0) {
      throw new BadRequestException("Missing removeOldRecipient information");
    }
    myOfferUcc.offerAgain(idOffer, oldRecipient, objectVersion);
  }


  /**
   * Mark the interest of the user for the object.
   *
   * @param idOffer the object the user is interested in
   * @param body    the body that contain the date and the phoneNumber
   * @param request the request to receive the user
   * @return true if the interest has been marked, false if the object already was marked
   */
  @POST
  @Path("{id}/interest")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public boolean markInterest(JsonNode body, @PathParam("id") int idOffer,
      @Context ContainerRequest request) {
    // Check date
    String date = body.get(Constants.OFFER_DATE).asText();
    int objectVersion = body.get(Constants.VERSION).asInt();
    if (date == null || date.equals("") || objectVersion == 0) {
      throw new BadRequestException("Missing informations.");
    }
    if (idOffer <= 0) {
      throw new NoResourceException("The offer does not exist.");
    }
    // retrieve the id of the requester
    int idUser = ((UserDto) request.getProperty(Constants.USER)).getIdUser();
    // Retriere the gsm number if not null
    String phoneNumber = null;
    if (body.hasNonNull(Constants.USER_PHONE_NUMBER) && !body.get(Constants.USER_PHONE_NUMBER)
        .asText().equals("")) {
      phoneNumber = body.get(Constants.USER_PHONE_NUMBER).asText();
    }
    return myOfferUcc.markInterest(idOffer, idUser, date, phoneNumber, objectVersion);
  }


  /**
   * Return an offer.
   *
   * @param idOffer the offer the user is interested in
   * @return the offer corresponding to the id
   */
  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public OfferDto seeDetails(@PathParam("id") int idOffer) {
    return myOfferUcc.getOne(idOffer);
  }

  /**
   * Set object state to "canceled".
   *
   * @param idOffer       the id of the offer
   * @param request       the request
   * @param objectVersion the version of the object
   * @return true if the field has been updated
   */
  @PATCH
  @Path("{id}/cancel")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public boolean removeOffer(@PathParam("id") int idOffer, @Context ContainerRequest request,
      @QueryParam(Constants.VERSION) int objectVersion) {
    if (objectVersion == 0) {
      throw new BadRequestException("Missing informations.");
    }

    int idUser = ((UserDto) request.getProperty(Constants.USER)).getIdUser();
    return myOfferUcc.cancelOffer(idOffer, idUser, objectVersion);
  }

  /**
   * Rate the offer (the object).
   *
   * @param idOffer the id of the offer
   * @param rating  the rating to add
   */
  @POST
  @Path("{id}/rate")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public void rateOffer(@PathParam("id") int idOffer, RatingDto rating) {
    // check credentials
    if (rating == null || rating.getDetail() == null || rating.getDetail().equals("")
        || rating.getValue() == 0) {
      throw new BadRequestException("Missing rateOffer information");
    }
    myOfferUcc.rateOffer(idOffer, rating);
  }
}
