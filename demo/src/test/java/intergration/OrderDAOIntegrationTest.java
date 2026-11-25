package intergration;
import config.EMF;
import dao.OrderDAO;
import dto.CreateOrderRequest;
import dto.OrderLineRequest;
import entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDAOIntegrationTest {

    @Test
    @DisplayName("TC01 - Không đủ tồn kho: Rollback thành công")
    void testRollbackWhenStockNotEnough() {

        // ARRANGE
        OrderDAO dao = new OrderDAO();

        int stockBefore;
        long ordersBefore;

        try (EntityManager em = EMF.getEntityManager()) {

            stockBefore = ((Number) em.createNativeQuery(
                "SELECT Stock FROM Products WHERE ProductId = 1"
            ).getSingleResult()).intValue();

            ordersBefore = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM Orders"
            ).getSingleResult()).longValue();
        }

        // Sản phẩm 1 hợp lệ
        // Sản phẩm 4 yêu cầu số lượng rất lớn
        CreateOrderRequest request = new CreateOrderRequest(
            null,
            "Kiểm tra Rollback",
            List.of(
                new OrderLineRequest(1, 1),
                new OrderLineRequest(4, Integer.MAX_VALUE)
            )
        );

        // ACT + ASSERT
        assertThrows(
            IllegalArgumentException.class,
            () -> dao.createOrder(request)
        );

        // Kiểm tra DB không bị thay đổi
        try (EntityManager em = EMF.getEntityManager()) {

            int stockAfter = ((Number) em.createNativeQuery(
                "SELECT Stock FROM Products WHERE ProductId = 1"
            ).getSingleResult()).intValue();

            long ordersAfter = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM Orders"
            ).getSingleResult()).longValue();

            assertEquals(stockBefore, stockAfter);
            System.out.println("PASS: Tồn kho không thay đổi!");
            assertEquals(ordersBefore, ordersAfter);
        }
    }

    @Test 
    @DisplayName ("TC02 - Tạo đơn hàng thành công")
    void testCreateOrderSuccess(){

        //arrage
        OrderDAO dao = new OrderDAO();

        int productId = 1;
        int quantity = 2;

        Integer orderId = null;
        int stockBefore;

        // lay ton kho ban dau 
        try(EntityManager em = EMF.getEntityManager()){
            
            stockBefore = ((Number) em.createNativeQuery("select Stock from Products where ProductId = :id")
                .setParameter("id", productId)
                .getSingleResult())
                .intValue();
        }

        assertTrue(stockBefore >= quantity, "sản phẩm không đủ tồn kho để chạy test!");

        CreateOrderRequest request = new CreateOrderRequest(
            null,
            "Integer Test TC02",
            List.of(new OrderLineRequest(productId, quantity))
            );


            try{
                //act
                Order order = dao.createOrder(request);
                orderId = order.getId();

                //assert
                assertNotNull(orderId, "Tạo đơn hàng thất bại!");
                assertEquals("NEW", order.getStatus(), "Trạng thái đơn hàng không đúng!");
                try(EntityManager em = EMF.getEntityManager()){
                    int stockAfter = ((Number) em.createNativeQuery("select Stock from Products where ProductId = :id")
                        .setParameter("id", productId)
                        .getSingleResult())
                        .intValue();
                    long itemCount = ((Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM OrderItems WHERE OrderId = :id"
                    )
                    .setParameter("id", orderId)
                    .getSingleResult()).longValue();

                        assertEquals(stockBefore - quantity, stockAfter);
                        assertEquals(1L, itemCount);

                }
            }finally {

            // Dọn dữ liệu test đã tạo
            if (orderId != null) {

                try (EntityManager em = EMF.getEntityManager()) {

                    EntityTransaction tx = em.getTransaction();

                    try {
                        tx.begin();

                        em.createNativeQuery(
                            "DELETE FROM OrderItems WHERE OrderId = :id"
                        )
                        .setParameter("id", orderId)
                        .executeUpdate();

                        em.createNativeQuery(
                            "DELETE FROM Orders WHERE OrderId = :id"
                        )
                        .setParameter("id", orderId)
                        .executeUpdate();

                        em.createNativeQuery(
                            "UPDATE Products SET Stock = Stock + :quantity "
                            + "WHERE ProductId = :id"
                        )
                        .setParameter("quantity", quantity)
                        .setParameter("id", productId)
                        .executeUpdate();

                        tx.commit();

                    } catch (RuntimeException e) {
                        if (tx.isActive()) {
                            tx.rollback();
                        }
                        throw e;
                    }
                }
            }
        }
    }
}

            