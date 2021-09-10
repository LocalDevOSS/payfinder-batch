package org.localdevelopers.payfinderbatch.config;

import org.localdevelopers.payfinderbatch.StepOperator;
import org.localdevelopers.payfinderbatch.api.StoreItemApiService;
import org.localdevelopers.payfinderbatch.domain.Store;
import org.localdevelopers.payfinderbatch.reader.StoreReader;
import org.localdevelopers.payfinderbatch.service.StoreService;
import org.localdevelopers.payfinderbatch.service.StoresFactory;
import org.localdevelopers.payfinderbatch.writer.StoreWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StoreService storeService;
    private final StoresFactory storesFactory;

    @Value("${spring.batch.config.chunkSize}")
    private int chunkSize;

    @Value("${spring.batch.config.poolSize}")
    private int poolSize;

    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, StoreItemApiService storeItemApiService, StoreService storeService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.storeService = storeService;
        this.storesFactory = new StoresFactory(storeItemApiService, storeService);
    }

    public SynchronizedItemStreamReader<Store> reader(final StepOperator stepOperator) {
        return new SynchronizedItemStreamReaderBuilder<Store>()
                .delegate(new StoreReader(storesFactory, stepOperator))
                .build();
    }

    public SynchronizedItemStreamWriter<Store> writer(final StepOperator stepOperator) {
        return new SynchronizedItemStreamWriterBuilder<Store>()
                .delegate(new StoreWriter(storeService, stepOperator))
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    @JobScope
    public Step loadStoresStep(@Value("#{jobParameters[siGunCode]}") String siGunCode) {
        return stepBuilderFactory.get("loadStoresStep")
                .tasklet((contribution, chunkContext) -> {
                    storesFactory.loadStores(siGunCode);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step saveStoresStep() {
        return stepBuilderFactory.get("saveStoresStep")
                .<Store, Store>chunk(chunkSize)
                .reader(reader(StepOperator.SAVE))
                .writer(writer(StepOperator.SAVE))
                .taskExecutor(taskExecutor())
                .throttleLimit(poolSize)
                .build();
    }

    @Bean
    public Step deleteStoresStep() {
        return stepBuilderFactory.get("deleteStoresStep")
                .<Store, Store>chunk(chunkSize)
                .reader(reader(StepOperator.DELETE))
                .writer(writer(StepOperator.DELETE))
                .taskExecutor(taskExecutor())
                .throttleLimit(poolSize)
                .build();
    }

    @Bean
    public Job updateStoresJob() {
        return jobBuilderFactory.get("updateStoresJob")
                .start(loadStoresStep(null))
                .next(saveStoresStep())
                .next(deleteStoresStep())
                .preventRestart()
                .build();
    }
}
