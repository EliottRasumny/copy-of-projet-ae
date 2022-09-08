package be.vinci.pae.biz;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.biz.address.Address;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.biz.user.User.UserState;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.dal.address.AddressDao;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;


public class UserImplTest {


  private static UserDao userDao;
  private static AddressDao addressDao;
  private static UserUcc userUcc;
  private static Factory factory;
  private Address address1;
  private Address address2;
  private User user;
  private User userNonValid;
  private User userValid;
  private User admin;
  private List<UserDto> requests;
  private List<UserDto> deniedRequests;

  @BeforeAll
  static void initAll() {
    ServiceLocator locator =
        ServiceLocatorUtilities.bind(new ApplicationBinder());
    userDao = locator.getService(UserDao.class);
    addressDao = locator.getService(AddressDao.class);
    userUcc = locator.getService(UserUcc.class);
    factory = locator.getService(Factory.class);
  }

  @BeforeEach
  void setUp() {
    address1 = (Address) factory.getAddress();
    address2 = (Address) factory.getAddress();
    user = (User) factory.getUser();
    userValid = (User) factory.getUser();
    userNonValid = (User) factory.getUser();
    admin = (User) factory.getUser();

    requests = new ArrayList<>();
    requests.add(user);

    deniedRequests = new ArrayList<>();
    requests.add(user);

    user.setIdUser(1);
    user.setAddress(address1);
    user.setRole("member");
    user.setUsername("Moi");
    user.setLastname("Toi");
    user.setSurname("alex");
    user.setPhoneNumber("0123456789");
    user.setPassword(BCrypt.hashpw("pwd", BCrypt.gensalt())); //pwd
    user.setState("valid");

    userValid.setState("valid");
    userValid.setRole("member");
    userNonValid.setState("denied");
    userNonValid.setRole("member");

    admin.setIdUser(2);
    admin.setAddress(address1);
    admin.setRole("admin");
    admin.setUsername("admin");
    admin.setLastname("admin");
    admin.setSurname("admin");
    admin.setPhoneNumber("0123456789");
    admin.setPassword(BCrypt.hashpw("pwd", BCrypt.gensalt())); //pwd
    admin.setState("valid");

    address1.setIdAddress(1);
    address1.setCommune("Vervier");
    address1.setUnitNumber("");

    address2.setIdAddress(1);
    address1.setCommune("Waterloo");

    Mockito.reset();
    Mockito.when(userDao.getOne(1)).thenReturn(user);
    Mockito.when(userDao.getOne(2)).thenReturn(admin);
    Mockito.when(userDao.getOne("Moi")).thenReturn(user);
    Mockito.when(userDao.getOne("admin")).thenReturn(admin);
    Mockito.when(userDao.getOne("PasMoi")).thenReturn(null);
    Mockito.when(userDao.getUsers(UserState.REGISTERED.get())).thenReturn(requests);
    Mockito.when(userDao.getUsers(UserState.DENIED.get())).thenReturn(deniedRequests);
    Mockito.when(userDao.modify(user)).thenReturn(user);
    Mockito.when(addressDao.getOne(1)).thenReturn(address1);
    Mockito.when(addressDao.modify(address1)).thenReturn(address2);
  }

  // TEST OF LOGIN

  /**
   * test of UserUcc with good username and bad password.
   */
  @DisplayName("good username bad password login test")
  @Test
  public void loginTest1() {
    assertThrows(NoResourceException.class, () -> userUcc.login("Moi", "false"));
  }

  /**
   * test of UserUcc with bad username and bad password.
   */
  @DisplayName("bad username bad password login test")
  @Test
  public void loginTest2() {
    assertThrows(NoResourceException.class, () -> userUcc.login("PasMoi", "false"));
  }

  /**
   * test of UserUcc with null username and bad password.
   */
  @DisplayName("null username bad password login test")
  @Test
  public void loginTest3() {
    assertThrows(NoResourceException.class, () -> userUcc.login(null, "false"));
  }

  /**
   * test of UserUcc with null username and good password.
   */
  @DisplayName("null username good password login test")
  @Test
  public void loginTest4() {
    assertThrows(NoResourceException.class, () -> userUcc.login(null, "pwd"));
  }

  /**
   * test of UserUcc with no username and bad password.
   */
  @DisplayName("no username bad password login test")
  @Test
  public void loginTest5() {
    assertThrows(NoResourceException.class, () -> userUcc.login("", "false"));
  }

  /**
   * test of UserUcc with no username and bad password.
   */
  @DisplayName("no username good password login test")
  @Test
  public void loginTest6() {
    assertThrows(NoResourceException.class, () -> userUcc.login("", "pwd"));
  }

  /**
   * test of UserUcc with good username and null password.
   */
  @DisplayName("good username null password login test")
  @Test
  public void loginTest7() {
    assertThrows(NoResourceException.class, () -> userUcc.login("Moi", null));
  }

  /**
   * test of UserUcc with good username and no password.
   */
  @DisplayName("good username no password login test")
  @Test
  public void loginTest8() {
    assertThrows(NoResourceException.class, () -> userUcc.login("Moi", ""));
  }

  /**
   * test of UserUcc with good username and good password but user is not valid.
   */
  @DisplayName("good username good password no valid login test")
  @Test
  public void loginTest9() {
    user.setState("registered");
    assertEquals(user, userUcc.login("Moi", "pwd"));
  }

