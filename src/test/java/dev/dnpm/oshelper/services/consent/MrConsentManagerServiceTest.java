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

package dev.dnpm.oshelper.services.consent;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MrConsentManagerServiceTest {

    private IOnkostarApi onkostarApi;

    private MrConsentManagerService service;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.service = new MrConsentManagerService(onkostarApi);
    }

    @Test
    void testShouldCreateSqlQueriesWithRelatedEntityIds() {
        var sessionFactory = mock(SessionFactory.class);
        var session = mock(Session.class);
        var query = mock(SQLQuery.class);

        when(onkostarApi.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createSQLQuery(anyString())).thenReturn(query);
        when(query.addScalar(anyString(), any(Type.class))).thenReturn(query);
        when(query.uniqueResult()).thenReturn("");

        var dummyProzedur = new Procedure(this.onkostarApi);
        dummyProzedur.setId(111);
        dummyProzedur.setPatientId(123);

        this.service.applyConsent(dummyProzedur);

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(session, times(2)).createSQLQuery(argumentCaptor.capture());
        assertThat(argumentCaptor.getAllValues()).hasSize(2);
        assertThat(argumentCaptor.getAllValues().get(0)).contains("where entity_id = '111'");
        assertThat(argumentCaptor.getAllValues().get(1)).contains("WHERE patient_id = 123 AND geloescht = 0");
    }

}
