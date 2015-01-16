package io.dropwizard.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.datadog.Datadog;
import com.codahale.metrics.datadog.DatadogReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonTypeName("datadog")
public class DatadogReporterFactory extends BaseReporterFactory {
    @NotNull
    @JsonProperty
    private String host = null;

    @NotNull
    @JsonProperty
    private String apiKey = null;

    @JsonProperty
    private String applicationKey = null;

    @JsonProperty
    private List<String> tags = null;

    @Override
    public ScheduledReporter build(MetricRegistry registry) {
        Datadog datadog = new Datadog(apiKey, applicationKey);
        return DatadogReporter.forRegistry(registry)
                .withHost(host)
                .withTags(tags)
                .filter(getFilter())
                .convertDurationsTo(getDurationUnit())
                .convertRatesTo(getRateUnit())
                .build(datadog);
    }
}