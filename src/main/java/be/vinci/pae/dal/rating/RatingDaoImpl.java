package be.vinci.pae.dal.rating;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.rating.RatingDto;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import javax.management.Query;

public class RatingDaoImpl implements RatingDao {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private Factory factory;
  @Inject
  private ObjectDao objectDao;

  @Override
  public RatingDto getOne(int id) {
    String query = "SELECT * FROM project.ratings ra WHERE ra.id_rating = ?";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatement(query)
    ) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          RatingDto rating = factory.getRating();
          rating.setIdRating(rs.getInt(1));
          rating.setObject(objectDao.getOne(rs.getInt(2)));
          rating.setDetail(rs.getString(3));
          rating.setValue(rs.getInt(4));
          return rating;
        }
        return null;
      }
    } catch (SQLException e) {
      throw new FatalException("Error : getOne(id) - ", e);
    }
  }

  @Override
  public RatingDto addOne(RatingDto ratingDto) {
    String query = "INSERT INTO project.ratings VALUES (DEFAULT,?,?,?)";
    try (
        PreparedStatement ps = dalBackendServices.getPreparedStatementInsertWithReturnId(query)
    ) {
      ps.setInt(1, ratingDto.getObject().getIdObject());
      ps.setString(2, ratingDto.getDetail());
      ps.setInt(3, ratingDto.getValue());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          ratingDto.setIdRating(rs.getInt(1));
          return ratingDto;
        }
        throw new SQLException("Rating was not added to the database");
      }
    } catch (SQLException e) {
      throw new FatalException("Error : addOne(ratingDto) - ", e);
    }
  }
}