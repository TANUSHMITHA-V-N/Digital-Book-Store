package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import com.example.demo.exception.CategoryNotFoundException;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService service;

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        logger.info("Received request to create a new category.");
        String message = service.saveCategory(category);
        logger.info("Category created successfully.");
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Fetching all categories.");
        List<Category> categories = service.getAllCategories();
        logger.info("Retrieved {} categories.", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Long id) {
        logger.info("Fetching category with ID: {}", id);
        try {
            Category category = service.getCategoryById(id);
            logger.info("Category found with ID: {}", id);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            logger.warn("Category not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        logger.info("Updating category with ID: {}", id);
        try {
            category.setId(id);
            Category updatedCategory = service.updateCategory(category);
            logger.info("Category updated successfully with ID: {}", id);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            logger.warn("Failed to update category. Not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUpdateCategory(@PathVariable("id") Long id, @RequestBody Category updatedFields) {
        logger.info("Patching category with ID: {}", id);
        try {
            Category patchedCategory = service.patchUpdateCategory(id, updatedFields);
            logger.info("Category patched successfully with ID: {}", id);
            return new ResponseEntity<>(patchedCategory, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            logger.warn("Patch update failed. Category not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable("id") Long id) {
        logger.info("Attempting to delete category with ID: {}", id);
        try {
            String message = service.deleteCategoryById(id);
            logger.info("Category deleted successfully with ID: {}", id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            logger.warn("Deletion failed. Category not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
