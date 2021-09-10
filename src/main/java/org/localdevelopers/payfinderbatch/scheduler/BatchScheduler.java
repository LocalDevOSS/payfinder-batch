package org.localdevelopers.payfinderbatch.scheduler;

import lombok.RequiredArgsConstructor;
import org.localdevelopers.payfinderbatch.domain.SiGun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
    private static final String SIGUN_CD = "siGunCode";

    private final Job job;
    private final JobLauncher jobLauncher;
    private final MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 0 5 * * *")
    public void launchJob() {
        List<String> siGunCodes = readSiGunCodes();

        try {
            for (String siGunCode : siGunCodes) {
                jobLauncher.run(job,
                        new JobParametersBuilder()
                                .addString("datetime", LocalDateTime.now().toString())
                                .addString(SIGUN_CD, siGunCode)
                                .toJobParameters());
            }
        } catch (JobExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    private List<String> readSiGunCodes() {
        return mongoTemplate.findAll(SiGun.class, "sigun_info")
                .stream()
                .map(SiGun::getCode)
                .collect(Collectors.toList());
    }
}
