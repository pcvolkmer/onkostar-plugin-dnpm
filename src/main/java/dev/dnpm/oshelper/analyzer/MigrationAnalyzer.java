/*
 * This file is part of DNPMHelper
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

package dev.dnpm.oshelper.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import dev.dnpm.oshelper.migration.DatabaseMigrationService;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Diese Klasse implementiert einen Analyzer zur Durchführung von Migrationen des Plugins
 *
 * @since 2.3.0
 */
@Component
public class MigrationAnalyzer extends Analyzer {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final IOnkostarApi onkostarApi;

  private final DataSource dataSource;

  private final DatabaseMigrationService databaseMigrationService;

  public MigrationAnalyzer(
      final IOnkostarApi onkostarApi,
      final DataSource dataSource,
      final ResourcePatternResolver resourcePatternResolver) {
    logger.info("Ctor migration");
    this.onkostarApi = onkostarApi;
    this.dataSource = dataSource;
    this.databaseMigrationService =
        new DatabaseMigrationService(new JdbcTemplate(dataSource), resourcePatternResolver);
  }

  @Override
  public String getDescription() {
    return "Migration des Plugins";
  }

  /**
   * @deprecated
   */
  @Override
  public boolean isRelevantForDeletedProcedure() {
    return false;
  }

  @Override
  public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
    return null != procedure && procedure.getFormName().startsWith("DNPM");
  }

  @Override
  public boolean isSynchronous() {
    return true;
  }

  @Override
  public AnalyzerRequirement getRequirement() {
    return AnalyzerRequirement.PROCEDURE;
  }

  @Override
  public Set<AnalyseTriggerEvent> getTriggerEvents() {
    return Set.of(AnalyseTriggerEvent.REORG);
  }

  @Override
  public void analyze(Procedure procedure, Disease disease) {
    this.init();
  }

  @PostConstruct
  public void init() {
    this.databaseMigrationService.migrate();
  }
}
