package unit.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import dao.ProductDAO;
import entity.Category;
import entity.Product;
import service.ProductService;
import service.ProductServiceImpl;

public class ProductServiceTest {
  @Test 
  void testCreateProductSuccess(){
    // arrange
    ProductDAO mockDAO  = mock(ProductDAO.class);
    ProductService sv = new ProductServiceImpl(mockDAO);
    
    Category category = new Category();
    category.setId(1);


    Product pro = new Product();
    pro.setName("Product 1");
    pro.setPrice(new BigDecimal("15000")); 
    pro.setCategory(category);

    // act
    Product result = sv.create(pro);

    // assert
    assertNotNull(result);
    assertEquals(pro, result);
    
    verify(mockDAO, times(1)).insert(pro);

  }

  @Test 
  void testNegativePrice(){
    // arrange
    ProductDAO mockDAO  = mock(ProductDAO.class);
    ProductService sv = new ProductServiceImpl(mockDAO);
    
    Category category = new Category();
    category.setId(1);

    Product pro = new Product();
    pro.setName("ads");
    pro.setPrice(new BigDecimal("-15000"));
    pro.setCategory(category);
    pro.setStock(20);
    
    assertThrows(IllegalArgumentException.class, () -> {
      sv.create(pro);
    });

    verify(mockDAO, never()).insert(pro);


  }
}
