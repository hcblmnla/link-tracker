package backend.academy.scrapper.notification.digest.quartz;

import backend.academy.scrapper.notification.digest.DigestService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@RequiredArgsConstructor
public class DigestJob implements Job {

    private final DigestService digestService;

    @Override
    public void execute(final JobExecutionContext context) {
        digestService.sendDigest();
    }
}
