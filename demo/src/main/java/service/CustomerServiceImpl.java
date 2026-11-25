package service;

import java.util.List;

import dao.CustomerDAO;
import entity.Customer;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO dao;

    public CustomerServiceImpl() {
        this(new CustomerDAO());
    }

    public CustomerServiceImpl(CustomerDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Customer> getAll() {
        return dao.findAll();
    }

    @Override
    public Customer getById(Integer id) {

        validateId(id);

        return dao.findById(id);
    }

    @Override
    public Customer create(Customer customer) {

        validate(customer);

        return dao.insert(customer);
    }

    @Override
    public Customer update(Customer customer) {

        validate(customer);
        validateId(customer.getId());

        if (dao.findById(customer.getId()) == null) {
            throw new IllegalArgumentException(
                "Khách hàng không tồn tại!"
            );
        }

        return dao.update(customer);
    }

    @Override
    public boolean delete(Integer id) {

        validateId(id);

        return dao.delete(id);
    }

    private void validateId(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "ID khách hàng không hợp lệ!"
            );
        }
    }

    private void validate(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                "Khách hàng không được null!"
            );
        }

        if (customer.getFullName() == null ||
            customer.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                "Tên khách hàng không được để trống!"
            );
        }

        if (customer.getPhone() != null &&
            !customer.getPhone().isBlank() &&
            !customer.getPhone().matches("0\\d{9}")) {

            throw new IllegalArgumentException(
                "Số điện thoại không hợp lệ!"
            );
        }
    }
}