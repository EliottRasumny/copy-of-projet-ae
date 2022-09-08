package be.vinci.pae.dal.rating;

import be.vinci.pae.biz.rating.RatingDto;

public interface RatingDao {

  /**
   * Prepare a request to retrieve a rating from the Database thanks to its id.
   *
   * @param id of the rating that needs to be retrieved.
   * @return the rating as
   */
  RatingDto getOne(int id);

  /**
   * Prepare a request to add a rating to the database. Throw an FatalException if not.
   *
   * @param ratingDto that needs to be added.
   * @return The rating created
   */
  RatingDto addOne(RatingDto ratingDto);
}
