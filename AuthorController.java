package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Author;
import com.example.demo.service.AuthorService;
import com.example.demo.exception.AuthorNotFoundException;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService service;

    @PostMapping
    public ResponseEntity<String> createAuthor(@RequestBody Author author) {
        logger.info("Received request to create a new author.");
        String message = service.saveAuthor(author);
        logger.info("Author created successfully.");
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        logger.info("Fetching all authors.");
        List<Author> authors = service.getAllAuthors();
        logger.info("Retrieved {} authors.", authors.size());
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorById(@PathVariable("id") Long id) {
        logger.info("Fetching author with ID: {}", id);
        try {
            Author author = service.getAuthorById(id);
            logger.info("Author found with ID: {}", id);
            return new ResponseEntity<>(author, HttpStatus.OK);
        } catch (AuthorNotFoundException e) {
            logger.warn("Author not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable("id") Long id, @RequestBody Author author) {
        logger.info("Updating author with ID: {}", id);
        try {
            author.setId(id);
            Author updatedAuthor = service.updateAuthor(author);
            logger.info("Author updated successfully with ID: {}", id);
            return new ResponseEntity<>(updatedAuthor, HttpStatus.OK);
        } catch (AuthorNotFoundException e) {
            logger.error("Failed to update author. Not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUpdateAuthor(@PathVariable("id") Long id, @RequestBody Author updatedFields) {
        logger.info("Partially updating author with ID: {}", id);
        try {
            Author patchedAuthor = service.patchUpdateAuthor(id, updatedFields);
            logger.info("Author patch update successful for ID: {}", id);
            return new ResponseEntity<>(patchedAuthor, HttpStatus.OK);
        } catch (AuthorNotFoundException e) {
            logger.warn("Patch update failed. Author not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthorById(@PathVariable("id") Long id) {
        logger.info("Attempting to delete author with ID: {}", id);
        try {
            String message = service.deleteAuthorById(id);
            logger.info("Author deleted successfully with ID: {}", id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (AuthorNotFoundException e) {
            logger.warn("Deletion failed. Author not found with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
