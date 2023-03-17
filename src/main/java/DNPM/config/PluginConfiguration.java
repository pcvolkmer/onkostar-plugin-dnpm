package DNPM.config;

import DNPM.services.*;
import de.itc.onkostar.api.IOnkostarApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Dynamische Konfiguration des Plugins basierend auf Onkostar-Einstellungen
 *
 * @since 0.0.2
 */
@Configuration
@ComponentScan(basePackages = "DNPM.analyzer")
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
    public TherapieplanServiceFactory therapieplanServiceFactory(final IOnkostarApi onkostarApi, final FormService formService) {
        return new TherapieplanServiceFactory(onkostarApi, formService);
    }

}
