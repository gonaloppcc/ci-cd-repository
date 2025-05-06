package domain.service;

import domain.assembler.DomainAssembler;
import domain.entity.Car;
import infrastructure.database.entity.CarEntity;
import infrastructure.database.repository.CarRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * DKR Key service.
 */
@Dependent
public class CarCRUD {

    @Inject
    DomainAssembler assembler;

    @Inject
    CarRepository carRepository;

    @Transactional
    public Car create(
            final Car car) {

        CarEntity carEntity = assembler.assembleCar(car);

        carRepository.persistAndFlush(carEntity);

        return car;
    }

    @Transactional
    public List<Car> read() {
        return carRepository.streamAll().map(assembler::assembleCarEntity).toList();
    }

    @Transactional
    public Car read(Long id) {
        return assembler.assembleCarEntity(carRepository.findById(id));
    }

    @Transactional
    public Car update(
            final Car car) {

        CarEntity carEntity = carRepository.findById(car.getId());

        carEntity.setBrand(car.getBrand());
        carEntity.setModel(car.getModel());
        carEntity.setPrice(car.getPrice());

        carRepository.persist(carEntity);

        return assembler.assembleCarEntity(carEntity);
    }

    public void delete(Long id) {
        carRepository.deleteById(id);
    }
}


