package com.example.spring_batch.config;

import com.example.spring_batch.batch.*;
import com.example.spring_batch.entity.Book;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class BatchConfig {

    @Bean
    public Job bookReaderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("bookReadJob", jobRepository)
                .incrementer(new RunIdIncrementer())
//                .start(chunkStep(jobRepository, transactionManager))
                .start(taskletStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("bookReadStep", jobRepository).<Book, Book>chunk(10, transactionManager)
                .reader(restBookReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Step taskletStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("taskletStep", jobRepository)
                .tasklet(new BookTasklet(), platformTransactionManager).build();
    }

    @Bean
    @StepScope
    public ItemReader<Book> restBookReader() {
        return new RestBookReader("http://localhost:8080/book", new RestTemplate());
    }

    @Bean
    public ItemProcessor<Book, Book> processor() {
        CompositeItemProcessor<Book, Book> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new BookTitleProcessor(), new BookAuthorProcessor()));
        return processor;
    }

    @Bean
    public ItemWriter<Book> writer() {
        return new BookWriter();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Book> reader() {
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookReader")
                .resource(new ClassPathResource("sample_books.csv"))
                .delimited()
                .names(new String[]{"title", "author", "year_of_publishing"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Book.class);
                    }
                })
                .build();
    }

}
