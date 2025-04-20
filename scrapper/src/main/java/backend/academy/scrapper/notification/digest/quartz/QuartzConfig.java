package backend.academy.scrapper.notification.digest.quartz;

import org.jspecify.annotations.NonNull;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfig {

    @Value("${notification.time.hours}")
    private int hours;

    @Value("${notification.time.minutes}")
    private int minutes;

    @Bean
    public JobDetail digestJobDetail() {
        return JobBuilder.newJob(DigestJob.class)
                .withIdentity("dailyDigestJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyDigestTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(digestJobDetail())
                .withIdentity("dailyDigestTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hours, minutes))
                .build();
    }

    @Bean
    public JobFactory jobFactory(final ApplicationContext applicationContext) {
        return new SpringBeanJobFactory() {

            @Override
            @NonNull
            protected Object createJobInstance(@NonNull final TriggerFiredBundle bundle) throws Exception {
                final Object job = super.createJobInstance(bundle);
                applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
                return job;
            }
        };
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            final JobFactory jobFactory, final Trigger dailyDigestTrigger, final JobDetail digestJobDetail) {
        final SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setJobDetails(digestJobDetail);
        factory.setTriggers(dailyDigestTrigger);
        return factory;
    }
}
