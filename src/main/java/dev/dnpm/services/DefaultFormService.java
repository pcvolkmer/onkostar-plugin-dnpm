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

package dev.dnpm.services;

import dev.dnpm.exceptions.FormException;
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
