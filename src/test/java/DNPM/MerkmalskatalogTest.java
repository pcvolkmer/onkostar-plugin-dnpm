package DNPM;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

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
