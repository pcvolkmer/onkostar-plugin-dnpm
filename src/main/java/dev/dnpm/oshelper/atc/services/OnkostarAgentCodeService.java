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

package dev.dnpm.oshelper.atc.services;

import dev.dnpm.oshelper.atc.AgentCode;
import dev.dnpm.oshelper.atc.AtcCode;
import dev.dnpm.oshelper.atc.UnregisteredCode;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Implementation of {@link AgentCodeService} that uses database to query for unregistered agents
 *
 * @author Paul-Christian Volkmer
 * @since 2.0.0
 */
public class OnkostarAgentCodeService implements AgentCodeService {

    private final JdbcTemplate jdbcTemplate;

    public OnkostarAgentCodeService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Queries source for agents code starting with or name containing query string.
     * If size is zero, all available results will be returned.
     *
     * @param query The query string
     * @param size  Maximal amount of responses
     * @return A list with agent codes
     */
    @Override
    public List<AgentCode> findAgentCodes(final String query, final int size) {
        var sql = "SELECT code, shortdesc\n" +
                "    FROM property_catalogue\n" +
                "    JOIN property_catalogue_version ON (property_catalogue_version.datacatalog_id = property_catalogue.id)\n" +
                "    JOIN property_catalogue_version_entry p ON (p.property_version_id = property_catalogue_version.id)\n" +
                "    WHERE name = 'OS.Substanzen'\n" +
                "    AND (LOWER(code) LIKE ? OR LOWER(shortdesc) LIKE ? OR LOWER(synonyms) LIKE ?)";

        if (size > 0) {
            sql = sql + " LIMIT " + size;
        }

        return jdbcTemplate.query(
                sql,
                new Object[]{query + "%", "%" + query + "%", "%" + query + "%"},
                (resultSet, i) -> {
                    var code = resultSet.getString("code");
                    var shortdesc = resultSet.getString("shortdesc");
                    if (AtcCode.isAtcCode(code)) {
                        return new AtcCode(code, shortdesc);
                    }
                    return new UnregisteredCode(code, shortdesc);
                }
        );
    }
}
