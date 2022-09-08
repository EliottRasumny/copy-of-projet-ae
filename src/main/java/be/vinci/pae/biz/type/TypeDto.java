package be.vinci.pae.biz.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = TypeImpl.class)
public interface TypeDto {

  int getIdType();

  void setIdType(int idType);

  String getLabel();

  void setLabel(String label);
}
