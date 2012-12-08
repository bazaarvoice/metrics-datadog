package com.yammer.metrics.reporting;

import com.yammer.metrics.reporting.model.DatadogGauge;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DatadogGaugeTest {

    @Test
    public void testSplitNameAndTags() {
        DatadogGauge gauge = new DatadogGauge(
                "test[tag1:value1,tag2:value2,tag3:value3]", 1L, 1234L, "Test Host");
        List<String> tags = gauge.getTags();

        assertEquals(3, tags.size());
        assertEquals("tag1:value1", tags.get(0));
        assertEquals("tag2:value2", tags.get(1));
        assertEquals("tag3:value3", tags.get(2));
    }

    @Test
    public void testStripInvalidCharsFromTagsAndNames() {
        DatadogGauge gauge = new DatadogGauge(
                "test.name-with_chars[tag.1:va  lue.1,tag-2:va %lue-2,ta  %# g_3:value_3]", 1L, 1234L, "Test Host");
        List<String> tags = gauge.getTags();

        assertEquals(3, tags.size());
        assertEquals("tag.1:value.1", tags.get(0));
        assertEquals("tag-2:value-2", tags.get(1));
        assertEquals("tag_3:value_3", tags.get(2));

        assertEquals("test.name_with_chars", gauge.getMetric());
    }
}