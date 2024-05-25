package com.food.ordering.system.domain.valueobject;

public class CustomerId extends BaseId<String> {

    public CustomerId(String value) {
        super(value);
    }

    public static CustomerId generate() {
        return new CustomerId(java.util.UUID.randomUUID().toString());
    }
}
