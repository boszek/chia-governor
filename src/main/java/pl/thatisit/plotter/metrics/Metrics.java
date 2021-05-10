package pl.thatisit.plotter.metrics;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class Metrics {
    private static PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static PrometheusMeterRegistry registry() {
        return prometheusRegistry;
    }
}
