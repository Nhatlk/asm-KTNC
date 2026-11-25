package service;

import java.util.List;

import dao.CategoryDAO;
import entity.Category;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO dao;

    public CategoryServiceImpl() {
        this(new CategoryDAO());
    }

    public CategoryServiceImpl(CategoryDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Category> getAll() {
        return dao.findAll();
    }

    @Override
    public Category getById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        return dao.findById(id);
    }

    @Override
    public Category create(Category category) {
        validate(category);
        return dao.insert(category);
    }

    @Override
    public Category update(Category category) {
        validate(category);

        if (category.getId() == null ||
            category.getId() <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        if (dao.findById(category.getId()) == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }

        return dao.update(category);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        return dao.delete(id);
    }

    private void validate(Category category) {

        if (category == null ||
            category.getName() == null ||
            category.getName().isBlank()) {

            throw new IllegalArgumentException(
                "Tên danh mục không được để trống"
            );
        }
    }
}