package be.vinci.pae.dal.address;

import be.vinci.pae.biz.address.AddressDto;

public interface AddressDao {

  /**
   * Prepare a request to retrieve an address from the Database thanks to its id.
   *
   * @param id of the address that needs to be retrieved.
   * @return the address as an AddressDto
   */
  AddressDto getOne(int id);

  /**
   * Prepare a request to add an address to the database.
   *
   * @param addressDto that needs to be added.
   * @return the address added to the database.
   */
  AddressDto addOne(AddressDto addressDto);

  /**
   * Modify the address.
   *
   * @param addressDto the address to modify with new values
   * @return the new address
   */
  AddressDto modify(AddressDto addressDto);
}
