package com.yammer.metrics.reporting.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DatadogSeries<T extends Number> {

    private String name;
    private T count;
    private Long epoch;
    private String host;
    private List<String> tags;

    // Expect the tags in the pattern
    // namespace.metricName[tag1:value1,tag2:value2,etc....]
    private final Pattern tagPattern = Pattern.compile("([\\w\\.]+)\\[([\\w\\W]+)\\]");

    public DatadogSeries(String name, T count, Long epoch, String host) {
        this.count = count;
        this.epoch = epoch;
        this.host = host;
        this.tags = new ArrayList<String>();
        Matcher matcher = tagPattern.matcher(name);
        if (matcher.find() && matcher.groupCount() == 2) {
            this.name = name.split("\\[")[0].replaceAll("[^a-zA-Z0-9\\.]", "\\_");
            for(String t : matcher.group(2).split("\\,")) {
                String sanitizedTag = t.replaceAll("[^a-zA-Z0-9\\:\\.\\-\\_]", "");
                String[] keyValuePair = sanitizedTag.split("\\:");
                if (keyValuePair[0].equals("host")) {
                    this.host = keyValuePair[1];
                }else{
                    this.tags.add(sanitizedTag);
                }
            }
        } else {
            this.name = name.replaceAll("[^a-zA-Z0-9\\.]", "\\_");
        }
    }

    abstract protected String getType();

    @JsonInclude(Include.NON_NULL)
    public String getHost() {
        return host;
    }

    public String getMetric() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<List<Number>> getPoints() {
        List<Number> point = new ArrayList<Number>();
        point.add(epoch);
        point.add(count);

        List<List<Number>> points = new ArrayList<List<Number>>();
        points.add(point);
        return points;
    }
}