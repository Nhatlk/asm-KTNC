package intergration;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import dao.ProductDAO;
import entity.Product;

public class ProductDAOTest {
    @Test 
    void testFindAll(){
      ProductDAO dao = new ProductDAO();
      
      List<Product> products = dao.findAll();
      
      assertNotNull(products);
      assertFalse(products.isEmpty());
      products.forEach( pro -> {
        System.out.println(pro.getName());
      });
    }
}
