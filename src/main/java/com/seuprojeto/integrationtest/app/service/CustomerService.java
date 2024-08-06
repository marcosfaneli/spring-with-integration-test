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
        final String url = customerApiUrl +"/{id}";

        final Customer customer = new RestTemplateBuilder()
                .build()
                .getForObject(url, Customer.class, id);

        final Optional<Customer> foundCustomer = this.customerRepository.findById(id);

        if (customer == null && foundCustomer.isEmpty()) {
            throw new CustomerNotFoundException(id);
        }

        System.out.println(customer);

        return this.customerRepository.save(customer);
    }
}
