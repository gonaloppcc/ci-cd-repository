package domain.assembler;

import domain.entity.Car;
import infrastructure.database.entity.CarEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DomainAssembler {
    public CarEntity assembleCar(@NotNull final Car origin) {
        CarEntity carEntity = new CarEntity();
        carEntity.setId(origin.getId());
        carEntity.setBrand(origin.getBrand());
        carEntity.setModel(origin.getModel());
        carEntity.setPrice(origin.getPrice());
        return carEntity;
    }

    public Car assembleCarEntity(@NotNull final CarEntity carEntity) {
        Car car = new Car(carEntity.getId(), carEntity.getBrand(), carEntity.getModel(), carEntity.getPrice());
        return car;
    }
    
}
