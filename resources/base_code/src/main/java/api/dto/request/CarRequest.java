package api.dto.request;

import jakarta.annotation.Nullable;

public record CarRequest(@Nullable Long id, @Nullable String brand, @Nullable String model, @Nullable Long price) {

}