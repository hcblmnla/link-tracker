package backend.academy.scrapper.link.service;

import backend.academy.scrapper.ScrapperTest;
import backend.academy.scrapper.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public abstract class LinkServiceTest implements ScrapperTest {}
