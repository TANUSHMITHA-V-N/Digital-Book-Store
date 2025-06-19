package com.example.demo.service;

import java.util.List;
import com.example.demo.model.Author;

public interface AuthorService {
    String saveAuthor(Author author);
    Author updateAuthor(Author author);
    Author patchUpdateAuthor(Long id, Author updatedFields);
    Author getAuthorById(Long id);
    String deleteAuthorById(Long id);
    List<Author> getAllAuthors();
}
