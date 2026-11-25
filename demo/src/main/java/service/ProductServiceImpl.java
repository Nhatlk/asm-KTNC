package service;

import java.math.BigDecimal;
import java.util.List;

import dao.ProductDAO;
import entity.Product;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    // Constructor dùng khi chạy ứng dụng thật
    public ProductServiceImpl() {
        this(new ProductDAO());
    }

    // Constructor Injection
    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public List<Product> getAll() {
        return productDAO.findAll();
    }

    @Override
    public Product getById(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "ID sản phẩm không hợp lệ!"
            );
        }

        return productDAO.findById(id);
    }

    @Override
    public Product create(Product product) {

        validate(product);

        return productDAO.insert(product);
    }

    @Override
    public Product update(Product product) {

        validate(product);

        if (product.getId() == null || product.getId() <= 0) {
            throw new IllegalArgumentException(
                "ID sản phẩm không hợp lệ!"
            );
        }

        if (productDAO.findById(product.getId()) == null) {
            throw new IllegalArgumentException(
                "Sản phẩm không tồn tại!"
            );
        }

        return productDAO.update(product);
    }

    @Override
    public boolean delete(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "ID sản phẩm không hợp lệ!"
            );
        }

        return productDAO.delete(id);
    }

    private void validate(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                "Sản phẩm không được null!"
            );
        }

        if (product.getName() == null ||
            product.getName().isBlank()) {

            throw new IllegalArgumentException(
                "Tên sản phẩm không được để trống!"
            );
        }

        if (product.getPrice() == null ||
            product.getPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                "Giá sản phẩm không hợp lệ!"
            );
        }

        if (product.getStock() == null ||
            product.getStock() < 0) {

            throw new IllegalArgumentException(
                "Số lượng tồn kho không hợp lệ!"
            );
        }

        if (product.getCategory() == null ||
            product.getCategory().getId() == null) {

            throw new IllegalArgumentException(
                "Danh mục sản phẩm không hợp lệ!"
            );
        }
    }
}