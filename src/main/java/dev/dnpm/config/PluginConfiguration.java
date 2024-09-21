package dev.dnpm.config;

import dev.dnpm.database.SettingsRepository;
import dev.dnpm.services.*;
import dev.dnpm.services.consent.ConsentManagerServiceFactory;
import dev.dnpm.services.molekulargenetik.MolekulargenetikFormService;
import dev.dnpm.services.molekulargenetik.OsMolekulargenetikFormService;
import dev.dnpm.services.mtb.DefaultMtbService;
import dev.dnpm.services.mtb.MtbService;
import dev.dnpm.services.strahlentherapie.DefaultStrahlentherapieService;
import dev.dnpm.services.strahlentherapie.StrahlentherapieService;
import dev.dnpm.services.systemtherapie.DefaultSystemtherapieService;
import dev.dnpm.services.systemtherapie.SystemtherapieService;
import dev.dnpm.services.therapieplan.TherapieplanServiceFactory;
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
@ComponentScan(basePackages = { "dev.dnpm.analyzer", "dev.dnpm.security" })
@EnableJpaRepositories(basePackages = "dev.dnpm.database")
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
    public SystemtherapieService systemtherapieService(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService
    ) {
        return new DefaultSystemtherapieService(onkostarApi, settingsService);
    }

    @Bean
    public StrahlentherapieService strahlentherapieService(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService
    ) {
        return new DefaultStrahlentherapieService(onkostarApi, settingsService);
    }

    @Bean
    public ConsentManagerServiceFactory consentManagerServiceFactory(final IOnkostarApi onkostarApi) {
        return new ConsentManagerServiceFactory(onkostarApi);
    }

    @Bean
    public TherapieplanServiceFactory therapieplanServiceFactory(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService,
            final FormService formService
    ) {
        return new TherapieplanServiceFactory(onkostarApi, settingsService, formService);
    }

    @Bean
    public MolekulargenetikFormService molekulargenetikFormService() {
        return new OsMolekulargenetikFormService();
    }

}
