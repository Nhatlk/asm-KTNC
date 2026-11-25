package service;

import dao.OrderDAO;

import dto.CreateOrderRequest;
import dto.OrderLineRequest;

import entity.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderServiceImpl implements OrderService {

    private final OrderDAO dao;

    public OrderServiceImpl() {
        this(new OrderDAO());
    }

    public OrderServiceImpl(OrderDAO dao) {
        this.dao = dao;
    }

    @Override
    public Order create(CreateOrderRequest request) {

        if (request == null ||
            request.items() == null ||
            request.items().isEmpty()) {

            throw new IllegalArgumentException(
                "Đơn hàng phải có ít nhất một sản phẩm!"
            );
        }

        if (request.customerId() != null &&
            request.customerId() <= 0) {

            throw new IllegalArgumentException(
                "ID khách hàng không hợp lệ!"
            );
        }

        Set<Integer> productIds = new HashSet<>();

        for (OrderLineRequest item : request.items()) {

            if (item == null ||
                item.productId() == null ||
                item.productId() <= 0 ||
                item.quantity() == null ||
                item.quantity() <= 0) {

                throw new IllegalArgumentException(
                    "Thông tin sản phẩm không hợp lệ!"
                );
            }

            if (!productIds.add(item.productId())) {
                throw new IllegalArgumentException(
                    "Sản phẩm bị trùng trong đơn hàng!"
                );
            }
        }

        return dao.createOrder(request);
    }

    @Override
    public Order getById(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "ID đơn hàng không hợp lệ!"
            );
        }

        return dao.findById(id);
    }

    @Override
    public List<Order> getAll() {
        return dao.findAll();
    }

    @Override
    public boolean cancel(Integer orderId) {

    if (orderId == null || orderId <= 0) {
        throw new IllegalArgumentException(
            "ID đơn hàng không hợp lệ!"
        );
    }

    return dao.cancelOrder(orderId);
}
}