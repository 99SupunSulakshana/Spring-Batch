package com.example.spring_batch.batch;

import com.example.spring_batch.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RestBookReader implements ItemReader<Book> {

    private final String url;

    private final RestTemplate restTemplate;

    private int nextBook;

    private List<Book> bookList;

    public RestBookReader(String url, RestTemplate restTemplate){
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public Book read() throws Exception {
        if(this.bookList == null){
            bookList = fetchBooks();
;        }
        Book book = null;

        assert bookList != null;

        if(nextBook<bookList.size()){
            book = bookList.get(nextBook);
            nextBook++;
        } else {
            nextBook = 0;
            bookList = null;
        }
        return book;
    }

    private List<Book> fetchBooks(){
        ResponseEntity<Book[]> response = restTemplate.getForEntity(this.url, Book[].class);
        Book[] books = response.getBody();
        if(books != null){
            return Arrays.asList(books);
        }
        return null;
    }


}
