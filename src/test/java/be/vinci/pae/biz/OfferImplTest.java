package be.vinci.pae.biz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.offer.Offer;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.offer.OfferUcc;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.rating.RatingDao;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.FatalException;
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

public class OfferImplTest {

  private static OfferDao offerDao;
  private static OfferUcc offerUcc;
  private static ObjectDao objectDao;
  private static InterestDao insterestDao;
  private static RatingDao ratingDao;
  private static Factory factory;

  private static List<OfferDto> offers;
  private static List<OfferDto> offersUser;

  private static Offer offer1;
  private static Offer offer2;
  private static Object object;
  private static User user;

  @BeforeAll
  static void initAll() {
    ServiceLocator locator =
        ServiceLocatorUtilities.bind(new ApplicationBinder());
    offerDao = locator.getService(OfferDao.class);
    offerUcc = locator.getService(OfferUcc.class);
    objectDao = locator.getService(ObjectDao.class);
    insterestDao = locator.getService(InterestDao.class);
    ratingDao = locator.getService(RatingDao.class);
    factory = locator.getService(Factory.class);

    offer1 = (Offer) factory.getOffer();
    offer1.setIdOffer(1);

    offer2 = (Offer) factory.getOffer();
    offer2.setIdOffer(2);

    user = (User) factory.getUser();
    user.setIdUser(1);

    object = (Object) factory.getObject();
    object.setIdObject(1);
    object.setOfferor(user);
    offer1.setObject(object);

    offers = new ArrayList<>();
    offers.add(offer1);
    offers.add(offer2);

    offersUser = new ArrayList<>();
    offersUser.add(offer1);

    Mockito.when(offerDao.getAll("ob.type", user, "mine", null)).thenReturn(offersUser);
    Mockito.when(offerDao.getAllDisconnected()).thenReturn(offers);
    Mockito.when(offerDao.getAll("ob.type", user, null, null)).thenReturn(offers);

    Mockito.when(objectDao.addOne(factory.getObject(), 0)).thenReturn(null);
    Mockito.when(objectDao.addOne(object, 1)).thenReturn(object);
    Mockito.when(offerDao.addOne(factory.getObject())).thenReturn(null);
    Mockito.when(offerDao.addOne(object)).thenReturn(offer1);

    Mockito.when(offerDao.getOne(1)).thenReturn(offer1);
    Mockito.when(offerDao.getOne(0)).thenReturn(null);
  }

  // TEST OF GETALL

  /**
   * test of getall with a user null.
   */
  @DisplayName("get all with a user null")
  @Test
  public void getall1() {
    assertEquals(offers, offerUcc.getAll("type_asc", null, null, null));
  }

  /**
   * test of getall with user and filter.
   */
  @DisplayName("get all with user and filter")
  @Test
  public void getall2() {
    assertEquals(offersUser, offerUcc.getAll("type_asc", user, "mine", null));
  }

  /**
   * test of getall with user and no filter.
   */
  @DisplayName("get all with user and no filter")
  @Test
  public void getall3() {
    assertEquals(offers, offerUcc.getAll("type_asc", user, null, null));
  }

  // TEST OF MARK INTERESTS

  /**
   * test of markInterest when the offer doesn't exist.
   */
  @DisplayName("mark interest when the offer doesn't exist")
  @Test
  public void markInterest1() {
    assertThrows(NoResourceException.class, () -> offerUcc.markInterest(0, 0,
        "", "", 1));
  }

  /**
   * test of markInterest when the offer doesn't exist.
   */
  @DisplayName("mark interest when the offer doesn't exist")
  @Test
  public void markInterest2() {
    object.setRecipient(factory.getUser());
    assertThrows(PreconditionException.class, () -> offerUcc.markInterest(1, 0,
        "", "", 1));
  }

  // TEST OF CREATE OFFER

  /**
   * test of createOffer when the object hasn't been added in the db.
   */
  @DisplayName("get all when the object hasn't been added in the db")
  @Test
  public void createOffer1() {
    assertThrows(FatalException.class, () -> offerUcc.createOffer(factory.getObject(), 0,
        null, ""));
  }

  /**
   * test of createOffer when the offer hasn't been added in the db.
   */
  @DisplayName("get all when when the offer hasn't been added in the db")
  @Test
  public void createOffer2() {
    assertThrows(FatalException.class, () -> offerUcc.createOffer(factory.getObject(), 0,
        null, ""));
  }

  /**
   * test of createOffer when everything is ok.
   */
  @DisplayName("get all when when everything is ok")
  @Test
  public void createOffer3() {
    assertEquals(offer1, offerUcc.createOffer(object, 1, null, ""));
  }

  // TEST OF REMOVE OFFER

  /**
   * test of removeOffer when the idUser isn't thee offeror of the object.
   */
  @DisplayName("get all when the idUser isn't thee offeror of the object")
  @Test
  public void removeOffer1() {
    assertThrows(PreconditionException.class,
        () -> offerUcc.cancelOffer(1, 2, 1));
  }

  /**
   * test of removeOffer when everything is ok.
   */
  @DisplayName("get all when everything is ok")
  @Test
  public void removeOffer2() {
    assertTrue(offerUcc.cancelOffer(1, 1, 1));
  }

  // TEST OF REMOVE OLD RECIPIENT

  // TEST OF OFFER AGAIN


}
