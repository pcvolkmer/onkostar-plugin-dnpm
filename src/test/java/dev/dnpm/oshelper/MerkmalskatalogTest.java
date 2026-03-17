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

package dev.dnpm.oshelper;

import dev.dnpm.oshelper.analyzer.Merkmalskatalog;
import de.itc.onkostar.api.IOnkostarApi;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerkmalskatalogTest {

    private IOnkostarApi onkostarApi;

    private Merkmalskatalog merkmalskatalog;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.merkmalskatalog = new Merkmalskatalog(onkostarApi);
    }

    @Test
    void testShouldReturnNullOnParamCheckIfNoMerkmalskatalogParamGiven() {
        var actual = merkmalskatalog.getMerkmalskatalog(Map.of("Spalten", "id, code"));
        assertThat(actual).isNull();

        verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
    }

    @Test
    void testShouldReturnNullOnParamCheckIfNoSpaltenParamGiven() {
        var actual = merkmalskatalog.getMerkmalskatalog(Map.of("Merkmalskatalog", "MK1"));
        assertThat(actual).isNull();

        verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
    }

    @Test
    void testShouldCreateSqlQueryWithMerkmalskatalog() {
        var sessionFactory = mock(SessionFactory.class);
        var session = mock(Session.class);
        var query = mock(SQLQuery.class);

        when(onkostarApi.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(anyString())).thenReturn(query);

        merkmalskatalog.getMerkmalskatalog(Map.of("Merkmalskatalog", "MK1", "Spalten", "id, code"));

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(session, times(1)).createSQLQuery(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).contains("WHERE name = 'MK1' AND aktiv = 1");
    }

}
