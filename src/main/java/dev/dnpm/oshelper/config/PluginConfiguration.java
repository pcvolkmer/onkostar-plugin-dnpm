/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.dnpm.oshelper.config;

import dev.dnpm.oshelper.database.SettingsRepository;
import dev.dnpm.oshelper.services.*;
import dev.dnpm.oshelper.services.consent.ConsentManagerServiceFactory;
import dev.dnpm.oshelper.services.molekulargenetik.MolekulargenetikFormService;
import dev.dnpm.oshelper.services.molekulargenetik.OsMolekulargenetikFormService;
import dev.dnpm.oshelper.services.mtb.DefaultMtbService;
import dev.dnpm.oshelper.services.mtb.MtbService;
import dev.dnpm.oshelper.services.strahlentherapie.DefaultStrahlentherapieService;
import dev.dnpm.oshelper.services.strahlentherapie.StrahlentherapieService;
import dev.dnpm.oshelper.services.systemtherapie.DefaultSystemtherapieService;
import dev.dnpm.oshelper.services.systemtherapie.SystemtherapieService;
import dev.dnpm.oshelper.services.therapieplan.TherapieplanServiceFactory;
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
@ComponentScan(basePackages = {"dev.dnpm.oshelper.analyzer", "dev.dnpm.oshelper.security"})
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
