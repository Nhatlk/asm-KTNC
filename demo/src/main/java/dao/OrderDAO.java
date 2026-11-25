package dao;

import config.EMF;
import dto.CreateOrderRequest;
import dto.OrderLineRequest;

import entity.Customer;
import entity.Order;
import entity.OrderItem;
import entity.OrderItemId;
import entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDAO extends BaseDAOImpl<Integer, Order> {

    public OrderDAO() {
        super(Order.class);
    }

    public Order createOrder(CreateOrderRequest request) {

        try (EntityManager em = EMF.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                // 1. Tìm khách hàng
                Customer customer = null;

                if (request.customerId() != null) {
                    customer = em.find(
                        Customer.class,
                        request.customerId()
                    );

                    if (customer == null) {
                        throw new IllegalArgumentException(
                            "Khách hàng không tồn tại!"
                        );
                    }
                }

                // 2. Tạo đơn hàng
                Order order = new Order();

                order.setCustomer(customer);
                order.setOrderDate(LocalDateTime.now());
                order.setStatus("NEW");
                order.setNote(request.note());

                em.persist(order);

                // 3. Xử lý từng sản phẩm
                for (OrderLineRequest item : request.items()) {

                    Product product = em.find(
                        Product.class,
                        item.productId(),
                        LockModeType.PESSIMISTIC_WRITE
                    );

                    if (product == null ||
                        !Boolean.TRUE.equals(product.getActive())) {

                        throw new IllegalArgumentException(
                            "Sản phẩm không tồn tại hoặc ngừng bán!"
                        );
                    }

                    if (product.getStock() < item.quantity()) {
                        throw new IllegalArgumentException(
                            "Sản phẩm không đủ tồn kho: "
                            + product.getName()
                        );
                    }

                    // 4. Trừ tồn kho
                    product.setStock(
                        product.getStock() - item.quantity()
                    );

                    // 5. Tạo chi tiết đơn hàng
                    OrderItem detail = new OrderItem();

                    detail.setId(new OrderItemId(
                        order.getId(),
                        product.getId()
                    ));

                    detail.setOrder(order);
                    detail.setProduct(product);
                    detail.setQuantity(item.quantity());

                    // Lưu giá tại thời điểm mua
                    detail.setUnitPrice(product.getPrice());

                    em.persist(detail);
                }

                // 6. Lưu toàn bộ thay đổi
                tx.commit();

                return order;

            } catch (RuntimeException e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }
    

    public boolean cancelOrder(Integer orderId) {

    try (EntityManager em = EMF.getEntityManager()) {

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. Tìm và khóa đơn hàng
            Order order = em.find(
                Order.class,
                orderId,
                LockModeType.PESSIMISTIC_WRITE
            );

            if (order == null) {
                throw new IllegalArgumentException(
                    "Đơn hàng không tồn tại!"
                );
            }

            // 2. Kiểm tra trạng thái
            if (!"NEW".equals(order.getStatus())) {
                throw new IllegalStateException(
                    "Chỉ được hủy đơn hàng ở trạng thái NEW!"
                );
            }

            // 3. Lấy chi tiết đơn hàng
            List<OrderItem> items = em.createQuery(
                "SELECT i FROM OrderItem i "
                + "WHERE i.order.id = :orderId",
                OrderItem.class
            )
            .setParameter("orderId", orderId)
            .getResultList();

            // 4. Hoàn lại tồn kho
            for (OrderItem item : items) {

                Product product = em.find(
                    Product.class,
                    item.getProduct().getId(),
                    LockModeType.PESSIMISTIC_WRITE
                );

                if (product == null) {
                    throw new IllegalStateException(
                        "Không tìm thấy sản phẩm để hoàn kho!"
                    );
                }

                int restoredStock = Math.addExact(
                    product.getStock(),
                    item.getQuantity()
                );

                product.setStock(restoredStock);
            }

            // 5. Chuyển trạng thái
            order.setStatus("CANCELLED");

            tx.commit();

            return true;

        } catch (RuntimeException e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw e;
        }
    }
}
}