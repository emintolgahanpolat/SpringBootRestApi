package com.emintolgahanpolat.api.service;

import com.emintolgahanpolat.api.exceptions.ProcessException;
import com.emintolgahanpolat.api.model.Book;
import com.emintolgahanpolat.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    private BookRepository bookRepository;

    public BookService() {
    }

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Page<Book> getPaginatedBooks(int page,int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Book> resultPage = bookRepository.findAll(pageable);
        if (page > resultPage.getTotalPages()) {
            throw new ProcessException("Not Found Page Number:" + page);
        }
        return resultPage;
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book findByTitle(String title) {
        return bookRepository.findByTitleAllIgnoreCase(title);
    }

    public List<Book> findByTopicsId(Long id) {
        return bookRepository.findByTopicsId(id);
    }

    public List<Book> findByAuthorsId(Long id) {
        return bookRepository.findByAuthorsId(id);
    }


    public void save(Book book) {
        bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public void update(Book book) {
        bookRepository.save(book);
    }

}
