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
