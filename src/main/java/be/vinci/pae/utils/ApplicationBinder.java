package be.vinci.pae.utils;

import be.vinci.pae.biz.Factory;
import be.vinci.pae.biz.FactoryImpl;
import be.vinci.pae.biz.interest.InterestUcc;
import be.vinci.pae.biz.interest.InterestUccImpl;
import be.vinci.pae.biz.object.ObjectUcc;
import be.vinci.pae.biz.object.ObjectUccImpl;
import be.vinci.pae.biz.offer.OfferUcc;
import be.vinci.pae.biz.offer.OfferUccImpl;
import be.vinci.pae.biz.user.UserUcc;
import be.vinci.pae.biz.user.UserUccImpl;
import be.vinci.pae.dal.DalBackendServices;
import be.vinci.pae.dal.DalServices;
import be.vinci.pae.dal.DalServicesImpl;
import be.vinci.pae.dal.address.AddressDao;
import be.vinci.pae.dal.address.AddressDaoImpl;
import be.vinci.pae.dal.interest.InterestDao;
import be.vinci.pae.dal.interest.InterestDaoImpl;
import be.vinci.pae.dal.object.ObjectDao;
import be.vinci.pae.dal.object.ObjectDaoImpl;
import be.vinci.pae.dal.offer.OfferDao;
import be.vinci.pae.dal.offer.OfferDaoImpl;
import be.vinci.pae.dal.rating.RatingDao;
import be.vinci.pae.dal.rating.RatingDaoImpl;
import be.vinci.pae.dal.type.TypeDao;
import be.vinci.pae.dal.type.TypeDaoImpl;
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.dal.user.UserDaoImpl;
import be.vinci.pae.utils.token.TokenManager;
import be.vinci.pae.utils.token.TokenManagerImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    //FACTORY BIZ
    bind(FactoryImpl.class).to(Factory.class).in(Singleton.class);

    //DAL SERVICES
    bind(DalServicesImpl.class).to(DalServices.class).to(DalBackendServices.class)
        .in(Singleton.class);

    //USER
    bind(UserDaoImpl.class).to(UserDao.class).in(Singleton.class);
    bind(UserUccImpl.class).to(UserUcc.class).in(Singleton.class);

    //ADDRESS
    bind(AddressDaoImpl.class).to(AddressDao.class).in(Singleton.class);

    //OBJECT
    bind(ObjectDaoImpl.class).to(ObjectDao.class).in(Singleton.class);
    bind(ObjectUccImpl.class).to(ObjectUcc.class).in(Singleton.class);

    //OFFER
    bind(OfferDaoImpl.class).to(OfferDao.class).in(Singleton.class);
    bind(OfferUccImpl.class).to(OfferUcc.class).in(Singleton.class);

    //INTEREST
    bind(InterestDaoImpl.class).to(InterestDao.class).in(Singleton.class);
    bind(InterestUccImpl.class).to(InterestUcc.class).in(Singleton.class);

    //TYPE
    bind(TypeDaoImpl.class).to(TypeDao.class).in(Singleton.class);

    //Rating
    bind(RatingDaoImpl.class).to(RatingDao.class).in(Singleton.class);

    //TOKEN
    bind(TokenManagerImpl.class).to(TokenManager.class).in(Singleton.class);
  }
}
