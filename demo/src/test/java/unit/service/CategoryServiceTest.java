package unit.service;
import dao.CategoryDAO;
import entity.Category;
import service.CategoryServiceImpl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Test
    void testCreateSuccess() {

        // ARRANGE
        CategoryDAO mockDAO = mock(CategoryDAO.class);

        CategoryServiceImpl service =
                new CategoryServiceImpl(mockDAO);

        Category category = new Category();
        category.setName("Đồ uống");

        when(mockDAO.insert(category))
                .thenReturn(category);

        // ACT
        Category result = service.create(category);

        // ASSERT
        assertNotNull(result);
        assertEquals("Đồ uống", result.getName());

        verify(mockDAO, times(1)).insert(category);
    }

    @Test
    void testCreateEmptyName() {
      CategoryDAO mockDAO = mock(CategoryDAO.class);
      CategoryServiceImpl service = new CategoryServiceImpl(mockDAO);

      Category category = new Category();
      category.setName("");

      IllegalArgumentException exception = assertThrows(
              IllegalArgumentException.class,
              () -> service.create(category)
      );
      assertEquals("Tên danh mục không được để trống", exception.getMessage());

      verify(mockDAO, never()).insert(category);
    }
}