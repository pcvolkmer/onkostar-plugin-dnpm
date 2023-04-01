package ATCCodes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtcCodeTest {

    @Test
    void testShouldDetectAtcCodeScheme() {
        assertTrue(AtcCode.isAtcCode("L01"));
        assertTrue(AtcCode.isAtcCode("L01A"));
        assertTrue(AtcCode.isAtcCode("L01AA"));
        assertTrue(AtcCode.isAtcCode("L01AA01"));
    }

    @Test
    void testShouldDetectInvalidAtcCodeScheme() {
        assertFalse(AtcCode.isAtcCode(null));
        assertFalse(AtcCode.isAtcCode("  "));
        assertFalse(AtcCode.isAtcCode("irgendwas"));
        assertFalse(AtcCode.isAtcCode("L00AA"));
        assertFalse(AtcCode.isAtcCode("Z01AA"));
        assertFalse(AtcCode.isAtcCode("L01AA0"));
    }

}
