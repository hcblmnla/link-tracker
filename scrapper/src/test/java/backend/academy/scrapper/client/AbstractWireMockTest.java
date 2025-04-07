package backend.academy.scrapper.client;

import backend.academy.scrapper.ScrapperTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

@WireMockTest
public abstract class AbstractWireMockTest implements ScrapperTest {

    protected static WireMockServer server;

    @BeforeAll
    protected static void setUpServer() {
        server = new WireMockServer();
        server.start();
    }

    @AfterAll
    protected static void tearDownServer() {
        server.stop();
    }

    @BeforeEach
    protected void initServer() {
        server.resetAll();
    }
}
