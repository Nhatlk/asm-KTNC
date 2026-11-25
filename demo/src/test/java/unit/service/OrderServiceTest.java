package unit.service;
import dao.OrderDAO;
import dto.CreateOrderRequest;
import dto.OrderLineRequest;

import entity.Order;
import service.OrderServiceImpl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Test
    void testCreateSuccess() {

        // ARRANGE
        OrderDAO mockDAO = mock(OrderDAO.class);

        OrderServiceImpl service =
                new OrderServiceImpl(mockDAO);

        CreateOrderRequest request =
                new CreateOrderRequest(
                    1,
                    "Giao buổi sáng",
                    List.of(
                        new OrderLineRequest(1, 2)
                    )
                );

        Order order = new Order();
        order.setId(1);
        order.setStatus("NEW");

        when(mockDAO.createOrder(request))
                .thenReturn(order);

        // ACT
        Order result = service.create(request);

        // ASSERT
        assertNotNull(result);
        assertEquals("NEW", result.getStatus());

        verify(mockDAO, times(1))
                .createOrder(request);
    }

    @Test
    void testCreateWithEmptyItems() {

        // ARRANGE
        OrderDAO mockDAO = mock(OrderDAO.class);

        OrderServiceImpl service =
                new OrderServiceImpl(mockDAO);

        CreateOrderRequest request =
                new CreateOrderRequest(
                    1,
                    null,
                    List.of()
                );

        // ACT + ASSERT
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create(request)
        );

        verify(mockDAO, never())
                .createOrder(any());
    }

    @Test
    void testCreateWithInvalidQuantity() {

        // ARRANGE
        OrderDAO mockDAO = mock(OrderDAO.class);

        OrderServiceImpl service =
                new OrderServiceImpl(mockDAO);

        CreateOrderRequest request =
                new CreateOrderRequest(
                    1,
                    null,
                    List.of(
                        new OrderLineRequest(1, -5)
                    )
                );

        // ACT + ASSERT
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create(request)
        );

        verify(mockDAO, never())
                .createOrder(any());
    }
}