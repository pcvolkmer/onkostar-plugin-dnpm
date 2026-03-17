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

package dev.dnpm.oshelper.services;

import dev.dnpm.oshelper.exceptions.FormException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Standardimplementierung zum Ermitteln von Unter- und Hauptformularen
 *
 * @since 0.0.2
 */
public class DefaultFormService implements FormService {

    private final JdbcTemplate jdbcTemplate;

    public DefaultFormService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getMainFormProcedureId(int procedureId) throws FormException {
        var sql = "SELECT hauptprozedur_id FROM prozedur WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, i) -> resultSet.getInt("hauptprozedur_id"), procedureId);
        } catch (Exception e) {
            throw new FormException(String.format("No main form found for subform with ID '%d'", procedureId));
        }
    }

    @Override
    public List<Integer> getSubFormProcedureIds(int procedureId) {
        var sql = "SELECT id FROM prozedur WHERE hauptprozedur_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, procedureId);
    }
}
