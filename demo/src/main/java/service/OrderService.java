package service;

import dto.CreateOrderRequest;
import entity.Order;

import java.util.List;

public interface OrderService {

    Order create(CreateOrderRequest request);

    Order getById(Integer id);

    List<Order> getAll();

    boolean cancel(Integer orderId);
}