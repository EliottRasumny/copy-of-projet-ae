package be.vinci.pae.utils;

import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.ForbiddenException;
import be.vinci.pae.utils.exceptions.NoResourceException;
import be.vinci.pae.utils.exceptions.PreconditionException;
import be.vinci.pae.utils.exceptions.TokenException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class WebExceptionMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable exception) {
    Logger logger = Logger.getLogger(LoggerManager.LOGGER_NAME);

    logger.log(Level.INFO, "###############");
    logger.log(Level.SEVERE, exception.getMessage(), exception);
    logger.log(Level.INFO, "###############");

    // exception.printStackTrace();
    if (exception instanceof FatalException) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof ConflictException) {
      return Response.status(Status.CONFLICT)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof PreconditionException) {
      return Response.status(Status.PRECONDITION_FAILED)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof BadRequestException) {
      return Response.status(Status.BAD_REQUEST)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof UnauthorizedException) {
      return Response.status(Status.UNAUTHORIZED)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof ForbiddenException) {
      return Response.status(Status.FORBIDDEN)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof NoResourceException) {
      return Response.status(Status.NOT_FOUND)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof TokenException) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof WebApplicationException) {
      // the response is already prepared
      return ((WebApplicationException) exception).getResponse();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(exception.getMessage())
        .build();
  }
}

