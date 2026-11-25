package service;

import java.util.List;
import entity.Product;

public interface ProductService {

    List<Product> getAll();

    Product getById(Integer id);

    Product create(Product product);

    Product update(Product product);

    boolean delete(Integer id);
}