package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional

import com.example.demo.model.Book;
import com.example.demo.model.Author; // Import Author
import com.example.demo.model.Category; // Import Category
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.AuthorRepository; // Autowire AuthorRepository
import com.example.demo.repository.CategoryRepository; // Autowire CategoryRepository
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.AuthorNotFoundException; // Import for AuthorNotFoundException
import com.example.demo.exception.CategoryNotFoundException; // Import for CategoryNotFoundException
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookServiceImpl implements BookService {
	
	private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
	
    @Autowired
    private BookRepository repository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository; 

    @Override
    @Transactional
    public String saveBook(Book book) {
        logger.info("Saving book: {}", book.getTitle());

        Author author = authorRepository.findById(book.getAuthor().getId())
                .orElseThrow(() -> {
                    logger.error("Author not found with ID: {}", book.getAuthor().getId());
                    return new AuthorNotFoundException("Author not found with ID: " + book.getAuthor().getId());
                });
        book.setAuthor(author);

        Category category = categoryRepository.findById(book.getCategory().getId())
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", book.getCategory().getId());
                    return new CategoryNotFoundException("Category not found with ID: " + book.getCategory().getId());
                });
        book.setCategory(category);

        repository.save(book);
        logger.info("Book saved successfully.");
        return "Saved Successfully";
    }
    

    @Override
    @Transactional
    public Book updateBook(Book book) {
    	logger.info("Attempting to update book with ID: {}", book.getBookid());
        Optional<Book> existingBookOpt = repository.findById(book.getBookid());
        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();
            if (book.getAuthor() != null && book.getAuthor().getId() != null) {
                Author author = authorRepository.findById(book.getAuthor().getId())
                        .orElseThrow(() -> {
                        	logger.error("Author not found with ID: {}", book.getAuthor().getId());
                            return new AuthorNotFoundException("Author not found with ID: " + book.getAuthor().getId());
                        });
                existingBook.setAuthor(author);
            }

            if (book.getCategory() != null && book.getCategory().getId() != null) {
                Category category = categoryRepository.findById(book.getCategory().getId())
                		.orElseThrow(() -> {
                            logger.error("Category not found with ID: {}", book.getCategory().getId());
                            return new CategoryNotFoundException("Category not found with ID: " + book.getCategory().getId());
                        });
                existingBook.setCategory(category);
            }

            existingBook.setTitle(book.getTitle());
            existingBook.setPrice(book.getPrice());
            existingBook.setStockquantity(book.getStockquantity());
            existingBook.setImageData(book.getImageData());
            existingBook.setDescription(book.getDescription());
            logger.info("Book updated successfully.");
            return repository.save(existingBook);
        } else {
        	logger.error("Book not found for update with ID: {}", book.getBookid());
            throw new BookNotFoundException("Book not found for update with ID: " + book.getBookid());
        }
    }

    @Override
    public Book getBookById(Long bookid) {
    	logger.info("Fetching book with ID: {}", bookid);
        Optional<Book> optionalBook = repository.findById(bookid);
        return optionalBook.orElseThrow(() -> {
        	logger.error("Book not found with ID: {}", bookid);
        	return new BookNotFoundException("Book not found with ID: " + bookid);
        });
    }

    @Override
    public String deleteBookById(Long bookid) {
    	logger.info("Attempting to delete book with ID: {}", bookid);
        if (repository.existsById(bookid)) {
            repository.deleteById(bookid);
            logger.info("Book deleted successfully.");
            return "Deleted Successfully";
        } else {
        	logger.error("Book not found for deletion with ID: {}", bookid);
            throw new BookNotFoundException("Book not found for deletion with ID: " + bookid);
        }
    }

    @Override
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Book patchUpdateBook(Long bookid, Book updatedFields) {
    	logger.info("Attempting patch update for book with ID: {}", bookid);
        return repository.findById(bookid).map(existingBook -> {
            if (updatedFields.getTitle() != null) {
            	logger.debug("Updating title to: {}", updatedFields.getTitle());
                existingBook.setTitle(updatedFields.getTitle());
            }
            if (updatedFields.getPrice() != 0.0) {
            	logger.debug("Updating price to: {}", updatedFields.getPrice());
                existingBook.setPrice(updatedFields.getPrice());
            }
            if (updatedFields.getStockquantity() != 0) {
            	logger.debug("Updating stock quantity to: {}", updatedFields.getStockquantity());
                existingBook.setStockquantity(updatedFields.getStockquantity());
            }
            if (updatedFields.getImageData() != null) {
            	logger.debug("Updating image data.");
                existingBook.setImageData(updatedFields.getImageData());
            }
            if (updatedFields.getDescription() != null) {
            	logger.debug("Updating description.");
                existingBook.setDescription(updatedFields.getDescription());
            }

            if (updatedFields.getAuthor() != null && updatedFields.getAuthor().getId() != null) {
                Author author = authorRepository.findById(updatedFields.getAuthor().getId())
                        .orElseThrow(() ->{
                            logger.error("Author not found with ID: {}", updatedFields.getAuthor().getId());
                            return new AuthorNotFoundException("Author not found with ID: " + updatedFields.getAuthor().getId());
                        });
                existingBook.setAuthor(author);
            }
            if (updatedFields.getCategory() != null && updatedFields.getCategory().getId() != null) {
                Category category = categoryRepository.findById(updatedFields.getCategory().getId())
                		.orElseThrow(() -> {
                            logger.error("Category not found with ID: {}", updatedFields.getCategory().getId());
                            return new CategoryNotFoundException("Category not found with ID: " + updatedFields.getCategory().getId());
                        });
                existingBook.setCategory(category);
            }
            logger.info("Patch update successful.");
            return repository.save(existingBook);
        }).orElseThrow(() -> {
            logger.error("Book not found with ID: {}", bookid);
            return new BookNotFoundException("Book not found with ID: " + bookid);
        });
    }
}
