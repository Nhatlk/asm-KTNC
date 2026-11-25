package intergration;
import config.EMF;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DBconnectionTest {

    @Test
    void testConnection() {

        try(EntityManager emm = EMF.getEntityManager()) {
            Object result = emm.createNativeQuery("SELECT COUNT(*) FROM dbo.Products").getSingleResult();
            assertNotNull(result);

            System.out.println("Kết nối SQL Server thành công!");
            System.out.println("so luong san pham: " + result);
        }catch(Exception e ){
            fail("Kết nối SQL Server thất bại: " + e.getMessage());
        }
    }
}