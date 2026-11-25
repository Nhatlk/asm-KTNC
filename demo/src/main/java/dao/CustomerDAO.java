package dao;

import entity.Customer;

public class CustomerDAO
        extends BaseDAOImpl<Integer, Customer> {

    public CustomerDAO() {
        super(Customer.class);
    }
}