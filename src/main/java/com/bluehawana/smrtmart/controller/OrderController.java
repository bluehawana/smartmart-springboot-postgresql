package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.OrderCreateDTO;
import com.bluehawana.smrtmart.dto.OrderDTO;
import com.bluehawana.smrtmart.dto.OrderUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Order;
import com.bluehawana.smrtmart.model.User;
import com.bluehawana.smrtmart.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal User user) {
        log.info("Getting orders for authenticated user: {}", user.getUsername());
        if (user != null) {
            // Fetch orders for authenticated user
            List<Order> orders = orderService.getOrdersByUser(user);
            return ResponseEntity.ok(orders);
        } else {
            // Fetch all orders for admin role
            log.info("Fetching all orders for admin");
            return ResponseEntity.ok(orderService.getAllOrders());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Integer id) {
        log.info("Getting order with id: {}", id);
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateDTO orderRequest) {
        log.info("Creating new order");
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Integer id,
            @RequestBody OrderUpdateDTO orderUpdate) {
        log.info("Updating order with id: {}", id);
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, orderUpdate));
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        log.info("Deleting order with id: {}", id);
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}