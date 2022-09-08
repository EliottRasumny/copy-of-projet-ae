package be.vinci.pae.biz.user;

import be.vinci.pae.dal.DalServices;
import be.vinci.pae.dal.address.AddressDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.Constants;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class UserUccImpl implements UserUcc {

  @Inject
  private UserDao userDao;
  @Inject
  private AddressDao addressDao;
  @Inject
  private DalServices dalServices;
  @Inject
  private OfferDao offerDao;

  @Override
  public UserDto register(UserDto userDto) {
    try {
      dalServices.startTransaction();
      //Verif user unique
      if (userDao.getOne(userDto.getUsername()) != null) {
        dalServices.rollback();
        throw new ConflictException("Error : The username already exist");
      }
      User user = (User) userDto;
      user.register(user);
      //insert address in db
      user.setAddress(addressDao.addOne(user.getAddress()));
      // insert the user in db
      UserDto userDB = userDao.addOne(user);
      dalServices.commit();
      return userDB;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    }
  }

  @Override
  public UserDto login(String username, String password) {
    try {
      dalServices.startTransaction();
      User userDB = (User) userDao.getOne(username);
      // Check if user exist
      if (userDB == null || !userDB.checkPassword(password)) {
        dalServices.rollback();
        throw new NoResourceException("Error : This user/password combination does not exist.");
      }
      if (userDB.getState().equals(Constants.USER_STATE_UNAVAILABLE)) {
        userDB.setState(Constants.USER_STATE_VALID);
        userDao.update(userDB);
        offerDao.updateOffersAvailability(userDB.getIdUser(), Constants.OFFER_STATE_AVAILABLE);
      }
      dalServices.commit();
      return userDB;

    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public UserDto getUser(int id) {
    try {
      dalServices.open();
      return userDao.getOne(id);
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public UserDto confirmInscription(int id, Boolean futurAdmin, int version) {
    String role;
    if (futurAdmin) {
      role = "admin";
    } else {
      role = "member";
    }
    try {
      dalServices.startTransaction();
      User userDB = (User) userDao.getOne(id);
      if (userDB == null) {
        dalServices.rollback();
        throw new NoResourceException("The user to confirm was not found");
      }
      if (!userDB.confirmInscription(role)) {
        dalServices.rollback();
        throw new PreconditionException("The user is already validated");
      }
      userDB.setVersion(version);
      userDao.update(userDB);
      dalServices.commit();
      return userDB;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public UserDto refuseInscription(int id, String refusalReason, int version) {
    try {
      dalServices.startTransaction();
      User userDB = (User) userDao.getOne(id);
      if (userDB == null) {
        dalServices.rollback();
        throw new NoResourceException("The user to refuse was not found");
      }
      if (!userDB.refuseInscription(refusalReason)) {
        dalServices.rollback();
        throw new PreconditionException("The user can not be refused");
      }
      userDB.setVersion(version);
      userDao.update(userDB);
      dalServices.commit();
      return userDB;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public List<UserDto> getInscriptionRequests() {
    try {
      dalServices.startTransaction();
      return userDao.getUsers(User.UserState.REGISTERED.get());
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public List<UserDto> getDeniedInscriptionRequests() {
    try {
      dalServices.open();
      return userDao.getUsers(User.UserState.DENIED.get());
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }

  @Override
  public UserDto modify(UserDto userDto) {
    try {
      dalServices.startTransaction();
      User user = (User) userDto;
      // verify if there's a pwd, if yes, hash it
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        user.hashPwd(user);
      }
      // Set the unitNumber to null if there was no modification
      if (user.getAddress().getUnitNumber() != null && user.getAddress().getUnitNumber()
          .isEmpty()) {
        user.getAddress().setUnitNumber(null);
      }
      if (!addressDao.getOne(user.getAddress().getIdAddress()).equals(user.getAddress())) {
        user.setAddress(addressDao.modify(user.getAddress()));
      }
      // modify the user in db
      UserDto userDB = userDao.modify(user);
      dalServices.commit();
      return userDB;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }


  @Override
  public List<UserDto> getAllUsers(String value, String type) {
    checkFilterType(type);
    try {
      dalServices.open();
      return userDao.getAllUsers(value, type);
    } catch (FatalException fatalException) {
      throw new FatalException(fatalException.getMessage(), fatalException);
    } finally {
      dalServices.close();
    }
  }


  @Override
  public UserDto promoteUser(int id, int version) {
    try {
      dalServices.startTransaction();
      User userDB = (User) userDao.getOne(id);
      if (userDB == null) {
        dalServices.rollback();
        throw new NoResourceException("The user to promote was not found");
      }
      userDB.setRole("admin");
      userDB.setVersion(version);
      userDao.update(userDB);
      dalServices.commit();
      return userDB;
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw new FatalException(fatalException.getMessage(), fatalException);
    } catch (ConflictException conflictException) {
      dalServices.rollback();
      throw new ConflictException(conflictException.getMessage(), conflictException);
    }
  }

  @Override
  public boolean setUserStateToUnavailable(int idUser, int version) {
    try {
      dalServices.startTransaction();
      // Set the user state to 'unavailable'
      UserDto userDB = userDao.getOne(idUser);
      userDB.setState(Constants.USER_STATE_UNAVAILABLE);
      userDB.setVersion(version);
      userDao.update(userDB);
      // Set all the offer's state of the user to 'unavailable'
      offerDao.updateOffersAvailability(userDB.getIdUser(), Constants.OFFER_STATE_UNAVAILABLE);
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

  /**
   * Checks if the filter is one of the authorized and give the column of that type.
   *
   * @param type the type of the research
   */
  private void checkFilterType(String type) {
    switch (type) {
      case Constants.MEMBER_FILTER_TYPE_BY_LASTNAME:
      case Constants.MEMBER_FILTER_TYPE_BY_POSTCODE:
      case Constants.MEMBER_FILTER_TYPE_BY_COMMUNE:
        break;
      default:
        throw new FatalException("There is no such filter");
    }
  }

}
