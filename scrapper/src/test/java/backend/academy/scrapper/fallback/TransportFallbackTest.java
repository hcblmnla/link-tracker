package backend.academy.scrapper.fallback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import backend.academy.scrapper.ScrapperTest;
import backend.academy.scrapper.service.LinkUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransportFallbackTest implements ScrapperTest {

    @Mock
    private LinkUpdateService primary;

    @Mock
    private LinkUpdateService secondary;

    private FallbackLinkUpdateService fallbackService;

    @BeforeEach
    void openMocks() {
        //noinspection resource
        MockitoAnnotations.openMocks(this);
        fallbackService = new FallbackLinkUpdateService(primary, secondary);
    }

    @Test
    void shouldUsePrimaryService() {
        // given-when
        fallbackService.update(LINK_UPDATE);

        // then
        verify(primary).update(LINK_UPDATE);
        verifyNoInteractions(secondary);

        assertThat(fallbackService.fallback().get()).isFalse();
    }

    @Test
    void shouldSwitchToSecondaryOnPrimaryFailure() {
        // given
        doThrow(new RuntimeException()).when(primary).update(LINK_UPDATE);

        // when
        fallbackService.update(LINK_UPDATE);

        // then
        verify(secondary).update(LINK_UPDATE);
        assertThat(fallbackService.fallback().get()).isTrue();
    }
}
