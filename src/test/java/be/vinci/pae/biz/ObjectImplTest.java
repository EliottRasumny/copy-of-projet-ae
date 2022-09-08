package be.vinci.pae.biz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.object.ObjectUcc;
import be.vinci.pae.biz.offer.Offer;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.offer.OfferUcc;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.NoResourceException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ObjectImplTest {

  private static OfferDao offerDao;
  private static ObjectUcc objectUcc;

  private static ObjectDao objectDao;
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
    objectDao = locator.getService(ObjectDao.class);
    objectUcc = locator.getService(ObjectUcc.class);
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

    Mockito.when(offerDao.getOne(object)).thenReturn(offer1);
    Mockito.when(objectDao.getOne(0)).thenReturn(null);
    Mockito.when(objectDao.getOne(1)).thenReturn(object);


  }

  // GET OBJECT TEST

  /**
   * test of getObject with a non-existing object.
   */
  @DisplayName("getObject with a non-existing object")
  @Test
  public void getObject1() {
    assertThrows(NoResourceException.class, () -> objectUcc.getObject(0));
  }

  /**
   * test of getObject with an existing object.
   */
  @DisplayName("getObject with an existing object")
  @Test
  public void getObject2() {
    assertEquals(object, objectUcc.getObject(1));
  }

  // MODIFY OBJECT

  /**
   * test of modifyObject with an existing object.
   */
  @DisplayName("getObject with an existing object")
  @Test
  public void modifyObject1() {
    assertEquals(offer1, objectUcc.modifyObject(object, offer1.getIdOffer(), null, ""));
  }


}
