package no.cantara.dropwizard.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.filter.Filter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import net.logstash.logback.encoder.LogstashEncoder;

@JsonTypeName("logstashfile")
public class LogstashFileAppenderFactory extends FileAppenderFactory {

    String customFields;

    @JsonProperty
    public String getCustomFields() {
        return customFields;
    }

    @JsonProperty
    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
        return this.build(context,applicationName, layout,null);
    }

    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout,AsyncAppenderFactory asyncAppenderFactory) {
        final FileAppender<ILoggingEvent> appender = buildAppender(context);
        appender.setName("logstashfile-appender");

        appender.setAppend(true);
        appender.setContext(context);

        LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setTimeZone(getTimeZone().getID());
        logstashEncoder.setIncludeCallerData(isIncludeCallerData());
        logstashEncoder.setCustomFields(customFields);
        logstashEncoder.start();
        appender.setEncoder(logstashEncoder);

        appender.setPrudent(false);
//        addThresholdFilter(appender, threshold);
        Filter<ILoggingEvent> thresholdFilter = createThresholdFilter(threshold);
        appender.addFilter(thresholdFilter);
        appender.stop();
        appender.start();

        return wrapAsync(appender,asyncAppenderFactory);
    }

    private Filter<ILoggingEvent> createThresholdFilter(Level threshold) {
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(threshold.toString());
        filter.start();
        return filter;
    }
}