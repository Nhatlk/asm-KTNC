package unit.service;
import dao.CustomerDAO;
import entity.Customer;
import service.CustomerServiceImpl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Test
    void testCreateSuccess() {

        // ARRANGE
        CustomerDAO mockDAO = mock(CustomerDAO.class);

        CustomerServiceImpl service =
                new CustomerServiceImpl(mockDAO);

        Customer customer = new Customer();

        customer.setFullName("Nguyen Minh Nhat");
        customer.setPhone("0901234567");

        when(mockDAO.insert(customer))
                .thenReturn(customer);

        // ACT
        Customer result = service.create(customer);

        // ASSERT
        assertNotNull(result);

        assertEquals(
            "Nguyen Minh Nhat",
            result.getFullName()
        );

        verify(mockDAO, times(1)).insert(customer);
    }

    @Test
    void testCreateInvalidPhone() {

        // ARRANGE
        CustomerDAO mockDAO = mock(CustomerDAO.class);

        CustomerServiceImpl service =
                new CustomerServiceImpl(mockDAO);

        Customer customer = new Customer();

        customer.setFullName("Nguyen Minh Nhat");
        customer.setPhone("123");

        // ACT + ASSERT
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create(customer)
        );

        verify(mockDAO, never()).insert(any());
    }

    @Test
    void testCreateEmptyName() {

        // ARRANGE
        CustomerDAO mockDAO = mock(CustomerDAO.class);

        CustomerServiceImpl service =
                new CustomerServiceImpl(mockDAO);

        Customer customer = new Customer();

        customer.setFullName("");
        customer.setPhone("0901234567");

        // ACT + ASSERT
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create(customer)
        );

        verify(mockDAO, never()).insert(any());
    }
}