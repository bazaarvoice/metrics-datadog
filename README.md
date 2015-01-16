# Metrics Datadog Reporter
Simple Metrics reporter backend that sends metrics to DataDog.

## Usage

~~~java
import com.codahale.metrics.datadog.DatadogReporter

final Datadog datadog = new Datadog(apiKey, applicationKey)
final DatadogReporter reporter = DatadogReporter.forRegistry(registry)
                                                .filter(MetricFilter.ALL)
                                                .build(datadog);

reporter.start(1, TimeUnit.MINUTES);
~~~

The hostname provided with each publish needs to match the hostname defined in the /etd/dd-agent/datadog.conf file
that configures the DataDog agent.  This is so that the metrics being pushed directly to datadog via this library
can be associated with other (box-level) metrics being pushed by the agent.

### Dropwizard Metrics Reporter

If you have a dropwizard project and have at least `dropwizard-core` 0.7.X, 
then you can just add this to your dropwizard YAML configuration file.

~~~yaml
metrics:
  frequency: 1 minute                       # Default is 1 second.
  reporters:
    - type: datadog
      host: <host>
      tags: <tags>                          # Optional.
      apiKey: <apiKey>
      applicationKey: <applicationKey>      # Optional
      includes:                             # Optional. Defaults to (all).
      excludes:                             # Optional. Defaults to (none).
~~~

If you want to whitelist only a few metrics, you can use the `includes` key to
create a set of metrics to include. 

~~~yaml
metrics:
  frequency: 1 minute                       # Default is 1 second.
  reporters:
    - type: datadog
      host: <host>
      tags: <tags>
      apiKey: <apiKey>
      applicationKey: <applicationKey>      # Optional
      includes:
        - jvm.
        - ch.
        
~~~

The check is very simplistic so be as specific as possible. For example, if 
you have "jvm.", the filter will check if the includes has that value in any 
part of the metric name (not just the beginning).