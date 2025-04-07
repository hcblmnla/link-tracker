package backend.academy.scrapper;

import org.springframework.boot.SpringApplication;

public enum TestApplication {
    ;

    public static void main(final String[] args) {
        SpringApplication.from(ScrapperApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
