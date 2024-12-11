package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {


}