package DNPM.config;

import DNPM.database.SettingsRepository;
import DNPM.services.*;
import DNPM.services.mtb.DefaultMtbService;
import DNPM.services.mtb.MtbService;
import de.itc.onkostar.api.IOnkostarApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * Dynamische Konfiguration des Plugins basierend auf Onkostar-Einstellungen
 *
 * @since 0.0.2
 */
@Configuration
@ComponentScan(basePackages = "DNPM.analyzer")
@EnableJpaRepositories(basePackages = "DNPM.database")
public class PluginConfiguration {

    @Bean
    public FormService formService(final DataSource dataSource) {
        return new DefaultFormService(dataSource);
    }

    @Bean
    public StudienService studienService(final DataSource dataSource) {
        return new DefaultStudienService(dataSource);
    }

    @Bean
    public SettingsService settingsService(final SettingsRepository settingsRepository) {
        return new SettingsService(settingsRepository);
    }

    @Bean
    public MtbService mtbService(final IOnkostarApi onkostarApi) {
        return new DefaultMtbService(onkostarApi);
    }

    @Bean
    public TherapieplanServiceFactory therapieplanServiceFactory(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService,
            final FormService formService
    ) {
        return new TherapieplanServiceFactory(onkostarApi, settingsService, formService);
    }

}
