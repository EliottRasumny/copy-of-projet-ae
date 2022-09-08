package be.vinci.pae.dal.address;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.address.AddressDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import javax.management.Query;

public class AddressDaoImpl implements AddressDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;

  @Override
  public AddressDto getOne(int id) {
    String query = "SELECT * FROM project.addresses ad WHERE ad.id_address = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          AddressDto address = factory.getAddress();
          address.setIdAddress(rs.getInt(1));
          address.setStreet(rs.getString(2));
          address.setBuildingNumber(rs.getString(3));
          address.setUnitNumber(rs.getString(4));
          address.setPostcode(rs.getString(5));
          address.setCommune(rs.getString(6));
          address.setVersionAddress(rs.getInt(7));
          return address;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(id) - ", e);
    }
  }

  @Override
  public AddressDto addOne(AddressDto addressDto) {
    String query = "INSERT INTO project.addresses VALUES (DEFAULT,?,?,?,?,?, 1)";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)
    ) {
      ps.setString(1, addressDto.getStreet());
      ps.setString(2, addressDto.getBuildingNumber());
      ps.setString(3, addressDto.getUnitNumber());
      ps.setString(4, addressDto.getPostcode());
      ps.setString(5, addressDto.getCommune());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          addressDto.setIdAddress(rs.getInt(1));
          return addressDto;
        }
        throw new SQLException("Address was not added to the database");
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne(adrresDto) - ", e);
    }
  }

  @Override
  public AddressDto modify(AddressDto addressDto) {
    String query = "UPDATE project.addresses "
        + " SET street=?,"
        + " building_number=?, "
        + " unit_number=?,"
        + " postcode=?,"
        + " commune=?,"
        + " version_address=version_address+1 "
        + " WHERE id_address=?"
        + " AND version_address = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setString(1, addressDto.getStreet());
      ps.setString(2, addressDto.getBuildingNumber());
      ps.setString(3, addressDto.getUnitNumber());
      ps.setString(4, addressDto.getPostcode());
      ps.setString(5, addressDto.getCommune());
      ps.setInt(6, addressDto.getIdAddress());
      ps.setInt(7, addressDto.getVersionAddress());
      int res = ps.executeUpdate();
      if (res == 0) {
        throw new ConflictException("Error : modify version not up to date");
      }
      return addressDto;
    } catch (SQLException e) {
      throw new FatalException("Error : modify(adrresDto) - ", e);
    }
  }
}