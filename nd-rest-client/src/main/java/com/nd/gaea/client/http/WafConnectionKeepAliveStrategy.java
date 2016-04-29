package com.nd.gaea.client.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
    /**
     * Returns the duration of time which this connection can be safely kept
     * idle. If the connection is left idle for longer than this period of time,
     * it MUST not reused. A value of 0 or less may be returned to indicate that
     * there is no suitable suggestion.
     * <p/>
     * When coupled with a {@link org.apache.http.ConnectionReuseStrategy}, if
     * {@link org.apache.http.ConnectionReuseStrategy#keepAlive(
     *org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)} returns true, this allows you to control
     * how long the reuse will last. If keepAlive returns false, this should
     * have no meaningful impact
     *
     * @param response The last response received over the connection.
     * @param context  the context in which the connection is being used.
     * @return the duration in ms for which it is safe to keep the connection
     * idle, or <=0 if no suggested duration.
     */
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        HeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000;
                } catch (NumberFormatException ignore) {
                }
            }
        }
        // keep alive for 30 seconds
        return 30 * 1000;
    }
}
