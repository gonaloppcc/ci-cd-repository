package infrastructure.database.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "Car")
@Cacheable
public class CarEntity extends PanacheEntity {

    @Column
    public String brand;

    @Column
    public String model;

    @Column
    public Long price;

    public CarEntity() {
    }

    public CarEntity(String brand, String model, Long price) {
        this.brand = brand;
        this.model = model;
        this.price = price;
    }  

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
