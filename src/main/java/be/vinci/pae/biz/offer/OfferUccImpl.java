package be.vinci.pae.biz.offer;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.interest.Interest;
import be.vinci.pae.biz.object.ObjectDto;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.dal.DalServices;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.rating.RatingDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Singleton
public class OfferUccImpl implements OfferUcc {

  @Inject
  private DalServices dalServices;
  @Inject
  private Factory factory;
  @Inject
  private OfferDao offerDao;
  @Inject
  private InterestDao interestDao;
  @Inject
  private UserDao userDao;
  @Inject
  private ObjectDao objectDao;
  @Inject
  private RatingDao ratingDao;

  @Override
  public List<OfferDto> getAll(String sortedBy, UserDto userRequester, String filterType,
      String filterValue) {
    try {
      dalServices.open();
      List<OfferDto> offers;
      // If the user is authenticated, we make a personalized request. Otherwise, it is by default.
      if (userRequester == null) {
        offers = offerDao.getAllDisconnected();
      } else {
        if (filterType != null && checkFilter(filterType)) {
          offers = offerDao.getAll(checkSortChoice(sortedBy), userRequester, filterType,
              filterValue);
        } else {
          offers = offerDao.getAll(checkSortChoice(sortedBy), userRequester, null, null);
        }
      }
      return offers;
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  private boolean checkFilter(String type) {
    switch (type) {
      case Constants.OBJECT_FILTER_TYPE_MINE:
      case Constants.OBJECT_FILTER_TYPE_BY_NAME:
      case Constants.OBJECT_FILTER_TYPE_BY_TYPE:
      case Constants.OBJECT_FILTER_TYPE_BY_STATE:
      case Constants.OBJECT_FILTER_TYPE_BY_DATE:
      case Constants.OBJECT_FILTER_VALUE_GIVEN_ASSIGNED:
      case Constants.OBJECT_FILTER_VALUE_RECEIVED:
        return true;
      default:
        throw new PreconditionException("Filter is unknown");
    }
  }

  @Override
  public List<OfferDto> getAllAdmin(int idUser) {
    if (idUser == 0) {
      throw new NoResourceException("There is no user of this id");
    }
    try {
      dalServices.open();
      return offerDao.getAllAdmin(idUser);
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }


  @Override
  public OfferDto createOffer(ObjectDto objectFront, int idUser, InputStream file,
      String filename) {
    try {
      objectFront.setState(Constants.OBJECT_DONATED);
      String uuid = null;
      if (file != null || filename != null) {
        String[] split = filename.split("\\.");
        String extension = split[split.length - 1];
        uuid = UUID.randomUUID().toString();
        uuid += "." + extension;
      }
      objectFront.setPictureName(uuid);
      dalServices.startTransaction();
      //create object
      ObjectDto object = objectDao.addOne(objectFront, idUser);
      if (object == null) {
        throw new FatalException("Error : The object hasn't been added into the db");
      }
      if (file != null) {
        try {
          Files.copy(file, Paths.get(
              Config.getProperty("PathToOneDrive")
                  + uuid));
        } catch (IOException e) {
          throw new FatalException(
              "Error : The picture hasn't been uploaded correctly on oneDrive");
        }
      }
      //create offer
      OfferDto offer = offerDao.addOne(object);
      if (offer == null) {
        throw new FatalException("Error : The offer hasn't been added into the db");
      }
      dalServices.commit();
      return offer;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }

  @Override
  public boolean markInterest(int idOffer, int idUser, String date, String phoneNumber,
      int objectVersion) {
    try {
      dalServices.startTransaction();
      Offer offerDB = (Offer) offerDao.getOne(idOffer);
      if (offerDB == null) {
        dalServices.rollback();
        throw new NoResourceException("Error : The selected offer does not exist.");
      }
      // Check if the object is still available for an exchange.
      if (!offerDB.canBeMarkedAsInterest(idUser)
          || offerDao.isAlreadyInterested(idOffer, idUser)) {
        dalServices.rollback();
        throw new PreconditionException("Error : You can not mark your interest for this offer.");
      }
      //Update the state of the object to "assignable"
      if (!offerDB.getObject().getState().equals(Constants.OBJECT_ASSIGNABLE)) {
        objectDao.updateState(offerDB.getObject().getIdObject(), Constants.OBJECT_ASSIGNABLE,
            objectVersion);
      }
      if (phoneNumber != null) {
        userDao.updatePhoneNumberOnInterest(idUser, phoneNumber);
      }
      Interest interest = (Interest) factory.getInterest();
      interest.prepareToAdd(offerDB, idUser, date, phoneNumber);
      interestDao.addOne(interest);

      dalServices.commit();
      return true;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public boolean isInterested(int idOffer, int idUser) {
    try {
      dalServices.open();
      return offerDao.isAlreadyInterested(idOffer, idUser);
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public OfferDto getOne(int idOffer) {
    try {
      dalServices.open();
      OfferDto offerDB = offerDao.getOne(idOffer);
      if (offerDB == null) {
        throw new NoResourceException("The offer doesn't exist");
      }
      return offerDB;
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public boolean cancelOffer(int idOffer, int idUser, int objectVersion) {
    try {
      dalServices.startTransaction();
      // Set object state to cancel
      OfferDto offer = offerDao.getOne(idOffer);
      ObjectDto object = offer.getObject();
      if (idUser != object.getOfferor().getIdUser()) {
        dalServices.rollback();
        throw new PreconditionException("Error : You can not remove an offer that's not yours");
      }
      offerDao.updateOfferState(idOffer, Constants.OFFER_STATE_OUTDATED);
      objectDao.updateState(object.getIdObject(), Constants.OBJECT_CANCELLED, objectVersion);
      objectDao.removeRecipient(object.getIdObject(), objectVersion + 1);
      dalServices.commit();
      return true;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public void removeOldRecipient(int idOffer, int oldRecipient, int objectVersion) {
    try {
      dalServices.startTransaction();
      // Remove current recipient and set object' state to 'assignable'
      OfferDto offer = offerDao.getOne(idOffer);
      ObjectDto object = offer.getObject();
      objectDao.removeRecipient(object.getIdObject(), objectVersion);
      objectDao.updateState(object.getIdObject(), Constants.OBJECT_ASSIGNABLE, objectVersion + 1);
      // Set interest has_come to false
      interestDao.updateRecipientHasCome(idOffer, oldRecipient, false);
      dalServices.commit();
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }


  @Override
  public void offerAgain(int idOffer, int oldRecipient, int objectVersion) {
    try {
      dalServices.startTransaction();
      // outdate the current offer
      offerDao.updateOfferState(idOffer, Constants.OFFER_STATE_OUTDATED);
      // create new offer
      OfferDto offer = offerDao.getOne(idOffer);
      offerDao.addOne(offer.getObject());
      // set object to donated
      objectDao.updateState(offer.getObject().getIdObject(), Constants.OBJECT_DONATED,
          objectVersion);
      dalServices.commit();
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public void rateOffer(int idOffer, RatingDto rating) {
    try {
      dalServices.startTransaction();
      OfferDto offer = offerDao.getOne(idOffer);
      if (offer == null) {
        dalServices.rollback();
        throw new NoResourceException("The offer doesn't exist");
      }
      ObjectDto object = offer.getObject();
      rating.setObject(object);
      ratingDao.addOne(rating);
      dalServices.commit();
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }


  /**
   * Check if the asked sort is valid by comparing it to all existing choices.
   *
   * @param sortedBy the sort method.
   * @return the correct string corresponding to the sort.
   */
  private String checkSortChoice(String sortedBy) {
    String sort;
    switch (sortedBy) {
      case "type_asc":
        sort = Sort.TYPE_ASC.get();
        break;
      case "type_desc":
        sort = Sort.TYPE_DESC.get();
        break;
      case "interests_asc":
        sort = Sort.NBR_INTERESTS_ASC.get();
        break;
      case "interests_desc":
        sort = Sort.NBR_INTERESTS_DESC.get();
        break;
      case "date_asc":
        sort = Sort.DATE_ASC.get();
        break;
      case "date_desc":
        sort = Sort.DATE_DESC.get();
        break;
      default:
        throw new PreconditionException("Error : this sort option does not exist.");
    }
    return sort;
  }

  private enum Sort {
    TYPE_ASC("ob.type"), TYPE_DESC("ob.type DESC"), NBR_INTERESTS_ASC(
        "NBR_INTERESTS"), NBR_INTERESTS_DESC("NBR_INTERESTS DESC"), DATE_ASC(
        "of.offer_date"), DATE_DESC("of.offer_date DESC") // BY DEFAULT
    ;
    private final String sort;

    Sort(String sort) {
      this.sort = sort;
    }

    public String get() {
      return sort;
    }
  }
}
