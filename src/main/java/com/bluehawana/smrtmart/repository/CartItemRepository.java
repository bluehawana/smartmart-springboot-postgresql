package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
