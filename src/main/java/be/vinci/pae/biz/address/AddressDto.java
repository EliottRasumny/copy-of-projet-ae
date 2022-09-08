package be.vinci.pae.biz.address;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * DTO of Address object.
 */
@JsonDeserialize(as = AddressImpl.class)
public interface AddressDto {

  int getVersionAddress();

  /**
   * Set a new version to the address object.
   *
   * @param versionAddress the versionAddress.
   */
  void setVersionAddress(int versionAddress);

  /**
   * Get the id of the address object.
   *
   * @return the id.
   */
  int getIdAddress();

  /**
   * Set a new id to the address object.
   *
   * @param idAddress new id of the address.
   */
  void setIdAddress(int idAddress);

  /**
   * Get the street of the address object.
   *
   * @return the street.
   */
  String getStreet();

  /**
   * Set a new street to the address object.
   *
   * @param street new street of the address.
   */
  void setStreet(String street);

  /**
   * Get the building number of the address object.
   *
   * @return the building number.
   */
  String getBuildingNumber();

  /**
   * Set a new building number to the address object.
   *
   * @param buildingNumber new building number of the address.
   */
  void setBuildingNumber(String buildingNumber);

  /**
   * Get the unit number of the address object.
   *
   * @return the unit number.
   */
  String getUnitNumber();

  /**
   * Set a new unit number to the address object.
   *
   * @param unitNumber the new unit number of the address.
   */
  void setUnitNumber(String unitNumber);

  /**
   * Get the post code of the address object.
   *
   * @return the post code.
   */
  String getPostcode();

  /**
   * Set a new post code to the address object.
   *
   * @param postCode the new post code of the address.
   */
  void setPostcode(String postCode);

  /**
   * Get the commune of the address object.
   *
   * @return the commune.
   */
  String getCommune();

  /**
   * Set a new commune to the address object.
   *
   * @param commune the new commune of the address.
   */
  void setCommune(String commune);
}
