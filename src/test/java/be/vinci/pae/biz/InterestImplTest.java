package be.vinci.pae.biz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.biz.interest.InterestDto;
import be.vinci.pae.biz.interest.InterestUcc;
import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.offer.Offer;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.ForbiddenException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InterestImplTest {

  private static OfferDao offerDao;

  private static UserDao userDao;
  private static Factory factory;

  private static InterestDao insterestDao;
  private static InterestUcc interestUcc;

  private static InterestDto interests1;
  private static List<InterestDto> interests = new ArrayList<>();

  private static Offer offer1;
  private static Offer offer2;
  private static Object object;
  private static User user;
  private static User recipient;

  @BeforeAll
  static void initAll() {
    ServiceLocator locator =
        ServiceLocatorUtilities.bind(new ApplicationBinder());
    offerDao = locator.getService(OfferDao.class);
    userDao = locator.getService(UserDao.class);
    insterestDao = locator.getService(InterestDao.class);
    interestUcc = locator.getService(InterestUcc.class);
    factory = locator.getService(Factory.class);

    offer1 = (Offer) factory.getOffer();
    offer1.setIdOffer(1);

    offer2 = (Offer) factory.getOffer();
    offer2.setIdOffer(2);

    user = (User) factory.getUser();
    user.setIdUser(1);

    recipient = (User) factory.getUser();
    recipient.setIdUser(2);

    object = (Object) factory.getObject();
    object.setIdObject(1);
    object.setOfferor(user);
    offer1.setObject(object);

    interests1 = factory.getInterest();
    interests1.setIdInterest(1);
    interests1.setOffer(offer1);
    interests1.setInterestedMember(recipient);

    interests.add(interests1);

    Mockito.when(offerDao.getOne(1)).thenReturn(offer1);
    Mockito.when(offerDao.getOne(0)).thenReturn(null);
    Mockito.when(userDao.getOne(0)).thenReturn(null);
    Mockito.when(userDao.getOne(1)).thenReturn(user);
    Mockito.when(userDao.getOne(2)).thenReturn(recipient);
    Mockito.when(insterestDao.getInterests(1)).thenReturn(interests);
    Mockito.when(insterestDao.getRecipient(1)).thenReturn(interests1);

  }

  // GET INTERESTS TEST

  /**
   * test of getInterests when the user is not the offeror.
   */
  @DisplayName("getInterests when the user is not the offeror")
  @Test
  public void getInterests1() {
    assertThrows(ForbiddenException.class, () -> interestUcc.getInterests(1, 2));
  }

  /**
   * test of getInterests when the user is the offeror.
   */
  @DisplayName("getInterests when the user is the offeror")
  @Test
  public void getInterests2() {
    assertEquals(interests, interestUcc.getInterests(1, 1));
  }

  // ADD RECIPIENT TEST

  /**
   * test of addRecipient when offer doesn't exist.
   */
  @DisplayName("addRecipient when offer doesn't exist")
  @Test
  public void addRecipient1() {
    assertThrows(NoResourceException.class, () -> interestUcc.addRecipient(0, 2, 1));
  }

  /**
   * test of addRecipient when the object is donated.
   */
  @DisplayName("addRecipient when the object is donated")
  @Test
  public void addRecipient2() {
    object.setState("donated");
    assertThrows(PreconditionException.class, () -> interestUcc.addRecipient(1, 1, 1));
  }

  // GET RECIPIENT TEST

  /**
   * test of getRecipient when the user is not the offeror.
   */
  @DisplayName("getRecipient when the user is not the offeror")
  @Test
  public void getRecipient1() {
    assertThrows(ForbiddenException.class, () -> interestUcc.getRecipient(1, 2));
  }

  /**
   * test of getRecipient when everything is ok.
   */
  @DisplayName("getRecipient when everything is ok")
  @Test
  public void getRecipient2() {
    assertEquals(interests1, interestUcc.getRecipient(1, 1));
  }

  // INDICATE RECIPIENT HAS COME

  /**
   * test of indicateRecipientHasCome when the offer doesn't exist.
   */
  @DisplayName("indicateRecipientHasCome when the offer doesn't exist")
  @Test
  public void indicateRecipient1() {
    assertThrows(NoResourceException.class, () -> interestUcc.indicateRecipientHasCome(0, 2, 1));
  }

  /**
   * test of indicateRecipientHasCome when the user doesn't exist.
   */
  @DisplayName("indicateRecipientHasCome when the user doesn't exist")
  @Test
  public void indicateRecipient2() {
    assertThrows(NoResourceException.class, () -> interestUcc.indicateRecipientHasCome(1, 0, 1));
  }

  /**
   * test of indicateRecipientHasCome when the user doesn't exist.
   */
  @DisplayName("indicateRecipientHasCome when the user doesn't exist")
  @Test
  public void indicateRecipient3() {
    object.setState("donated");
    assertThrows(PreconditionException.class, () -> interestUcc.indicateRecipientHasCome(1, 2, 1));
  }

}
