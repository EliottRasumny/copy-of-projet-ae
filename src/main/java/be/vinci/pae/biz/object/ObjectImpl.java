package be.vinci.pae.biz.object;

import be.vinci.pae.biz.type.Type;
import be.vinci.pae.biz.type.TypeDto;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.biz.user.UserDto;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ObjectImpl implements Object {


  private static final String[] states =
      {"donated", "assignable", "assigned", "given", "cancelled"};

  private int idObject;
  private Type type;
  private String description;
  private String pictureName;
  private String timeSlot;
  private UserDto offeror;
  private UserDto recipient;
  private String state;
  private int version;

  public ObjectImpl() {
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getIdObject() {
    return idObject;
  }

  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  public TypeDto getType() {
    return type;
  }

  public void setType(TypeDto type) {
    this.type = (Type) type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPictureName() {
    return pictureName;
  }

  public void setPictureName(String pictureName) {
    this.pictureName = pictureName;
  }

  public String getTimeSlot() {
    return timeSlot;
  }

  public void setTimeSlot(String timeSlot) {
    this.timeSlot = timeSlot;
  }

  public UserDto getOfferor() {
    return offeror;
  }

  public void setOfferor(UserDto offeror) {
    this.offeror = offeror;
  }

  public UserDto getRecipient() {
    return recipient;
  }

  public void setRecipient(UserDto recipient) {
    this.recipient = recipient;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public boolean isStateOk(String state) {
    return Arrays.stream(states).collect(Collectors.toList()).contains(state);
  }

  public boolean isStateMarkable() {
    return state.equals("donated") || state.equals("assignable");
  }

  @Override
  public boolean checkAndModifyToAddARecipient(User recipient) {
    if (!state.equals("assignable")) {
      return false;
    }
    setState("assigned");
    setRecipient(recipient);
    return true;
  }


  @Override
  public String toString() {
    return "ObjectImpl{"
        + "id=" + idObject
        + ", type=" + type
        + ", description='" + description + '\''
        + ", picture=" + pictureName
        + ", timeSlot='" + timeSlot + '\''
        + ", offeror=" + offeror
        + ", recipient=" + recipient
        + ", state='" + state + '\''
        + '}';
  }

}
