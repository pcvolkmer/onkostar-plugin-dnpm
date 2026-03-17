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

import dev.dnpm.oshelper.analyzer.ConsentManager;
import dev.dnpm.oshelper.services.consent.ConsentManagerServiceFactory;
import dev.dnpm.oshelper.services.consent.MrConsentManagerService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentManagerTest {

    private IOnkostarApi onkostarApi;

    private ConsentManagerServiceFactory consentManagerServiceFactory;

    private ConsentManager consentManager;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock ConsentManagerServiceFactory consentManagerServiceFactory
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManagerServiceFactory = consentManagerServiceFactory;
        this.consentManager = new ConsentManager(onkostarApi, consentManagerServiceFactory);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        var consentManagerServiceMock = mock(MrConsentManagerService.class);

        when(consentManagerServiceMock.canApply(any(Procedure.class))).thenReturn(true);

        when(this.consentManagerServiceFactory.currentUsableInstance())
                .thenReturn(consentManagerServiceMock);

        this.consentManager.analyze(new Procedure(onkostarApi), null);

        verify(consentManagerServiceMock, times(1)).applyConsent(any(Procedure.class));
    }

    @Test
    void shouldNotRunServiceMethodsIfProcedureCannotBeAppliesForForm() {
        var consentManagerServiceMock = mock(MrConsentManagerService.class);

        when(consentManagerServiceMock.canApply(any(Procedure.class))).thenReturn(false);

        when(this.consentManagerServiceFactory.currentUsableInstance())
                .thenReturn(consentManagerServiceMock);

        this.consentManager.analyze(new Procedure(onkostarApi), null);

        verify(consentManagerServiceMock, times(0)).applyConsent(any(Procedure.class));
    }

}
