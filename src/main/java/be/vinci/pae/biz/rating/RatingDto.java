package be.vinci.pae.biz.rating;

import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.object.ObjectDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RatingImpl.class)
public interface RatingDto {

  Object getObject();

  void setObject(ObjectDto object);

  int getIdRating();

  void setIdRating(int id);

  String getDetail();

  void setDetail(String detail);

  int getValue();

  void setValue(int value);
}
