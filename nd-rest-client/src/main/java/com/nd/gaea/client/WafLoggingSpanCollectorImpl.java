package com.nd.gaea.client;

import com.github.kristofa.brave.SpanCollector;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.AnnotationType;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Span;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafLoggingSpanCollectorImpl implements SpanCollector {

    private static final String UTF_8 = "UTF-8";

    private final Logger logger;
    private final Set<BinaryAnnotation> defaultAnnotations = new HashSet<BinaryAnnotation>();

    public WafLoggingSpanCollectorImpl() {
        logger = LoggerFactory.getLogger(WafLoggingSpanCollectorImpl.class);
    }

    public WafLoggingSpanCollectorImpl(final String loggerName) {
        Validate.notEmpty(loggerName);
        logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collect(final Span span) {
        Validate.notNull(span);
        if (!defaultAnnotations.isEmpty()) {
            for (final BinaryAnnotation ba : defaultAnnotations) {
                span.addToBinary_annotations(ba);
            }
        }

        if (getLogger().isInfoEnabled()) {
            getLogger().info(span.toString());
        }
    }

    void annotationToString(StringBuilder sb, Annotation annotation) {
        sb.append("Annotation(");
        sb.append("timestamp:");
        sb.append(annotation.getTimestamp());
        sb.append(", ");
        sb.append("value:");
        String value = annotation.getValue();
        if (value == null) {
            sb.append("null");
        } else {
            sb.append(value);
        }
        if (annotation.isSetHost()) {
            sb.append(", ");
            sb.append("host:");
            if (annotation.getHost() == null) {
                sb.append("null");
            } else {
                sb.append(annotation.getHost());
            }
        }
        sb.append(")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do for this collector.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDefaultAnnotation(final String key, final String value) {
        Validate.notEmpty(key);
        Validate.notNull(value);

        try {
            final ByteBuffer bb = ByteBuffer.wrap(value.getBytes(UTF_8));

            final BinaryAnnotation binaryAnnotation = new BinaryAnnotation();
            binaryAnnotation.setKey(key);
            binaryAnnotation.setValue(bb);
            binaryAnnotation.setAnnotation_type(AnnotationType.STRING);
            defaultAnnotations.add(binaryAnnotation);

        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    Logger getLogger() {
        return logger;
    }
}
