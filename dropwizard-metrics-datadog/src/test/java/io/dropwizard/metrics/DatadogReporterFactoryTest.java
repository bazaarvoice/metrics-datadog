package io.dropwizard.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatadogReporterFactoryTest {
    @Test
    public void isDiscoverable() throws Exception {
        Assertions.assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(DatadogReporterFactory.class);
    }

    @Test
    public void checkForRegexSupportInBaseReporterFactory() {
        // The RegexMetricFilter used in DatadogReporterFactory is a stop-gap solution to support regex expressions in our metric filters until the relevant change in Dropwizard
        // (https://github.com/dropwizard/dropwizard/pull/884) becomes available. This test exists to proactively detect the presence of that change and alert the developer
        // so that he/she can 1.) remove the RegexMetricFilter class and 2.) remove the custom getFilter() and "useRegexFilters" field from the DatadogReporterFactory.
        Field[] fields = BaseReporterFactory.class.getDeclaredFields();
        boolean fieldFound = false;
        for (Field field : fields) {
            if (field.getName().equals("useRegexFilters")) {
                fieldFound = true;
                break;
            }
        }
        Assertions.assertThat(fieldFound).isFalse();
    }

    @Test
    public void testRegexFilterMatch() {
        ImmutableSet<String> includesSet = ImmutableSet.of();
        ImmutableSet<String> excludesSet = ImmutableSet.of("foo.*");
        MetricFilter defaultMetricFilter = createMetricFilter(includesSet, excludesSet, false);
        MetricFilter regexMetricFilter = createMetricFilter(includesSet, excludesSet, true);
        // "foobar" should be correctly rejected by the exclusion expression "foo.*" when using the regex filter but allowed
        // under the default filter (since it does not match the string exactly)
        Assertions.assertThat(defaultMetricFilter.matches("foobar", mock(Metric.class))).isTrue();
        Assertions.assertThat(regexMetricFilter.matches("foobar", mock(Metric.class))).isFalse();
    }

    private static MetricFilter createMetricFilter(ImmutableSet includesSet, ImmutableSet excludesSet, boolean useRegex) {
        DatadogReporterFactory reporterFactory = mock(DatadogReporterFactory.class);
        when(reporterFactory.getIncludes()).thenReturn(includesSet);
        when(reporterFactory.getExcludes()).thenReturn(excludesSet);
        when(reporterFactory.useRegexFilters()).thenReturn(useRegex);
        when(reporterFactory.getFilter()).thenCallRealMethod();
        return reporterFactory.getFilter();
    }
}
