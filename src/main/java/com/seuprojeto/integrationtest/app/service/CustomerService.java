package com.seuprojeto.integrationtest.app.service;

import com.seuprojeto.integrationtest.domain.Customer;
import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.infra.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Value("${customer.api.url}")
    private String customerApiUrl;

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(String id) {
        return this.customerRepository.findById(id)
                .orElseGet(() -> queryCustomerApi(id)
                        .orElseThrow(() -> new CustomerNotFoundException(id)));
    }

    private Optional<Customer> queryCustomerApi(String id) {
        try {
            final String url = customerApiUrl + "/{id}";

            Optional<Customer> customer = Optional.ofNullable(new RestTemplateBuilder()
                    .build()
                    .getForObject(url, Customer.class, id));

            customer.ifPresent(this.customerRepository::save);

            return customer;
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
