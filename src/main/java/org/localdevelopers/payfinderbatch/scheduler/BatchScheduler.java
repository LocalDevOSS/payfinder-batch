package org.localdevelopers.payfinderbatch.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);

    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "0 0 5 * * *")
    public void launchJob() {
        try {
            jobLauncher.run(
                    job,
                    new JobParametersBuilder()
                            .addString("datetime", LocalDateTime.now().toString())
                            .toJobParameters());
        } catch (JobExecutionException e) {
            logger.error(e.getMessage());
        }
    }
}
