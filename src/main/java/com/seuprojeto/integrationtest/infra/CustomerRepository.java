package com.seuprojeto.integrationtest.infra;

import com.seuprojeto.integrationtest.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
}
