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

package dev.dnpm.oshelper.migration;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Diese Klasse implementiert die Durchführung einer Datenbankmigration
 *
 * @since 2.3.0
 */
public class DatabaseMigrationService {

  private final Logger log = LoggerFactory.getLogger(DatabaseMigrationService.class);

  private final JdbcTemplate jdbcTemplate;
  private final ResourcePatternResolver resourcePatternResolver;

  public DatabaseMigrationService(
      final JdbcTemplate jdbcTemplate, final ResourcePatternResolver resourcePatternResolver) {
    this.jdbcTemplate = jdbcTemplate;
    this.resourcePatternResolver = resourcePatternResolver;
  }

  /**
   * Diese Methode führt eine Datenbankmigration durch. Es werden alle Migrationen durchgeführt, die
   * noch nicht angewendet wurden.
   */
  public void migrate() {
    log.info("Starting database migration");
    try {
      for (var migration : this.getNewMigrationIds()) {
        log.info("Apply database migration: '{}'", migration);
        try {
          jdbcTemplate.execute(MigrationUtils.getSqlQuery(resourcePatternResolver, migration));
        } catch (DataAccessException e) {
          jdbcTemplate.update(
              "INSERT INTO update_db (date, update_script, failed, fail_description) VALUES (NOW(), ?, ?, ?)",
              migration,
              true,
              e.getMessage().length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage());
          continue;
        }
        jdbcTemplate.update(
            "INSERT INTO update_db (date, update_script, failed) VALUES (NOW(), ?, ?)",
            migration,
            false);
      }
    } catch (Exception e) {
      log.error("Error while migrating database", e);
    }
  }

  List<String> getNewMigrationIds() throws Exception {
    var existingMigrations = MigrationUtils.getExecutedDatabaseMigrations(this.jdbcTemplate);
    return MigrationUtils.getDatabaseMigrationScripts(this.resourcePatternResolver).stream()
        .filter(migration -> !existingMigrations.contains(migration))
        .collect(Collectors.toList());
  }
}
