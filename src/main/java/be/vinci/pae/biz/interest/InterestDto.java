package be.vinci.pae.biz.interest;

import be.vinci.pae.biz.offer.OfferDto;
import be.vinci.pae.biz.user.UserDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * DTO of Interest object.
 */
@JsonDeserialize(as = InterestImpl.class)
public interface InterestDto {

  int getIdInterest();

  void setIdInterest(int idInterest);

  OfferDto getOffer();

  void setOffer(OfferDto offer);

  UserDto getInterestedMember();

  void setInterestedMember(UserDto interestedMember);

  String getDate();

  void setDate(String date);

  boolean isAnswerCall();

  void setAnswerCall(boolean answerCall);

  boolean isRecipentChosen();

  void setRecipentChosen(boolean recipentChosen);

  boolean isHasCome();

  void setHasCome(boolean hasCome);
}
