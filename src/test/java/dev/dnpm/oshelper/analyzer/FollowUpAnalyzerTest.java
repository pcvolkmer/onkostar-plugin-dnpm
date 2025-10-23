package dev.dnpm.oshelper.analyzer;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowUpAnalyzerTest {

    private IOnkostarApi onkostarApi;

    private FollowUpAnalyzer followUpAnalyzer;

    @BeforeEach
    void setUp(
        @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.followUpAnalyzer = new FollowUpAnalyzer(onkostarApi);
    }

    @Test
    void shouldBacklinkEinzelempfehlungUsingOnkostarApi() throws Exception {
        var einzelempfehlung = new Procedure(onkostarApi);
        einzelempfehlung.setId(1234);

        when(onkostarApi.getProcedure(anyInt())).thenReturn(einzelempfehlung);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setValue("LinkTherapieempfehlung", new Item("LinkTherapieempfehlung", 1234));

        followUpAnalyzer.analyze(procedure, null);

        var procedureIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(onkostarApi, times(1)).getProcedure(procedureIdCaptor.capture());
        assertThat(procedureIdCaptor.getValue()).isEqualTo(einzelempfehlung.getId());

        var procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(onkostarApi, times(1)).saveProcedure(procedureCaptor.capture());
        assertThat(procedureCaptor.getValue()).isNotNull();
        assertThat(procedureCaptor.getValue().getId()).isEqualTo(einzelempfehlung.getId());
    }

    @Test
    void shouldNotBacklinkEinzelempfehlungIfNoEinzelempfehlungSelected() throws Exception {
        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);

        followUpAnalyzer.analyze(procedure, null);

        verify(onkostarApi, times(0)).getProcedure(anyInt());
        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class));
    }

    @Test
    void shouldNotBacklinkEinzelempfehlungIfEinzelempfehlungDoesNotExist() throws Exception {
        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setValue("LinkTherapieempfehlung", new Item("LinkTherapieempfehlung", 1234));

        followUpAnalyzer.analyze(procedure, null);

        var procedureIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(onkostarApi, times(1)).getProcedure(procedureIdCaptor.capture());
        assertThat(procedureIdCaptor.getValue()).isEqualTo(1234);

        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class));
    }

}
