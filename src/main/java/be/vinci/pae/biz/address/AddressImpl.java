package be.vinci.pae.biz.address;

import java.util.Objects;

/**
 * Implementation of Address interface.
 */
public class AddressImpl implements Address {

  private int idAddress;
  private String street;
  private String buildingNumber;
  private String unitNumber;
  private String postcode;
  private String commune;
  private int versionAddress;

  public AddressImpl() {
  }

  @Override
  public int getVersionAddress() {
    return versionAddress;
  }

  @Override
  public void setVersionAddress(int versionAddress) {
    this.versionAddress = versionAddress;
  }

  @Override
  public int getIdAddress() {
    return idAddress;
  }

  @Override
  public void setIdAddress(int idAddress) {
    this.idAddress = idAddress;
  }

  @Override
  public String getStreet() {
    return street;
  }

  @Override
  public void setStreet(String street) {
    this.street = street;
  }

  @Override
  public String getBuildingNumber() {
    return buildingNumber;
  }

  @Override
  public void setBuildingNumber(String buildingNumber) {
    this.buildingNumber = buildingNumber;
  }

  @Override
  public String getUnitNumber() {
    return unitNumber;
  }

  @Override
  public void setUnitNumber(String unitNumber) {
    this.unitNumber = unitNumber;
  }

  @Override
  public String getPostcode() {
    return postcode;
  }

  @Override
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddressImpl address = (AddressImpl) o;
    return idAddress == address.idAddress && Objects.equals(street, address.street)
        && Objects.equals(buildingNumber, address.buildingNumber)
        && Objects.equals(unitNumber, address.unitNumber) && Objects.equals(
        postcode, address.postcode) && Objects.equals(commune, address.commune);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idAddress, street, buildingNumber, unitNumber, postcode, commune);
  }

  @Override
  public String getCommune() {
    return commune;
  }

  @Override
  public void setCommune(String commune) {
    this.commune = commune;
  }

  @Override
  public String toString() {
    return "AddressImpl{"
        + "idAddress=" + idAddress
        + ", street='" + street + '\''
        + ", buildingNumber='" + buildingNumber + '\''
        + ", unitNumber='" + unitNumber + '\''
        + ", postcode='" + postcode + '\''
        + ", commune='" + commune + '\''
        + '}';
  }
}
