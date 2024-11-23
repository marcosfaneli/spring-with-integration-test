package com.seuprojeto.integrationtest.infra.database;

import com.seuprojeto.integrationtest.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByDescriptionContaining(String query, Pageable pageable);
}
