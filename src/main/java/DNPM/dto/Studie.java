/*
 * MIT License
 *
 * 2023 Comprehensive Cancer Center Mainfranken
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

package DNPM.dto;

public class Studie {
    private final String kategorieName;
    private final String code;
    private final String studiennummer;
    private final String shortDesc;
    private final String description;
    private final int version;

    public Studie(final String kategorieName, final int version, final String code, final String studiennummer, final String shortDesc, final String description) {
        this.kategorieName = kategorieName;
        this.version = version;
        this.code = code;
        this.studiennummer = studiennummer;
        this.shortDesc = shortDesc;
        this.description = description;
    }

    public String getKategorieName() {
        return kategorieName;
    }

    public int getVersion() {
        return version;
    }

    public String getCode() {
        return code;
    }

    public String getStudiennummer() {
        return studiennummer;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        if (this.hasNctNumber()) {
            return Type.NCT;
        } else if (this.hasEudraCtNumber()) {
            return Type.EUDRA_CT;
        } else {
            return Type.UNKNOWN;
        }
    }

    private boolean hasNctNumber() {
        return null != studiennummer && studiennummer.toLowerCase().startsWith("nct");
    }

    private boolean hasEudraCtNumber() {
        return null != studiennummer && studiennummer.matches("[0-9]{4}-[0-9]{6}-[0-9]{2}");
    }

    public enum Type {
        NCT,
        EUDRA_CT,
        UNKNOWN
    }
}
