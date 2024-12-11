package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.OrderCreateDTO;
import com.bluehawana.smrtmart.dto.OrderDTO;
import com.bluehawana.smrtmart.dto.OrderUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Order;
import com.bluehawana.smrtmart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Order createOrder(OrderCreateDTO orderDTO) {
        Order order = new Order();
        order.setStatus("PENDING");
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setShippingAddress(orderDTO.getShippingAddress());
        return orderRepository.save(order);
    }

    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    public OrderDTO updateOrder(Integer id, OrderUpdateDTO orderUpdate) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(orderUpdate.getStatus());
        order.setTotalAmount(orderUpdate.getTotalAmount());
        order.setShippingAddress(orderUpdate.getShippingAddress());
        order.setStripePaymentId(orderUpdate.getStripePaymentId());
        return new OrderDTO(orderRepository.save(order));
    }
}