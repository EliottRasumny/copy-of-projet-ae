package be.vinci.pae.biz.object;

import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.dal.DalServices;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Singleton
public class ObjectUccImpl implements ObjectUcc {

  @Inject
  private ObjectDao objectDao;
  @Inject
  private DalServices dalServices;
  @Inject
  private OfferDao offerDao;


  @Override
  public ObjectDto getObject(int id) {
    try {
      dalServices.open();
      ObjectDto objectDb = objectDao.getOne(id);
      if (objectDb == null) {
        throw new NoResourceException("Error : This object does not exist.");
      }
      return objectDb;
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public OfferDto modifyObject(ObjectDto objectFront, int idOffer, InputStream file,
      String filename) {
    try {
      String uuid = null;
      if (file != null) {
        String[] split = filename.split("\\.");
        String extension = split[split.length - 1];
        uuid = UUID.randomUUID().toString();
        uuid += "." + extension;
      }

      dalServices.startTransaction();
      Object object = (Object) objectFront;
      // modification were made
      ObjectDto oldObject = offerDao.getOne(idOffer).getObject();
      oldObject.setDescription(object.getDescription());
      oldObject.setTimeSlot(object.getTimeSlot());
      if (!oldObject.equals(object)) {
        // modify the object in db
        if (file != null) {
          try {
            Files.delete(
                Paths.get(Config.getProperty("PathToOneDrive") + oldObject.getPictureName()));
            Files.copy(file, Paths.get(
                Config.getProperty("PathToOneDrive")
                    + uuid));
            oldObject.setPictureName(uuid);
          } catch (IOException e) {
            throw new FatalException(
                "Error : The picture hasn't been uploaded correctly on oneDrive");
          }
        }
        oldObject.setVersion(objectFront.getVersion());
        objectDao.update(oldObject);

      }
      OfferDto offer = offerDao.getOne(objectFront);
      dalServices.commit();
      return offer;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }
}
