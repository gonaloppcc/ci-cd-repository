package domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Car {

    public static final String ID_PROPERTY_NAME = "id";

    public static final String BRAND_PROPERTY_NAME = "brand";

    public static final String MODEL_PROPERTY_NAME = "model";

    public static final String PRICE_PROPERTY_NAME = "price";

    @JsonProperty(Car.ID_PROPERTY_NAME)
    private Long id;

    @JsonProperty(Car.BRAND_PROPERTY_NAME)
    private String brand;

    @JsonProperty(Car.MODEL_PROPERTY_NAME)
    private String model;

    @JsonProperty(Car.PRICE_PROPERTY_NAME)
    private Long price;

    public Car(Long id, String brand, String model, Long price) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    public Car(String brand, String model, Long price) {
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
}