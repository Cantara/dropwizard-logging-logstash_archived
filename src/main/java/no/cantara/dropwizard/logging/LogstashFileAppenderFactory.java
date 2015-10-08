package no.cantara.dropwizard.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.FileAppenderFactory;
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

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
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
        addThresholdFilter(appender, threshold);
        appender.stop();
        appender.start();

        return wrapAsync(appender);
    }
}