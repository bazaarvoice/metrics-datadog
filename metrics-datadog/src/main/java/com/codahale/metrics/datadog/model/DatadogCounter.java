package com.codahale.metrics.datadog.model;

import java.util.List;

public class DatadogCounter extends DatadogSeries<Long> {

    public DatadogCounter(String name, Long count, Long epoch, String host, List<String> additionalTags) {
        super(name, count, epoch, host, additionalTags);
    }

    public String getType() {
        return "counter";
    }
}
