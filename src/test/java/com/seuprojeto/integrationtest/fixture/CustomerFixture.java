package com.seuprojeto.integrationtest.fixture;

import com.seuprojeto.integrationtest.domain.Customer;

public class CustomerFixture {
    public static Customer createCustomer() {
        return new Customer("1", "John Doe", "john@doe.com");
    }
}
