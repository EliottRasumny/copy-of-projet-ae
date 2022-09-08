package be.vinci.pae.biz.user;

import be.vinci.pae.biz.address.Address;
import be.vinci.pae.biz.address.AddressDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementation of User interface.
 */
public class UserImpl implements User {

  private int idUser;
  private String role;
  private String username;
  private String lastname;
  private String surname;
  private Address address;
  private String phoneNumber;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  private String refusalReason;
  private String state;
  private int version;

  public UserImpl() {
  }

  @Override
  public int getIdUser() {
    return idUser;
  }

  @Override
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getLastname() {
    return lastname;
  }

  @Override
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  @Override
  public String getSurname() {
    return surname;
  }

  @Override
  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public AddressDto getAddress() {
    return address;
  }

  @Override
  public void setAddress(AddressDto address) {
    this.address = (Address) address;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getRefusalReason() {
    return refusalReason;
  }

  @Override
  public void setRefusalReason(String refusalReason) {
    this.refusalReason = refusalReason;
  }

  @Override
  public String getState() {
    return state;
  }

  @Override
  public void setState(String state) {
    this.state = state;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public User register(User user) {
    hashPwd(user);
    //Set the state
    user.setState("registered");
    return user;
  }

  @Override
  public User hashPwd(User user) {
    String hashPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
    user.setPassword(hashPwd);
    return user;
  }

  @Override
  public boolean checkPassword(String pwNotHashed) {
    return BCrypt.checkpw(pwNotHashed, password);
  }

  @Override
  public boolean confirmInscription(String role) {
    if (state.equals(UserState.VALID.get())) {
      return false;
    }
    state = UserState.VALID.get();
    refusalReason = null;
    this.role = role;
    return true;
  }

  @Override
  public boolean refuseInscription(String refusalReason) {
    if (state.equals(UserState.DENIED.get()) || state.equals(UserState.VALID.get())) {
      return false;
    }
    state = UserState.DENIED.get();
    this.refusalReason = refusalReason;
    return true;
  }

  @Override
  public String toString() {
    return "UserImpl{"
        + "idUser=" + idUser
        + ", role='" + role + '\''
        + ", username='" + username + '\''
        + ", lastname='" + lastname + '\''
        + ", surname='" + surname + '\''
        + ", address=" + address
        + ", phoneNumber='" + phoneNumber + '\''
        + ", password='" + password + '\''
        + ", refusalReason='" + refusalReason + '\''
        + ", state='" + state + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserImpl)) {
      return false;
    }
    UserImpl user = (UserImpl) o;
    return idUser == user.idUser;
  }

  @Override
  public int hashCode() {
    return Objects.hash(idUser);
  }
}
