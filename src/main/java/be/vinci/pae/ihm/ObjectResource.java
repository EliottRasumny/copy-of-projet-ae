package be.vinci.pae.ihm;


import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.object.ObjectUcc;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.ihm.filters.Authenticated;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.FatalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/objects")
public class ObjectResource {

  //private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private ObjectUcc myObjectUcc;


  /**
   * Looks for an object in the database with the given id.
   *
   * @param id the id of the object
   * @return the Object with the corresponding id
   */
  @POST
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public ObjectDto getObject(@PathParam("id") int id) {
    return myObjectUcc.getObject(id);

  }

  /**
   * modify the given object in the database.
   *
   * @param object          the object given by the user
   * @param fileDisposition the info of the file including the filename
   * @param file            the file given by the user
   * @return the Object created in the database
   */
  @PUT
  @Path("/modify")
  @Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated
  public OfferDto modifyObject(@FormDataParam(Constants.OBJECT) String object,
      @FormDataParam(Constants.ID_OFFER) int idOffer,
      @FormDataParam(Constants.FILE) InputStream file,
      @FormDataParam(Constants.FILE) FormDataContentDisposition fileDisposition) {
    // check credentials
    JsonMapper mapper = new JsonMapper();
    ObjectDto objectFront;
    try {
      objectFront = mapper.readValue(object, ObjectDto.class);
    } catch (JsonProcessingException e) {
      throw new FatalException("Error : the mapper did not succeed to transform json into object");
    }
    // check credentials
    if (objectFront == null
        || objectFront.getTimeSlot() == null
        || objectFront.getDescription() == null
        || objectFront.getVersion() == 0) {
      throw new BadRequestException("Missing modifyObject information");
    }
    String filename = null;
    if (fileDisposition != null) {
      filename = fileDisposition.getFileName();
    }
    return myObjectUcc.modifyObject(objectFront, idOffer, file, filename);
  }

  /**
   * Get the picture from oneDrive corresponding to the given name.
   *
   * @param pictureName the name of the picture we want
   * @return the outputstream of the picture
   */
  @GET
  @Path("/{pictureName}/picture")
  @Consumes(MediaType.APPLICATION_JSON)
  public File getPicture(@PathParam("pictureName") String pictureName,
      @Context ContainerRequest request) {

    return new File(
        Config.getProperty("PathToOneDrive")
            + pictureName);
  }

}
