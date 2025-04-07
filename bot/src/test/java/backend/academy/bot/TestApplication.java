package backend.academy.bot;

import org.springframework.boot.SpringApplication;

public enum TestApplication {
    ;

    public static void main(final String[] args) {
        SpringApplication.from(BotApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
