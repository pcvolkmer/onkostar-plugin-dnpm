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

package dev.dnpm.oshelper.atc;

import java.util.Objects;

/**
 * Unregistered code as used in Onkostar database
 *
 * @author Paul-Christian Volkmer
 * @since 2.0.0
 */
public class UnregisteredCode implements AgentCode {

    private final String code;
    private final String name;

    public UnregisteredCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public CodeSystem getSystem() {
        return CodeSystem.UNREGISTERED;
    }

    public String getVersion() {
        return null;
    }

    @Override
    public int compareTo(final AgentCode agentCode) {
        return this.name.toLowerCase().compareTo(agentCode.getName().toLowerCase());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentCode otherAgentCode = (AgentCode) o;
        return Objects.equals(code.toLowerCase(), otherAgentCode.getCode().toLowerCase())
                && Objects.equals(name.toLowerCase(), otherAgentCode.getName().toLowerCase())
                && Objects.equals(this.getVersion(), otherAgentCode.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.toLowerCase(), name.toLowerCase());
    }
}
