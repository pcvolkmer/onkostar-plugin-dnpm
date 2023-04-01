package DNPM;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentManagerTest {

    private IOnkostarApi onkostarApi;

    private ConsentManager consentManager;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManager = new ConsentManager(onkostarApi);
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

        consentManager.analyze(dummyProzedur, null);

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(session, times(2)).createSQLQuery(argumentCaptor.capture());
        assertThat(argumentCaptor.getAllValues()).hasSize(2);
        assertThat(argumentCaptor.getAllValues().get(0)).contains("where entity_id = '111'");
        assertThat(argumentCaptor.getAllValues().get(1)).contains("WHERE patient_id = 123 AND geloescht = 0");
    }

}
