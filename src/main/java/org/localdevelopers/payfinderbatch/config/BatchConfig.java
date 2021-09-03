package org.localdevelopers.payfinderbatch.config;

import lombok.RequiredArgsConstructor;
import org.localdevelopers.payfinderbatch.api.StoreItemApiService;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.model.StoreItem;
import org.localdevelopers.payfinderbatch.processor.StoreItemProcessor;
import org.localdevelopers.payfinderbatch.reader.StoreItemReader;
import org.localdevelopers.payfinderbatch.writer.StoreItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {StoreItemApiService.class})
public class BatchConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StoreItemApiService storeItemApiService;

    @Bean
    @JobScope
    @PostConstruct
    public ItemReader<StoreItem> reader() {
        return new StoreItemReader(storeItemApiService, null);
    }

    @Bean
    public MongoItemWriter<Store> writer(MongoTemplate mongoTemplate) {
        return new StoreItemWriter(mongoTemplate);
    }

    @Bean
    public StoreItemProcessor processor() {
        return new StoreItemProcessor();
    }

    @Bean
    public Step updateStoreItemsStep(ItemReader<StoreItem> reader, ItemWriter<Store> writer) {
        return stepBuilderFactory.get("updateStoreItemsStep")
                .<StoreItem, Store>chunk(CHUNK_SIZE)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job updateStoreItemsJob(Step collectStoreItemsStep) {
        return jobBuilderFactory.get("updateStoreItemsJob")
                .start(collectStoreItemsStep)
                .build();
    }
}
