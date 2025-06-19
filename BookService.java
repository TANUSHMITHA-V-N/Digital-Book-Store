package com.example.demo.service;

import java.util.List;
import com.example.demo.model.Book; // Ensures the correct Book model is imported

public interface BookService {
	String saveBook(Book book);

	Book updateBook(Book book);

	Book patchUpdateBook(Long bookid, Book updatedFields);

	Book getBookById(Long bookid);

	String deleteBookById(Long bookid);

	List<Book> getAllBooks();
}
