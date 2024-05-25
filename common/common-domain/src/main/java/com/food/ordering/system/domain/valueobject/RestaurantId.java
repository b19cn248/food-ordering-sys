package com.food.ordering.system.domain.valueobject;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID> {

    public RestaurantId(UUID value) {
        super(value);
    }

    public static RestaurantId generate() {
        return new RestaurantId(UUID.randomUUID());
    }
}
