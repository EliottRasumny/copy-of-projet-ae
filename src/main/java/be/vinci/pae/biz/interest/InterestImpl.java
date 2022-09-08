package be.vinci.pae.biz.interest;

import be.vinci.pae.biz.offer.Offer;
import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.user.User;
import be.vinci.pae.biz.user.UserDto;
import be.vinci.pae.biz.user.UserImpl;

public class InterestImpl implements Interest {

  private int idInterest;
  private Offer offer;
  private User interestedMember;
  private String date;
  private boolean answerCall;
  private boolean recipentChosen;
  private boolean hasCome;


  public InterestImpl() {
  }

  @Override
  public int getIdInterest() {
    return idInterest;
  }

  @Override
  public void setIdInterest(int idInterest) {
    this.idInterest = idInterest;
  }

  @Override
  public OfferDto getOffer() {
    return offer;
  }

  @Override
  public void setOffer(OfferDto offer) {
    this.offer = (Offer) offer;
  }

  @Override
  public UserDto getInterestedMember() {
    return interestedMember;
  }

  @Override
  public void setInterestedMember(UserDto interestedMember) {
    this.interestedMember = (User) interestedMember;
  }

  @Override
  public String getDate() {
    return date;
  }

  @Override
  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public boolean isAnswerCall() {
    return answerCall;
  }

  @Override
  public void setAnswerCall(boolean answerCall) {
    this.answerCall = answerCall;
  }

  @Override
  public boolean isRecipentChosen() {
    return recipentChosen;
  }

  @Override
  public void setRecipentChosen(boolean recipentChosen) {
    this.recipentChosen = recipentChosen;
  }

  @Override
  public boolean isHasCome() {
    return hasCome;
  }

  @Override
  public void setHasCome(boolean hasCome) {
    this.hasCome = hasCome;
  }

  @Override
  public void prepareToAdd(OfferDto offer, int idUser, String date, String phoneNumber) {
    this.offer = (Offer) offer;
    User user = new UserImpl();
    user.setIdUser(idUser);
    this.interestedMember = user;
    this.date = date;
    this.answerCall = phoneNumber != null;
  }

  @Override
  public String toString() {
    return "InterestImpl{"
        + "idInterest=" + idInterest
        + ", offer=" + offer
        + ", interestedMember=" + interestedMember
        + ", date='" + date + '\''
        + ", answerCall=" + answerCall
        + ", recipentChosen=" + recipentChosen
        + ", hasCome=" + hasCome
        + '}';
  }
}
