// src/main/java/com/bluehawana/smrtmart/repository/OrderItemRepository.java
package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);
}
