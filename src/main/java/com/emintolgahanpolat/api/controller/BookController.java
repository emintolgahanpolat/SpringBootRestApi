package com.emintolgahanpolat.api.controller;

import com.emintolgahanpolat.api.exceptions.BindingErrorsResponse;
import com.emintolgahanpolat.api.model.Book;
import com.emintolgahanpolat.api.service.BookService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(value = "/books", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
@Api(tags = "Kitap")
public class BookController {
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("")
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam("page") int page , @RequestParam("size") int size) {
        List<Book> allBooks = bookService.findAll();
        if (allBooks == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (allBooks.isEmpty())
            return new ResponseEntity<>(allBooks, HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.findById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-topic-id/{topicIds}")
    public ResponseEntity<List<Book>> getAllBooksByTopicId(@PathVariable Long[] topicIds) {
        List<Book> allBooksByTopicId = new ArrayList<>();
        for (Long topicId : topicIds) {
            List<Book> booksByTopicId = bookService.findByTopicsId(topicId);
            if (!booksByTopicId.isEmpty())
                allBooksByTopicId.addAll(booksByTopicId);
        }
        if (allBooksByTopicId.isEmpty())
            return new ResponseEntity<>(allBooksByTopicId, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(allBooksByTopicId, HttpStatus.OK);
    }

    @GetMapping("/by-author-id/{authorIds}")
    public ResponseEntity<List<Book>> getAllBooksByAuthorId(@PathVariable Long[] authorIds) {
        List<Book> allBooksByAuthorId = new ArrayList<>();
        for (Long authorId : authorIds) {
            List<Book> booksByAuthorId = bookService.findByAuthorsId(authorId);
            if (!booksByAuthorId.isEmpty()) allBooksByAuthorId.addAll(booksByAuthorId);
        }
        if (allBooksByAuthorId.isEmpty())
            return new ResponseEntity<>(allBooksByAuthorId, HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(allBooksByAuthorId, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Book> saveBook(@RequestBody @Valid Book book, BindingResult bindingResult,
                                         UriComponentsBuilder uriComponentsBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (book == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        bookService.save(book);
        headers.setLocation(uriComponentsBuilder.path("/books/{id}").buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<>(book, headers, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody @Valid Book book,
                                           BindingResult bindingResult) {
        Optional<Book> currentBook = bookService.findById(id);
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (book == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if (!currentBook.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        bookService.update(book);
        return new ResponseEntity<>(book, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable("id") Long id) {
        Optional<Book> bookToDelete = bookService.findById(id);
        if (!bookToDelete.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        bookService.delete(id);
        return new ResponseEntity<>(bookToDelete.get(), HttpStatus.NO_CONTENT);

    }
}
