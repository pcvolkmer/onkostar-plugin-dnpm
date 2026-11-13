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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * In dieser Klasse werden Hilfsmethoden zur Durchführung von Migrationen des Plugins implementiert
 *
 * @since 2.3.0
 */
class MigrationUtils {

  static List<String> getExecutedDatabaseMigrations(JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(
        "SELECT id FROM update_db WHERE update_script LIKE 'updatednpm_%'",
        (rs, rowNum) -> rs.getString("update_script"));
  }

  static boolean requiredFormRevisionAvailable(
      JdbcTemplate jdbcTemplate, String formName, String minRev, String maxRev) {
    try {
      return 1
          == jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM data_form WHERE name = ? AND revision >= ? AND revision <= ?",
              new Object[] {formName, minRev, maxRev},
              Integer.class);
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  static List<String> getDatabaseMigrationScripts(ResourcePatternResolver resourcePatternResolver)
      throws IOException {
    var resources =
        List.of(
            resourcePatternResolver.getResources("classpath:dev/dnpm/oshelper/migration/*.sql"));
    return resources.stream()
        .map(Resource::getFilename)
        .filter(s -> s.startsWith("updatednpm_") && s.endsWith(".sql"))
        .collect(Collectors.toList());
  }

  static String getSqlQuery(ResourcePatternResolver resourcePatternResolver, String id)
      throws IOException {
    final var sql =
        resourcePatternResolver
            .getResource(String.format("classpath:dev/dnpm/oshelper/migration/%s", id))
            .getInputStream()
            .readAllBytes();
    return new String(sql);
  }
}
