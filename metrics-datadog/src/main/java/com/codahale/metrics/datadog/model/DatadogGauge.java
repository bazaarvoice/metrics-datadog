package com.codahale.metrics.datadog.model;


import java.util.List;

public class DatadogGauge extends DatadogSeries<Number> {
    public DatadogGauge(String name, Number count, Long epoch, String host, List<String> additionalTags) {
        super(name, count, epoch, host, additionalTags);
    }

    public String getType() {
        return "gauge";
    }
}
