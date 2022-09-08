package be.vinci.pae.biz.address;

/**
 * Interface of Address object.
 */
public interface Address extends AddressDto {

  int getIdAddress();

  void setIdAddress(int idAddress);

  String getStreet();

  void setStreet(String street);

  String getBuildingNumber();

  void setBuildingNumber(String buildingNumber);

  String getUnitNumber();

  void setUnitNumber(String unitNumber);

  String getPostcode();

  void setPostcode(String postcode);

  String getCommune();

  void setCommune(String commune);


}
