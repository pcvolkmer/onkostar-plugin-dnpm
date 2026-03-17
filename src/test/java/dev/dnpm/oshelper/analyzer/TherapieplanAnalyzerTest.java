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

package dev.dnpm.oshelper.analyzer;

import dev.dnpm.oshelper.security.DelegatingDataBasedPermissionEvaluator;
import dev.dnpm.oshelper.security.PermissionType;
import dev.dnpm.oshelper.services.FormService;
import dev.dnpm.oshelper.services.mtb.MtbService;
import dev.dnpm.oshelper.services.therapieplan.MultipleMtbTherapieplanService;
import dev.dnpm.oshelper.services.therapieplan.TherapieplanService;
import dev.dnpm.oshelper.services.therapieplan.TherapieplanServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    @Mock
    private TherapieplanServiceFactory therapieplanServiceFactory;

    @Mock
    private TherapieplanService therapieplanService;

    @Mock
    private MtbService mtbService;

    @Mock
    private DelegatingDataBasedPermissionEvaluator permissionEvaluator;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(therapieplanServiceFactory, mtbService, permissionEvaluator);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        when(this.therapieplanServiceFactory.currentUsableInstance())
                .thenReturn(new MultipleMtbTherapieplanService(onkostarApi, formService));

        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(this.therapieplanServiceFactory, times(1)).currentUsableInstance();
    }

    @Test
    void shouldRequestProtokollauszug() {
        doAnswer(invocationOnMock -> {
            var procedure = new Procedure(onkostarApi);
            procedure.setValue("referstemtb", new Item("referstemtb", 2345));
            return List.of(procedure);
        }).when(this.therapieplanService).findReferencedMtbs(anyInt());

        when(this.therapieplanServiceFactory.currentUsableInstance())
                .thenReturn(therapieplanService);

        when(this.permissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class))).thenReturn(true);

        var input = Map.of("id", (Object) 1234);
        this.therapieplanAnalyzer.getProtokollauszug(input);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(mtbService, times(1)).getProtocol(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void shouldNotRequestProtokollauszugDueToNoPermission() {
        when(this.permissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class)))
                .thenReturn(false);

        var input = Map.of("id", (Object) 1234);
        this.therapieplanAnalyzer.getProtokollauszug(input);

        verify(mtbService, times(0)).getProtocol(anyList());
    }

}
