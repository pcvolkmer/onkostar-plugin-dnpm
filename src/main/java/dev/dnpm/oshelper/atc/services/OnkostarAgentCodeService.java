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

package dev.dnpm.oshelper.atc.services;

import dev.dnpm.oshelper.atc.AgentCode;
import dev.dnpm.oshelper.atc.AtcCode;
import dev.dnpm.oshelper.atc.UnregisteredCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Implementation of {@link AgentCodeService} that uses database to query for unregistered agents
 *
 * @author Paul-Christian Volkmer
 * @since 2.0.0
 */
@Service
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
