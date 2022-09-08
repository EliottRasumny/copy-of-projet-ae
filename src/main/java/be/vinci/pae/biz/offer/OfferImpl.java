package be.vinci.pae.biz.offer;


import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.object.ObjectDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

public class OfferImpl implements Offer {

  private int idOffer;
  private String usernameOfferor;
  private Object object;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "E dd MMM yyyy à HH:mm",
      timezone = "GMT+2")
  private Timestamp date;
  private String state;
  private int nbrInterests;
  private boolean myInterest;

  public OfferImpl() {
  }

  @Override
  public int getIdOffer() {
    return idOffer;
  }

  @Override
  public void setIdOffer(int idOffer) {
    this.idOffer = idOffer;
  }

  @Override
  public String getUsernameOfferor() {
    return usernameOfferor;
  }

  @Override
  public void setUsernameOfferor(String usernameOfferor) {
    this.usernameOfferor = usernameOfferor;
  }

  @Override
  public ObjectDto getObject() {
    return object;
  }

  @Override
  public void setObject(ObjectDto object) {
    this.object = (Object) object;
  }

  @Override
  public Timestamp getDate() {
    return date;
  }

  @Override
  public void setDate(Timestamp date) {
    this.date = date;
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
  public int getNbrInterests() {
    return nbrInterests;
  }

  @Override
  public void setNbrInterests(int nbrInterests) {
    this.nbrInterests = nbrInterests;
  }

  @Override
  public boolean isMyInterest() {
    return myInterest;
  }

  @Override
  public void setMyInterest(boolean myInterest) {
    this.myInterest = myInterest;
  }

  @Override
  public boolean canBeMarkedAsInterest(int idUser) {
    return idUser != 0 && object.getOfferor().getIdUser() != idUser
        && object.isStateMarkable();
  }

  @Override
  public String toString() {
    return "OfferImpl{"
        + "idOffer=" + idOffer
        + ", object=" + object
        + ", date=" + date
        + ", state=" + state
        + ", nbrInterests=" + nbrInterests
        + ", myInterest=" + myInterest
        + '}';
  }
}
