package be.vinci.pae.biz.interest;

import static be.vinci.pae.utils.Constants.OBJECT_ASSIGNED;
import static be.vinci.pae.utils.Constants.OBJECT_GIVEN;

import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.offer.Offer;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.dal.DalServices;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.ForbiddenException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import jakarta.inject.Inject;
import java.util.List;

public class InterestUccImpl implements InterestUcc {

  @Inject
  private DalServices dalServices;
  @Inject
  private InterestDao interestDao;
  @Inject
  private ObjectDao objectDao;
  @Inject
  private OfferDao offerDao;
  @Inject
  private UserDao userDao;


  @Override
  public List<InterestDto> getInterests(int idOffer, int idMember) {
    // Make the request
    try {
      dalServices.startTransaction();
      if (idMember != offerDao.getOne(idOffer).getObject().getOfferor().getIdUser()) {
        dalServices.rollback();
        throw new ForbiddenException("The user is trying so see interests of an "
            + "offer he is not the offeror!");
      }
      List<InterestDto> interests = interestDao.getInterests(idOffer);
      dalServices.commit();
      return interests;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }

  @Override
  public void addRecipient(int idOffer, int idRecipient, int objectVersion) {
    try {
      dalServices.startTransaction();

      Offer offerDB = (Offer) offerDao.getOne(idOffer);
      if (offerDB == null) {
        dalServices.rollback();
        throw new NoResourceException("Error : The selected offer does not exist.");
      }
      User recipient = (User) userDao.getOne(idRecipient);
      if (recipient == null) {
        dalServices.rollback();
        throw new NoResourceException("Error : The selected recipient user does not exist.");
      }

      Object objectToModify = (Object) offerDB.getObject();
      if (!objectToModify.checkAndModifyToAddARecipient(recipient)) {
        dalServices.rollback();
        throw new PreconditionException(
            "Error : The user can not chose a recipient for this offer.");
      }
      objectToModify.setVersion(objectVersion);
      objectDao.update(objectToModify);
      interestDao.updateRecipientChosen(idOffer, idRecipient, true);

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
  public InterestDto getRecipient(int idOffer, int idMember) {
    // Make the request
    try {
      dalServices.startTransaction();
      if (idMember != offerDao.getOne(idOffer).getObject().getOfferor().getIdUser()) {
        dalServices.rollback();
        throw new ForbiddenException("The user is trying so see interests of an "
            + "offer he is not the offeror!");
      }
      InterestDto interest = interestDao.getRecipient(idOffer);
      dalServices.commit();
      return interest;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }

  @Override
  public void indicateRecipientHasCome(int idOffer, int idRecipient, int objectVersion) {
    try {
      dalServices.startTransaction();

      Offer offerDB = (Offer) offerDao.getOne(idOffer);
      User recipient = (User) userDao.getOne(idRecipient);

      if (offerDao.getOne(idOffer) == null) {
        dalServices.rollback();
        throw new NoResourceException("Error : The selected offer does not exist.");
      }
      if (recipient == null) {
        dalServices.rollback();
        throw new NoResourceException("Error : The selected recipient user does not exist.");
      }

      Object objectToModify = (Object) offerDB.getObject();
      if (!objectToModify.getState().equals(OBJECT_ASSIGNED)) {
        dalServices.rollback();
        throw new PreconditionException(
            "Error : There is no user assigned for this object");
      }

      objectToModify.setRecipient(recipient);
      objectToModify.setState(OBJECT_GIVEN);
      objectToModify.setVersion(objectVersion);
      objectDao.update(objectToModify);

      interestDao.updateRecipientHasCome(idOffer, idRecipient, true);

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
  public boolean isInterested(int idOffer, int idMember) {
    // Make the request
    try {
      dalServices.open();
      return interestDao.isInterested(idOffer, idMember);
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public List<InterestDto> getInterestedAndRecipeint(int idUser) {
    try {
      dalServices.startTransaction();
      List<InterestDto> list = interestDao.getInterestedAndRecipeint(idUser);
      // sead the interests to read
      interestDao.setInterestedToRead(idUser);
      dalServices.commit();
      return list;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }

}
