/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper.config;

import de.itc.onkostar.api.IOnkostarApi;
import dev.dnpm.oshelper.atc.services.AgentCodeService;
import dev.dnpm.oshelper.atc.services.CsvAgentCodeService;
import dev.dnpm.oshelper.atc.services.OnkostarAgentCodeService;
import dev.dnpm.oshelper.database.SettingsRepository;
import dev.dnpm.oshelper.services.*;
import dev.dnpm.oshelper.services.consent.ConsentManagerServiceFactory;
import dev.dnpm.oshelper.services.molekulargenetik.MolekulargenetikFormService;
import dev.dnpm.oshelper.services.molekulargenetik.OsMolekulargenetikFormService;
import dev.dnpm.oshelper.services.strahlentherapie.DefaultStrahlentherapieService;
import dev.dnpm.oshelper.services.strahlentherapie.StrahlentherapieService;
import dev.dnpm.oshelper.services.systemtherapie.DefaultSystemtherapieService;
import dev.dnpm.oshelper.services.systemtherapie.SystemtherapieService;
import dev.dnpm.oshelper.services.therapieplan.TherapieplanServiceFactory;
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
@ComponentScan(basePackages = {"dev.dnpm.oshelper.analyzer", "dev.dnpm.oshelper.security"})
@EnableJpaRepositories(basePackages = "dev.dnpm.oshelper.database")
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

    @Bean
    public AgentCodeService csvAgentCodeService() {
        return new CsvAgentCodeService();
    }

    @Bean
    public AgentCodeService onkostarAgentCodeService (final DataSource dataSource) {
        return new OnkostarAgentCodeService(dataSource);
    }

}
