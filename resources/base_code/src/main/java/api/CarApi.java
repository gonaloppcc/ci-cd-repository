package api;

import api.dto.request.CarRequest;
import application.Config;
import domain.entity.Car;
import domain.service.CarCRUD;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("car")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CarApi {

    private static final Logger LOGGER = Logger.getLogger(CarApi.class.getName());

    @Inject
    Config config;

    @Inject
    CarCRUD carCRUD;

    @GET
    public List<Car> get() {
        LOGGER.info("In Cars.get()");
        return carCRUD.read();
    }

    @GET
    @Path("{id}")
    public Car getSingle(Long id) {
        LOGGER.info("In Car.getSingle()");
        
        return carCRUD.read(id);
    }

    @POST
    @Transactional
    public Response create(CarRequest car) {
        LOGGER.info("In Car.create()");

        //If any values in CarRequest are null, throws BadRequestException with message
        if (car.brand() == null || car.model() == null || car.price() == null) {
            throw new BadRequestException("Car values were not set on request.");
        }

        Car carToPersist = new Car(car.brand(), car.model(), car.price());

        if (config.getPriceTaxBoost()) {
            LOGGER.info("Tax Boost is enabled");
            
            //Randomly sets price to be between 2 and 10 times the original price
            carToPersist.setPrice((long) (car.price() * (2 + (Math.random() * 10))));
        }

        Car carCreated = carCRUD.create(carToPersist);

        return Response.ok(carCreated).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Car update(Long id, CarRequest car) {
        LOGGER.info("In Car.update()");
        //If any values in CarRequest are null, throws BadRequestException with message
        if (car.brand() == null || car.model() == null || car.price() == null) {
            throw new BadRequestException("Car values were not set on request.");
        }
        
        Car carToUpdate = new Car(car.id(), car.brand(), car.model(), car.price());

        return carCRUD.update(carToUpdate);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        LOGGER.info("In Car.delete()");

        Car car = carCRUD.read(id);
        if (car == null) {
            throw new WebApplicationException("Car with id of " + id + " does not exist.", 404);
        }
        carCRUD.delete(id);
        return Response.status(204).build();
    }
}
