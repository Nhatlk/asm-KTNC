package unit.service;
import dao.OrderDAO;
import service.OrderServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName ("Unit test for Order Cancel Service")
public class OrderCancelServiceTest {
  @Mock 
  private OrderDAO dao;

  @InjectMocks 
  private OrderServiceImpl service;

  @Nested 
  @DisplayName ("Validation")
  class ValidationTests{
    @ParameterizedTest 
    @NullSource 
    @ValueSource (ints = {0, -1, -100})
    @DisplayName("TC01 - ID invali")
    void invalidOrderID(Integer id){
      // act + assert
      IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> service.cancel(id));
      
      assertAll(
        () -> assertEquals(
          "ID đơn hàng không hợp lệ!",
          ex.getMessage()
        ), 
        () -> verifyNoInteractions(dao)
      );
    }
  }

  @Nested 
  @DisplayName("TC02 - cancel Order")
  class CancelTests{
    @Test 
    @DisplayName("TC02 - Canceled Order")
    void cancelSuccess(){
      //arrange
      when(dao.cancelOrder(1)).thenReturn(true);

      //act 
      boolean result = service.cancel(1);

      //assert
      assertAll(
        ()-> assertTrue(result),
        () -> verify(dao, times(1))
              .cancelOrder(1)
      );
    }
  }

  @Test
        @DisplayName("TC03 - Đơn không tồn tại")
        void orderNotFound() {

            // ARRANGE
            when(dao.cancelOrder(999))
                .thenThrow(new IllegalArgumentException(
                    "Đơn hàng không tồn tại!"
                ));

            // ACT + ASSERT
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancel(999)
            );

            assertAll(
                () -> assertEquals(
                    "Đơn hàng không tồn tại!",
                    ex.getMessage()
                ),
                () -> verify(dao).cancelOrder(999)
            );
        }

        @Test
        @DisplayName("TC04 - Không được hủy lần hai")
        void alreadyCancelled() {

            // ARRANGE
            when(dao.cancelOrder(1))
                .thenThrow(new IllegalStateException(
                    "Chỉ được hủy đơn hàng ở trạng thái NEW!"
                ));

            // ACT + ASSERT
            assertThrows(
                IllegalStateException.class,
                () -> service.cancel(1)
            );

            verify(dao).cancelOrder(1);
        }
    }





