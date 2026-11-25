package service;

import java.util.List;
import entity.Customer;

public interface CustomerService {

    List<Customer> getAll();

    Customer getById(Integer id);

    Customer create(Customer customer);

    Customer update(Customer customer);

    boolean delete(Integer id);
}