package DNPM.services;

import DNPM.database.SettingsRepository;
import de.itc.db.dnpm.Setting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class SettingsServiceTest {

    @Mock
    private SettingsRepository settingsRepository;

    private SettingsService service;

    @BeforeEach
    void setUp() {
        this.service = new SettingsService(settingsRepository);
    }

    @Test
    void shouldReturnSID() {
        doAnswer(invocationOnMock -> {
            var name = invocationOnMock.getArgument(0, String.class);
            if (null != name && name.equals("SID")) {
                return new Setting(1L, "SID", "12345");
            }
            return null;
        }).when(settingsRepository).findByName(anyString());

        var actual = service.getSID();
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo("12345");
    }

    @Test
    void shouldReturnSIDByName() {
        doAnswer(invocationOnMock -> {
            var name = invocationOnMock.getArgument(0, String.class);
            if (null != name && name.equals("SID")) {
                return new Setting(1L, "SID", "12345");
            }
            return null;
        }).when(settingsRepository).findByName(anyString());

        var actual = service.getSetting("SID");
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo("12345");
    }

}
