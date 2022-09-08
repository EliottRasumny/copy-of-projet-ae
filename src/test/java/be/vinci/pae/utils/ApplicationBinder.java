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
import be.vinci.pae.dal.user.UserDao;
import be.vinci.pae.dal.user.UserDaoImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    //USER
    bind(UserUccImpl.class).to(UserUcc.class).in(Singleton.class);
    bind(Mockito.mock(UserDaoImpl.class)).to(UserDao.class);

    //bind(Mockito.mock(UserUccImpl.class)).to(UserUcc.class);

    //ADDRESS
    bind(Mockito.mock(AddressDaoImpl.class)).to(AddressDao.class);

    //FACTORY BIZ
    bind(FactoryImpl.class).to(Factory.class).in(Singleton.class);

    //OFFER
    bind(OfferUccImpl.class).to(OfferUcc.class).in(Singleton.class);
    bind(Mockito.mock(OfferDaoImpl.class)).to(OfferDao.class);

    //INTEREST
    bind(InterestUccImpl.class).to(InterestUcc.class).in(Singleton.class);
    bind(Mockito.mock(InterestDaoImpl.class)).to(InterestDao.class);

    //OBJECT
    bind(ObjectUccImpl.class).to(ObjectUcc.class).in(Singleton.class);
    bind(Mockito.mock(ObjectDaoImpl.class)).to(ObjectDao.class);

    //RATING
    bind(Mockito.mock(RatingDaoImpl.class)).to(RatingDao.class);

    // DAL
    bind(Mockito.mock(DalServicesImpl.class)).to(DalServices.class);


  }
}