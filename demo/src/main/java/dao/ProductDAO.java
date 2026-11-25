package dao;

import entity.Product;

public class ProductDAO
        extends BaseDAOImpl<Integer, Product> {

    public ProductDAO() {
        super(Product.class);
    }
}