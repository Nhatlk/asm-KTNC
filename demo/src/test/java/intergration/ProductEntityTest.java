package intergration;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import config.EMF;
import entity.Product;
import jakarta.persistence.EntityManager;

public class ProductEntityTest {
  @Test
  void testFindProducct(){
    try(EntityManager emm = EMF.getEntityManager()){
      Product product = emm.find(Product.class, 1);
      assertNotNull(product);
      System.out.println("Product name: " + product);
    }catch(Exception e){
      System.out.println("Error: " + e.getMessage());
      fail("Error: " + e.getMessage());
    }
  }
}
