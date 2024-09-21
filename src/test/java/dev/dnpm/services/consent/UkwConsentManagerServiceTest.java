package dev.dnpm.services.consent;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UkwConsentManagerServiceTest {

    private IOnkostarApi onkostarApi;

    private UkwConsentManagerService service;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.service = new UkwConsentManagerService(onkostarApi);
    }

    @Test
    void testShouldSkipUpdateRelatedDnpmKlinikAnamneseFormIfNoConsentAvailable() throws Exception {

        var excelForm = new Procedure(this.onkostarApi);
        excelForm.setId(111);
        excelForm.setPatientId(123);
        excelForm.setValue("refdnpmklinikanamnese", new Item("refdnpmklinikanamnese", 2));

        var dnpmKlinikAnamneseForm = new Procedure(this.onkostarApi);
        dnpmKlinikAnamneseForm.setId(2);
        dnpmKlinikAnamneseForm.setPatientId(123);

        when(onkostarApi.getProcedure(anyInt())).thenReturn(dnpmKlinikAnamneseForm);

        this.service.applyConsent(excelForm);

        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void testShouldSkipUpdateRelatedDnpmKlinikAnamneseFormIfNoConsentDateAvailable() throws Exception {

        var consentSubForm = new Procedure(this.onkostarApi);
        consentSubForm.setId(1);
        consentSubForm.setPatientId(123);
        consentSubForm.setValue("status", new Item("status", "accepted"));


        var excelForm = new Procedure(this.onkostarApi);
        excelForm.setId(111);
        excelForm.setPatientId(123);
        excelForm.setValue("refdnpmklinikanamnese", new Item("refdnpmklinikanamnese", 2));
        excelForm.addSubProcedure("ufdnpmconsent", consentSubForm);

        var dnpmKlinikAnamneseForm = new Procedure(this.onkostarApi);
        dnpmKlinikAnamneseForm.setId(2);
        dnpmKlinikAnamneseForm.setPatientId(123);

        when(onkostarApi.getProcedure(anyInt())).thenReturn(dnpmKlinikAnamneseForm);

        this.service.applyConsent(excelForm);

        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void testShouldSkipUpdateRelatedDnpmKlinikAnamneseFormIfNoConsentValueAvailable() throws Exception {

        var consentSubForm = new Procedure(this.onkostarApi);
        consentSubForm.setId(1);
        consentSubForm.setPatientId(123);
        consentSubForm.setStartDate(Date.from(Instant.parse("2023-04-03T12:00:00Z")));
        consentSubForm.setValue("datum", new Item("datum", Date.from(Instant.parse("2023-04-03T12:00:00Z"))));

        var excelForm = new Procedure(this.onkostarApi);
        excelForm.setId(111);
        excelForm.setPatientId(123);
        excelForm.setValue("refdnpmklinikanamnese", new Item("refdnpmklinikanamnese", 2));
        excelForm.addSubProcedure("ufdnpmconsent", consentSubForm);

        var dnpmKlinikAnamneseForm = new Procedure(this.onkostarApi);
        dnpmKlinikAnamneseForm.setId(2);
        dnpmKlinikAnamneseForm.setPatientId(123);

        when(onkostarApi.getProcedure(anyInt())).thenReturn(dnpmKlinikAnamneseForm);

        this.service.applyConsent(excelForm);

        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void testShouldUpdateRelatedDnpmKlinikAnamneseFormOnFormSave() throws Exception {

        var consentSubForm = new Procedure(this.onkostarApi);
        consentSubForm.setId(1);
        consentSubForm.setPatientId(123);
        consentSubForm.setStartDate(Date.from(Instant.parse("2023-04-03T12:00:00Z")));
        consentSubForm.setValue("datum", new Item("datum", Date.from(Instant.parse("2023-04-03T12:00:00Z"))));
        consentSubForm.setValue("status", new Item("status", "accepted"));

        var excelForm = new Procedure(this.onkostarApi);
        excelForm.setId(111);
        excelForm.setPatientId(123);
        excelForm.setValue("refdnpmklinikanamnese", new Item("refdnpmklinikanamnese", 2));
        excelForm.addSubProcedure("ufdnpmconsent", consentSubForm);

        var dnpmKlinikAnamneseForm = new Procedure(this.onkostarApi);
        dnpmKlinikAnamneseForm.setId(2);
        dnpmKlinikAnamneseForm.setPatientId(123);

        when(onkostarApi.getProcedure(anyInt())).thenReturn(dnpmKlinikAnamneseForm);

        this.service.applyConsent(excelForm);

        var argumentCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(onkostarApi, times(1)).saveProcedure(argumentCaptor.capture(), anyBoolean());

        var savedForm = argumentCaptor.getValue();
        assertThat(savedForm).isExactlyInstanceOf(Procedure.class);
        assertThat(savedForm.getValue("ConsentStatusEinwilligungDNPM").getString()).isEqualTo("accepted");
        assertThat(savedForm.getValue("ConsentDatumEinwilligungDNPM").getDate()).isEqualTo("2023-04-03T12:00:00Z");
    }

}
