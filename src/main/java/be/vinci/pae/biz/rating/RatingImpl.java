package be.vinci.pae.biz.rating;

import be.vinci.pae.biz.object.Object;
import be.vinci.pae.biz.object.ObjectDto;

public class RatingImpl implements Rating {

  private int idRating;
  private Object object;
  private String detail;
  private int value;

  public RatingImpl() {
  }

  @Override
  public int getIdRating() {
    return idRating;
  }

  @Override
  public void setIdRating(int idRating) {
    this.idRating = idRating;
  }

  @Override
  public Object getObject() {
    return object;
  }

  @Override
  public void setObject(ObjectDto object) {
    this.object = (Object) object;
  }

  @Override
  public String getDetail() {
    return detail;
  }

  @Override
  public void setDetail(String detail) {
    this.detail = detail;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "RatingImpl{"
        + "idRating=" + idRating
        + ", object=" + object
        + ", detail='" + detail + '\''
        + ", value=" + value
        + '}';
  }
}
