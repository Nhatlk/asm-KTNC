package dao;

import entity.Category;

public class CategoryDAO
        extends BaseDAOImpl<Integer, Category> {

    public CategoryDAO() {
        super(Category.class);
    }
}