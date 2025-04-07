package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScrapperScheduler {

    private final ScrapperService service;

    @Scheduled(fixedDelayString = "${scheduler.delay}")
    public void checkForUpdateLink() {
        service.checkForUpdate();
    }
}
