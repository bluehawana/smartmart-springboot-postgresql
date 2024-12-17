package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 改为 Long 类型参数，因为实体类中 productId 是 Long 类型
    CartItem findByProductId(Long productId);
}