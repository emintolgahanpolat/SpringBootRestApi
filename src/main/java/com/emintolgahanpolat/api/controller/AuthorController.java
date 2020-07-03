package com.emintolgahanpolat.api.controller;

import com.emintolgahanpolat.api.model.Author;
import com.emintolgahanpolat.api.exceptions.BindingErrorsResponse;
import com.emintolgahanpolat.api.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


/**
 * Created by Keno&Kemo on 17.12.2017..
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(value = "/authors", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
public class AuthorController {
    private AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(value ="")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> allAuthors = authorService.findAll();
        if (allAuthors == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (allAuthors.isEmpty())
            return new ResponseEntity<>(allAuthors, HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(allAuthors, HttpStatus.OK);
    }
    @Autowired
    MessageSource messageSource;
    @Deprecated
    @GetMapping(value ="deprecated/{param0}")
    public ResponseEntity<String> getDeprecated(@PathVariable String param0) {
        String[] params= new String[]{param0};
       return new ResponseEntity<>(messageSource.getMessage("message.hello",params, LocaleContextHolder.getLocale()), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long id) {
        return authorService
                .findById(id)
                .map(author -> new ResponseEntity<>(author, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<Author> saveAuthor(@RequestBody @Valid Author author, BindingResult bindingResult,
                                             UriComponentsBuilder uriComponentsBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (author == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        authorService.save(author);
        headers.setLocation(uriComponentsBuilder.path("/authors/{id}").buildAndExpand(author.getId()).toUri());
        return new ResponseEntity<>(author, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable("id") Long id, @RequestBody @Valid Author author,
                                               BindingResult bindingResult) {
        Optional<Author> currentAuthor = authorService.findById(id);
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (author == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if (!currentAuthor.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        authorService.update(author);
        return new ResponseEntity<>(author, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Author> deleteAuthor(@PathVariable("id") Long id) {
        Optional<Author> authorToDelete = authorService.findById(id);
        if (!authorToDelete.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        authorService.delete(id);
        return new ResponseEntity<>(authorToDelete.get(), HttpStatus.NO_CONTENT);
    }

}
