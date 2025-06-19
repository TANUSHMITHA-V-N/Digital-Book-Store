package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.exception.CategoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository repository;

    @Override
    public String saveCategory(Category category) {
        logger.info("Saving category: {}", category.getName());
        repository.save(category);
        logger.info("Category saved successfully.");
        return "Category saved successfully!";
    }

    @Override
    public Category updateCategory(Category category) {
        logger.info("Attempting to update category with ID: {}", category.getId());
        Optional<Category> existingCategory = repository.findById(category.getId());
        if (existingCategory.isPresent()) {
            logger.info("Category found. Proceeding with update.");
            return repository.save(category);
        } else {
            logger.error("Category not found for update with ID: {}", category.getId());
            throw new CategoryNotFoundException("Category not found for update with ID: " + category.getId());
        }
    }

    @Override
    public Category patchUpdateCategory(Long id, Category updatedFields) {
        logger.info("Attempting partial update for category with ID: {}", id);
        return repository.findById(id).map(existingCategory -> {
            if (updatedFields.getName() != null && !updatedFields.getName().isEmpty()) {
                logger.debug("Updating category name to: {}", updatedFields.getName());
                existingCategory.setName(updatedFields.getName());
            }
            logger.info("Saving partially updated category.");
            return repository.save(existingCategory);
        }).orElseThrow(() -> {
            logger.error("Category not found for partial update with ID: {}", id);
            return new CategoryNotFoundException("Category not found for partial update with ID: " + id);
        });
    }

    @Override
    public Category getCategoryById(Long id) {
        logger.info("Fetching category with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", id);
                    return new CategoryNotFoundException("Category not found with ID: " + id);
                });
    }

    @Override
    public String deleteCategoryById(Long id) {
        logger.info("Attempting to delete category with ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            logger.info("Category deleted successfully.");
            return "Category deleted successfully!";
        } else {
            logger.error("Category not found for deletion with ID: {}", id);
            throw new CategoryNotFoundException("Category not found for deletion with ID: " + id);
        }
    }

    @Override
    public List<Category> getAllCategories() {
        logger.info("Retrieving all categories.");
        return repository.findAll();
    }
}