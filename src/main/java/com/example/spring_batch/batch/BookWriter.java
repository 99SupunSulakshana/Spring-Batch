package com.example.spring_batch.batch;

import com.example.spring_batch.entity.Book;
import com.example.spring_batch.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BookWriter implements ItemWriter<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void write(Chunk<? extends Book> chunk) throws Exception {
        log.info("Write: {}", chunk.getItems().size());
        bookRepository.saveAll(chunk.getItems());
    }
}