  /**
   * test of UserUcc with good username and good password and valid inscription.
   */
  @DisplayName("good username good password login test")
  @Test
  public void loginTest10() {
    assertEquals(user, userUcc.login("Moi", "pwd"));
  }

  // TEST OF REGISTER

  /**
   * test of register with an already existing pseudo.
   */
  @DisplayName("register already existing pseudo")
  @Test
  public void registerTest1() {
    assertThrows(ConflictException.class, () -> userUcc.register(user));
  }

  // TEST OF CONFIRM FRIEND INSCRIPTION

  /**
   * test of confirmInscription with an already valid friend.
   */
  @DisplayName("confirm valid friend test")
  @Test
  public void confirmInscriptionTest1() {
    assertThrows(PreconditionException.class,
        () -> userUcc.confirmInscription(user.getIdUser(), false, 1));
  }

  /**
   * test of confirmFriendInscription with a friend.
   */
  @DisplayName("confirm an non future admin friend inscription test")
  @Test
  public void confirmInscriptionTest2() {
    user.setState("registered");
    assertAll(
        () -> assertNotNull(userUcc.confirmInscription(user.getIdUser(), false, 1)),
        () -> assertEquals("valid", user.getState()),
        () -> assertNull(user.getRefusalReason())
    );
  }

  /**
   * test of confirmInscription with a denied friend.
   */
  @DisplayName("confirm an non future admin denied friend inscription test")
  @Test
  public void confirmInscriptionTest3() {
    user.setState("denied");
    user.setRefusalReason("nonon");
    assertAll(
        () -> assertNotNull(userUcc.confirmInscription(user.getIdUser(), false, 1)),
        () -> assertEquals("valid", user.getState()),
        () -> assertNull(user.getRefusalReason())
    );
  }

  // TEST OF REFUSE FRIEND INSCRIPTION

  /**
   * test of refuseInscription with an valid friend.
   */
  @DisplayName("refuse friend inscription with a valid friend test")
  @Test
  public void refuseInscriptionTest1() {
    assertThrows(PreconditionException.class,
        () -> userUcc.confirmInscription(user.getIdUser(), false, 1));
  }

  /**
   * test of refuseInscription with an already denied user.
   */
  @DisplayName("refuse friend inscription with an already denied user")
  @Test
  public void refuseInscriptionTest2() {
    user.setState("denied");
    user.setRefusalReason("nonon");
    assertThrows(PreconditionException.class,
        () -> userUcc.refuseInscription(user.getIdUser(), "non", 1));
  }

  /**
   * test of refuseInscription with a friend.
   */
  @DisplayName("refuse friend inscription with an already denied user")
  @Test
  public void refuseInscriptionTest3() {
    user.setState("registered");
    assertAll(
        () -> assertNotNull(userUcc.refuseInscription(user.getIdUser(), "non", 1)),
        () -> assertEquals("denied", user.getState()),
        () -> assertEquals("non", user.getRefusalReason())
    );
  }

  // TEST OF GET USER

  /**
   * test of getUser.
   */
  @DisplayName("get user")
  @Test
  public void getUserTest1() {
    assertEquals(user, userUcc.getUser(1));
  }

  // TEST OF GET INSCRIPTION REQUESTS

  /**
   * test of getInscriptionRequests.
   */
  @DisplayName("get inscription requests")
  @Test
  public void getInscriptionRequestsTest1() {
    assertEquals(requests, userUcc.getInscriptionRequests());
  }

  // TEST OF GET DENIED INSCRIPTION

  /**
   * test of getDeniedInscriptionRequest.
   */
  @DisplayName("get denied inscription requests")
  @Test
  public void getDeniedInscriptionRequestsTest1() {
    assertEquals(deniedRequests, userUcc.getDeniedInscriptionRequests());
  }

  // TEST OF MODIFY

  /**
   * test of modify with password.
   */
  @DisplayName("modify with password")
  @Test
  public void modifyTest1() {
    UserDto userPwd = factory.getUser();
    userPwd.setIdUser(1);
    userPwd.setAddress(address1);
    userPwd.setRole("member");
    userPwd.setUsername("Moi");
    userPwd.setLastname("Toi");
    userPwd.setSurname("alex");
    userPwd.setPhoneNumber("0123456789");
    userPwd.setPassword("pwd"); //pwd
    userPwd.setState("valid");

    address1.setCommune("Waterloo");

    assertAll(
        () -> assertEquals(user, userUcc.modify(userPwd)),
        () -> assertEquals(user.getPassword(), userUcc.modify(userPwd).getPassword()),
        () -> assertNull(userPwd.getAddress().getUnitNumber()),
        () -> assertEquals(user.getLastname(), userUcc.modify(userPwd).getLastname()),
        () -> assertEquals(user.getUsername(), userUcc.modify(userPwd).getUsername()),
        () -> assertEquals(user.getSurname(), userUcc.modify(userPwd).getSurname()),
        () -> assertEquals(user.getPhoneNumber(), userUcc.modify(userPwd).getPhoneNumber()),
        () -> assertEquals(user.getState(), userUcc.modify(userPwd).getState())
    );

  }


}
