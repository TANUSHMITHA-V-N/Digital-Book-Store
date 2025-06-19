package com.example.demo.service;

import java.util.List;
import com.example.demo.model.Category;

public interface CategoryService {
    String saveCategory(Category category);
    Category updateCategory(Category category);
    Category patchUpdateCategory(Long id, Category updatedFields); // For partial updates
    Category getCategoryById(Long id);
    String deleteCategoryById(Long id);
    List<Category> getAllCategories();
}
