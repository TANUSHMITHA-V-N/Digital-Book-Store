package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.AuthorNotFoundException;
import com.example.demo.exception.CategoryNotFoundException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService service;

    @PostMapping
    public ResponseEntity<String> saveBook(@RequestBody Book book) {
        logger.info("Received request to save a new book.");
        try {
            String message = service.saveBook(book);
            logger.info("Book saved successfully.");
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (AuthorNotFoundException | CategoryNotFoundException e) {
            logger.warn("Book save failed due to missing reference: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error saving book: {}", e.getMessage());
            return new ResponseEntity<>("Error saving book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("Fetching all books.");
        List<Book> allBooks = service.getAllBooks();
        logger.info("Retrieved {} books.", allBooks.size());
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long bookid) {
        logger.info("Fetching book with ID: {}", bookid);
        try {
            Book book = service.getBookById(bookid);
            logger.info("Book found with ID: {}", bookid);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            logger.warn("Book not found with ID: {}", bookid);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching book with ID {}: {}", bookid, e.getMessage());
            return new ResponseEntity<>("Error fetching book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        logger.info("Updating book with ID: {}", id);
        try {
            book.setBookid(id); 
            Book updatedBook = service.updateBook(book);
            logger.info("Book updated successfully with ID: {}", id);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            logger.warn("Book not found for update with ID: {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthorNotFoundException | CategoryNotFoundException e) {
            logger.warn("Book update failed due to missing reference: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error updating book with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>("Error updating book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") Long bookid) {
        logger.info("Attempting to delete book with ID: {}", bookid);
        try {
            String message = service.deleteBookById(bookid);
            logger.info("Book deleted successfully with ID: {}", bookid);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            logger.warn("Book not found for deletion with ID: {}", bookid);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting book with ID {}: {}", bookid, e.getMessage());
            return new ResponseEntity<>("Error deleting book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/partial-update/{id}")
    public ResponseEntity<?> patchUpdateBook(@PathVariable("id") Long bookid, @RequestBody Book updatedFields) {
        logger.info("Attempting partial update for book with ID: {}", bookid);
        try {
            Book updatedBook = service.patchUpdateBook(bookid, updatedFields);
            logger.info("Book partially updated successfully with ID: {}", bookid);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            logger.warn("Book not found for patch update with ID: {}", bookid);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthorNotFoundException | CategoryNotFoundException e) {
            logger.warn("Patch update failed due to missing reference: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error patching book with ID {}: {}", bookid, e.getMessage());
            return new ResponseEntity<>("Error patching book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
