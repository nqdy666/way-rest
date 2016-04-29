package com.nd.gaea.client.support;

import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.zipkin.SdpZipkinSpanCollector;
import com.github.kristofa.brave.zipkin.ZipkinSpanCollectorParams;
import com.nd.gaea.WafProperties;
import com.twitter.zipkin.gen.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ZipkinSpanCollectorWarpper implements SpanCollector {

	private Logger logger = LoggerFactory.getLogger(ZipkinSpanCollectorWarpper.class);

	/**
	 * zipkin collector服务的主机地址
	 *
	 * @since 0.9.6
	 */
	public static final String WAF_ZIPKIN_COLLECTOR_HOST = "waf.zipkin.collector.host";
	/**
	 * zipkin collector服务的主机端口
	 *
	 * @since 0.9.6
	 */
	public static final String WAF_ZIPKIN_COLLECTOR_PORT = "waf.zipkin.collector.port";
	
	public static final String WAF_ZIPKIN_COLLECTOR_SOCKET_TIMEOUT = "waf.zipkin.collector.socketTimeout";

	private SdpZipkinSpanCollector zipkinSpanCollector;
	private boolean flag;
	private Date lastInitTime;


	public void initZipkinSpanCollector() {
		try {
			lastInitTime = new Date();
			zipkinSpanCollector = instanceZipkinSpanCollector();
			flag = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public SdpZipkinSpanCollector instanceZipkinSpanCollector(){
		ZipkinSpanCollectorParams zipkinSpanCollectorParams = new ZipkinSpanCollectorParams();
		zipkinSpanCollectorParams.setSocketTimeout(WafProperties.getPropertyForInteger(WAF_ZIPKIN_COLLECTOR_SOCKET_TIMEOUT,"10000"));
		final String environment =  WafContext.getEnvironment();
		if ( environment.equals("development") || environment.equals("test") ) {
			return new SdpZipkinSpanCollector(WafProperties.getProperty(WAF_ZIPKIN_COLLECTOR_HOST, "changle.zipkin.collector.sdp"),
					WafProperties.getPropertyForInteger(WAF_ZIPKIN_COLLECTOR_PORT, "9410"), zipkinSpanCollectorParams);
		}
		else {
			return new SdpZipkinSpanCollector(WafProperties.getProperty(WAF_ZIPKIN_COLLECTOR_HOST, "wuxi.zipkin.collector.sdp"),
					WafProperties.getPropertyForInteger(WAF_ZIPKIN_COLLECTOR_PORT, "9410"), zipkinSpanCollectorParams);
		}
	}

	@Override
	public void collect(Span span) {
		if (!flag && isInit()) {
			this.initZipkinSpanCollector();
		}
		if (flag) {
			zipkinSpanCollector.collect(span);
		}
	}

	@Override
	public void addDefaultAnnotation(String key, String value) {
		if (!flag && isInit()) {
			this.initZipkinSpanCollector();
		}
		if (flag) {
			zipkinSpanCollector.addDefaultAnnotation(key, value);
		}
	}

	@Override
	public void close() {
		if (!flag && isInit()) {
			this.initZipkinSpanCollector();
		}
		if (flag) {
			zipkinSpanCollector.close();
		}
	}

	private boolean isInit() {
		Date now = new Date();
		if (lastInitTime!=null) {
			return now.getTime() - lastInitTime.getTime() > 1000 * 60;
		}else {
			return true;
		}
	}

}
