package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.Order;
import com.bluehawana.smrtmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);

    List<Order> findByUser(User user);
}

