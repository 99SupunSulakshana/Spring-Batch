package com.example.spring_batch.batch;

import com.example.spring_batch.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class BookAuthorProcessor implements ItemProcessor<Book, Book> {
    @Override
    public Book process(Book item) throws Exception {
        log.info("Processing author for {}", item);
        item.setAuthor("By " + item.getAuthor());
        return item;
    }
}
