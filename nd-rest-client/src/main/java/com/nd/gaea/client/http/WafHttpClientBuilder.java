package com.nd.gaea.client.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nd.gaea.WafProperties;
import com.nd.sdp.trace.http.HttpClientTracer;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafHttpClientBuilder {
    private IdleConnectionMonitorThread monitorThread;
    private HttpClientConnectionManager connectionManager;
    private HttpClient httpClient;
    private HttpRequestRetryHandler httpRequestRetryHandler;

    public HttpClient build() {
        connectionManager = buildConnectionManager();
        httpRequestRetryHandler = new WafHttpRequestRetryHandler();
        httpClient = buildHttpClient(connectionManager);
        return httpClient;
    }

    public HttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
    
    public HttpClient getHttpClient(int retryCount) {
         return this.getHttpClient(retryCount, new ArrayList<Class<? extends IOException>>());
    }
    
    public HttpClient getHttpClient(int retryCount, Collection<Class<? extends IOException>> clazzes) {
   	 	connectionManager = buildConnectionManager();
		if (clazzes==null) {
			clazzes = new ArrayList<Class<? extends IOException>>();
		}
        httpRequestRetryHandler = new WafHttpRequestRetryHandler(retryCount, true, clazzes);
        httpClient = buildHttpClient(connectionManager);
        return httpClient;
   }

    protected HttpClient buildHttpClient(HttpClientConnectionManager connectionManager) {
        org.apache.http.impl.client.HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder = configHttpClientBuilder(httpClientBuilder, connectionManager, httpRequestRetryHandler);
        return httpClientBuilder.build();
    }

    protected org.apache.http.impl.client.HttpClientBuilder configHttpClientBuilder(org.apache.http.impl.client.HttpClientBuilder httpClientBuilder, HttpClientConnectionManager connectionManager, HttpRequestRetryHandler httpRequestRetryHandler) {
        ConnectionKeepAliveStrategy keepAliveStrategy = new WafConnectionKeepAliveStrategy();
        HttpClientTracer.configHttpClientBuilder(httpClientBuilder);
        return httpClientBuilder
                .setKeepAliveStrategy(keepAliveStrategy)
                .setConnectionManager(connectionManager)
                .setRetryHandler(httpRequestRetryHandler);
    }

    protected HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        configConnectionManager(connectionManager);
        startMonitorThread(connectionManager);
        return connectionManager;
    }

    protected void configConnectionManager(HttpClientConnectionManager connectionManager) {
        if (connectionManager instanceof PoolingHttpClientConnectionManager) {
            PoolingHttpClientConnectionManager poolingConnectionManager = (PoolingHttpClientConnectionManager) connectionManager;
            SocketConfig socketConfig = getConnectionManagerSocketConfig();
            ConnectionConfig connectionConfig = getConnectionManagerConnectionConfig();
            poolingConnectionManager.setDefaultSocketConfig(socketConfig);
            poolingConnectionManager.setDefaultConnectionConfig(connectionConfig);
            poolingConnectionManager.setMaxTotal(WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_MAX_TOTAL));
            poolingConnectionManager.setDefaultMaxPerRoute(WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_MAX_PER_ROUTE));
        }
    }

    protected void startMonitorThread(HttpClientConnectionManager connectionManager) {
        monitorThread = new IdleConnectionMonitorThread(connectionManager);
        // Don't stop quitting.
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    protected ConnectionConfig getConnectionManagerConnectionConfig() {
        return ConnectionConfig.custom()
                .setBufferSize(8 * 1024)
                .setFragmentSizeHint(8 * 1024)
                .build();
    }

    protected SocketConfig getConnectionManagerSocketConfig() {
        return SocketConfig.custom()
                .setSoTimeout(WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_SOCKET_TIMEOUT))
                .build();
    }

    public static class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connMgr;
        protected final Logger log = LoggerFactory.getLogger(this.getClass());

        private volatile boolean shutdown;
        private int counter = 0;
        private boolean logFlag = false;
        private Log4JLogger connMgrLogger;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;

            // 禁用 log.debug,v避免输出大量的日志
            Log connMgrLog = LogFactory.getLog(PoolingHttpClientConnectionManager.class);
            if (connMgrLog instanceof Log4JLogger) {
                connMgrLogger = (Log4JLogger) connMgrLog;
                if (connMgrLogger.isDebugEnabled()) {
                    log.debug("Current log level is debug, set connMgrLogger info level.");
                    connMgrLogger.getLogger().setLevel(org.apache.log4j.Level.INFO);
                    logFlag = true;
                }
            }
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        if (logFlag) {
                            counter++;
                            // 10 分钟打印一次
                            if (counter % 120 == 1) {
                                log.debug("Set connMgrLogger default level.");
                                connMgrLogger.getLogger().setLevel(null);
                            }
                        }
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);

                        if(logFlag && counter % 120 == 1)
                        {
                            log.debug("Set connMgrLogger info level.");
                            connMgrLogger.getLogger().setLevel(Level.INFO);
                        }
                    }
                }
            } catch (InterruptedException ex) {

                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
}
