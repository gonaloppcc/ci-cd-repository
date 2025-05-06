package application;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Config {
    @ConfigProperty(name = "price.tax.boost")
    Boolean priceTaxBoost;

    public Boolean getPriceTaxBoost() {
        return priceTaxBoost;
    }

}