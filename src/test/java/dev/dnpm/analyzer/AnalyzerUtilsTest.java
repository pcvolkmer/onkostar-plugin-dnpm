package dev.dnpm.analyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzerUtilsTest {

    private final Map<String, Object> input = Map.of("value1", 1, "valueA", "A", "valueTrue", true);

    private static Set<TestTypeData> testTypeData() {
        return Set.of(
                new TestTypeData("value1", Integer.class).withExpectedResult(true),
                new TestTypeData("valueA", String.class).withExpectedResult(true),
                new TestTypeData("valueTrue", Boolean.class).withExpectedResult(true),

                new TestTypeData("value1", String.class).withExpectedResult(false),
                new TestTypeData("valueA", Boolean.class).withExpectedResult(false),
                new TestTypeData("valueTrue", Integer.class).withExpectedResult(false),

                new TestTypeData("value1", Boolean.class).withExpectedResult(false),
                new TestTypeData("valueA", Integer.class).withExpectedResult(false),
                new TestTypeData("valueTrue", String.class).withExpectedResult(false)
        );
    }

    @ParameterizedTest
    @MethodSource("testTypeData")
    void testShouldReturnExpectedResultForTypedCheck(TestTypeData testData) {
        var actual = AnalyzerUtils.requiredValuePresent(input, testData.key, testData.type);
        assertThat(actual).isEqualTo(testData.result);
    }

    private static Set<TestMatchData> testMatchData() {
        return Set.of(
                new TestMatchData("value1", "[\\d]").withExpectedResult(true),
                new TestMatchData("valueA", "[A-Z]").withExpectedResult(true),

                new TestMatchData("value1", "[A-Z]").withExpectedResult(false),
                new TestMatchData("valueA", "[a-z]").withExpectedResult(false),
                new TestMatchData("valueA", "[\\d]").withExpectedResult(false)
        );
    }

    @ParameterizedTest
    @MethodSource("testMatchData")
    void testShouldReturnExpectedResultForMatchCheck(TestMatchData testData) {
        var actual = AnalyzerUtils.requiredValueMatches(input, testData.key, testData.regexp);
        assertThat(actual).isEqualTo(testData.result);
    }

    @Test
    void testShouldCheckIfInputValueIsIdNumber() {
        assertThat(AnalyzerUtils.requiredValueIsId(Map.of("value", 0), "value")).isFalse();
        assertThat(AnalyzerUtils.requiredValueIsId(Map.of("value", "ABC"), "value")).isFalse();
        assertThat(AnalyzerUtils.requiredValueIsId(Map.of("value", 1234), "value")).isTrue();
    }

    @Test
    void testShouldReturnInputValueAsOptional() {
        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", 1234), "value", Integer.class)).isEqualTo(Optional.of(1234));
        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", "ABC"), "value", String.class)).isEqualTo(Optional.of("ABC"));

        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", 1234), "value1", Integer.class)).isEmpty();
        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", "ABC"), "value1", String.class)).isEmpty();
        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", 1234), "value", String.class)).isEmpty();
        assertThat(AnalyzerUtils.getRequiredValue(Map.of("value", "ABC"), "value", Boolean.class)).isEmpty();
    }

    @Test
    void testShouldReturnInputIdAsOptional() {
        assertThat(AnalyzerUtils.getRequiredId(Map.of("value", 1234), "value")).isEqualTo(Optional.of(1234));

        assertThat(AnalyzerUtils.getRequiredId(Map.of("value", 1234), "value1")).isEmpty();
        assertThat(AnalyzerUtils.getRequiredId(Map.of("value", "ABC"), "value")).isEmpty();
        assertThat(AnalyzerUtils.getRequiredId(Map.of("value", 0), "value")).isEmpty();
    }

    @Test
    void testShouldReturnInputValueMatchingAsOptional() {
        assertThat(AnalyzerUtils.getRequiredValueMatching(Map.of("value", 1234), "value", "[\\d]+")).isEqualTo(Optional.of("1234"));
        assertThat(AnalyzerUtils.getRequiredValueMatching(Map.of("value", "ABC"), "value", "[A-Z]+")).isEqualTo(Optional.of("ABC"));

        assertThat(AnalyzerUtils.getRequiredValueMatching(Map.of("value", "ABC"), "value1", "[A-Z]+")).isEmpty();
    }

    private static class TestTypeData {
        public final String key;
        public final Class<?> type;

        public boolean result;

        public TestTypeData(String key, Class<?> type) {
            this.key = key;
            this.type = type;
        }

        public TestTypeData withExpectedResult(boolean result) {
            this.result = result;
            return this;
        }

        @Override
        public String toString() {
            return String.format("key: '%s', type: %s, result: %s", key, type.getSimpleName(), result);
        }
    }

    private static class TestMatchData {
        public final String key;
        public final String regexp;

        public boolean result;

        public TestMatchData(String key, String regexp) {
            this.key = key;
            this.regexp = regexp;
        }

        public TestMatchData withExpectedResult(boolean result) {
            this.result = result;
            return this;
        }

        @Override
        public String toString() {
            return String.format("key: '%s', regexp: '%s', result: %s", key, regexp, result);
        }
    }

}
