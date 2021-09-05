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
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {StoreItemApiService.class})
public class BatchConfig {
    private static final String STORE_ITEM_JOB = "StoreItem";


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StoreItemApiService storeItemApiService;

    @Value("${spring.batch.config.chunkSize}")
    private int chunkSize;

    @Value("${spring.batch.config.poolSize}")
    private int poolSize;

    @Bean(name = STORE_ITEM_JOB + "Reader")
    @StepScope
//    @PostConstruct
    public SynchronizedItemStreamReader<StoreItem> reader(@Value("#{jobParameters[siGunCode]}") String siGunCode) {
        return new SynchronizedItemStreamReaderBuilder<StoreItem>()
                .delegate(new StoreItemReader(storeItemApiService, siGunCode))
                .build();
    }

    @Bean(name = STORE_ITEM_JOB + "Writer")
    public MongoItemWriter<Store> writer(MongoTemplate mongoTemplate) {
        return new StoreItemWriter(mongoTemplate);
    }

    @Bean
    public StoreItemProcessor processor() {
        return new StoreItemProcessor();
    }

    @Bean(name = STORE_ITEM_JOB + "TaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = STORE_ITEM_JOB + "Step")
    @JobScope
    public Step updateStoreItemsStep(ItemReader<StoreItem> reader, ItemWriter<Store> writer) {
        return stepBuilderFactory.get(STORE_ITEM_JOB + "Step")
                .<StoreItem, Store>chunk(chunkSize)
                .reader(reader(null))
                .processor(processor())
                .writer(writer)
                .taskExecutor(taskExecutor())
                .throttleLimit(poolSize)
                .build();
    }

    @Bean(name = STORE_ITEM_JOB)
    public Job updateStoreItemsJob(Step updateStoreItemsStep) {
        return jobBuilderFactory.get(STORE_ITEM_JOB)
                .start(updateStoreItemsStep)
                .preventRestart()
                .build();
    }
}
