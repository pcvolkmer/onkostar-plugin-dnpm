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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class DatabaseMigrationServiceTest {

  JdbcTemplate jdbcTemplate;
  ResourcePatternResolver resourcePatternResolver;

  DatabaseMigrationService databaseMigration;

  @BeforeEach
  void setUp(
      @Mock JdbcTemplate jdbcTemplate, @Mock ResourcePatternResolver resourcePatternResolver) {
    this.jdbcTemplate = jdbcTemplate;
    this.resourcePatternResolver = resourcePatternResolver;

    databaseMigration = new DatabaseMigrationService(jdbcTemplate, resourcePatternResolver);
  }

  @Test
  void shouldGetNewMigrationFiles() throws Exception {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql", "updatednpm_003.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      var actual = databaseMigration.getNewMigrationIds();
      assertThat(actual).containsExactly("updatednpm_003.sql");
    }
  }

  @Test
  void shouldIgnoreOldMigrationFiles() throws Exception {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      var actual = databaseMigration.getNewMigrationIds();
      assertThat(actual).isEmpty();
    }
  }

  @Test
  void shouldExecuteMigrationFile() throws Exception {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getSqlQuery(any(ResourcePatternResolver.class), anyString()))
          .thenReturn("UPDATE TABLE test;");

      databaseMigration.migrate();

      var argumentCaptor = ArgumentCaptor.forClass(String.class);
      verify(jdbcTemplate, times(1)).execute(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue()).isEqualTo("UPDATE TABLE test;");
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void shouldIgnoreMigrationFileDueToMissingFormRev(boolean formRevisionAvailable) {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql"));

      databaseUtils
          .when(
              () ->
                  MigrationUtils.requiredFormRevisionAvailable(
                      any(JdbcTemplate.class), anyString(), anyString(), anyString()))
          .thenReturn(formRevisionAvailable);

      databaseUtils
          .when(() -> MigrationUtils.getSqlQuery(any(ResourcePatternResolver.class), anyString()))
          .thenReturn("-- form: DNPM Test\n-- min-rev: 1\n-- max-rev: 2\nUPDATE TABLE test;");

      databaseMigration.migrate();

      if (formRevisionAvailable) {
        verify(jdbcTemplate, times(1)).execute(anyString());
      } else {
        verify(jdbcTemplate, times(0)).execute(anyString());
      }
    }
  }

  @Test
  void shouldNotCheckRequiredRevsionRange() {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql"));

      databaseUtils
          .when(
              () ->
                  MigrationUtils.requiredFormRevisionAvailable(
                      any(JdbcTemplate.class), anyString(), anyString(), anyString()))
          .thenReturn(false);

      databaseUtils
          .when(() -> MigrationUtils.getSqlQuery(any(ResourcePatternResolver.class), anyString()))
          .thenReturn("UPDATE TABLE test;");

      databaseMigration.migrate();

      databaseUtils.verify(
          () -> {
            MigrationUtils.requiredFormRevisionAvailable(
                any(JdbcTemplate.class), anyString(), anyString(), anyString());
          },
          never());
    }
  }

  @Test
  void shouldUseDefaultRequiredRevsionRange() {
    try (MockedStatic<MigrationUtils> databaseUtils = Mockito.mockStatic(MigrationUtils.class)) {
      databaseUtils
          .when(
              () -> MigrationUtils.getDatabaseMigrationScripts(any(ResourcePatternResolver.class)))
          .thenReturn(List.of("updatednpm_001.sql", "updatednpm_002.sql"));

      databaseUtils
          .when(() -> MigrationUtils.getExecutedDatabaseMigrations(any(JdbcTemplate.class)))
          .thenReturn(List.of("updatednpm_001.sql"));

      databaseUtils
          .when(
              () ->
                  MigrationUtils.requiredFormRevisionAvailable(
                      any(JdbcTemplate.class), anyString(), anyString(), anyString()))
          .thenReturn(false);

      databaseUtils
          .when(() -> MigrationUtils.getSqlQuery(any(ResourcePatternResolver.class), anyString()))
          .thenReturn("-- form: DNPM Test\nUPDATE TABLE test;");

      databaseMigration.migrate();

      var formNameCaptor = ArgumentCaptor.forClass(String.class);
      var minRevCaptor = ArgumentCaptor.forClass(String.class);
      var maxRevCaptor = ArgumentCaptor.forClass(String.class);
      databaseUtils.verify(
          () -> {
            MigrationUtils.requiredFormRevisionAvailable(
                any(JdbcTemplate.class),
                formNameCaptor.capture(),
                minRevCaptor.capture(),
                maxRevCaptor.capture());
          },
          times(1));
      assertThat(formNameCaptor.getValue()).isEqualTo("DNPM Test");
      assertThat(minRevCaptor.getValue()).isEqualTo("0");
      assertThat(maxRevCaptor.getValue()).isEqualTo("99999999999999");
    }
  }
}
