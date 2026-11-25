package service;

import java.util.List;
import entity.Category;

public interface CategoryService {

    List<Category> getAll();

    Category getById(Integer id);

    Category create(Category category);

    Category update(Category category);

    boolean delete(Integer id);
}