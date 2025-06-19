package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.exception.AuthorNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthorServiceImpl implements AuthorService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);
	
    @Autowired
    private AuthorRepository repository;

    @Override
    public String saveAuthor(Author author) {
    	logger.info("Saving author: {}", author);
        repository.save(author);
        logger.info("Author saved successfully.");
        return "Author saved successfully!";
    }

    @Override
    public Author updateAuthor(Author author) {
    	logger.info("Attempting to update author with ID: {}", author.getId());
        Optional<Author> existingAuthor = repository.findById(author.getId());
        if (existingAuthor.isPresent()) {
        	logger.info("Author found. Proceeding with update.");
            return repository.save(author);
        } else {
        	logger.error("Author not found for update with ID: {}", author.getId());
            throw new AuthorNotFoundException("Author not found for update with ID: " + author.getId());
        }
    }

    @Override
    public Author patchUpdateAuthor(Long id, Author updatedFields) {
    	logger.info("Attempting partial update for author with ID: {}", id);
        return repository.findById(id).map(existingAuthor -> {
            if (updatedFields.getName() != null && !updatedFields.getName().isEmpty()) {
                existingAuthor.setName(updatedFields.getName());
            }
            if (updatedFields.getBiography() != null) {
                existingAuthor.setBiography(updatedFields.getBiography());
            }
            logger.info("Saving partially updated author.");
            return repository.save(existingAuthor);
        }).orElseThrow(() ->{
        	logger.error("Author not found for partial update with ID: {}", id);
        return new AuthorNotFoundException("Author not found for partial update with ID: " + id);
        });
    }

    @Override
    public Author getAuthorById(Long id) {
    	logger.info("Fetching author by ID: {}", id);
        return repository.findById(id)
        		.orElseThrow(() -> {
                    logger.error("Author not found with ID: {}", id);
                    return new AuthorNotFoundException("Author not found with ID: " + id);
                });
    }

    @Override
    public String deleteAuthorById(Long id) {
    	logger.info("Attempting to delete author with ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            logger.info("Author deleted successfully.");
            return "Author deleted successfully!";
        } else {
        	logger.error("Author not found for deletion with ID: {}", id);
            throw new AuthorNotFoundException("Author not found for deletion with ID: " + id);
        }
    }

    @Override
    public List<Author> getAllAuthors() {
    	logger.info("Fetching all authors from repository.");
        return repository.findAll();
    }
}





