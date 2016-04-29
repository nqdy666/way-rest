package com.nd.gaea.rest.clientcache;


/**
 * 
 * 项目名字:webrest
 * 类名称:CachePolicy
 * 类描述:缓存策略枚举类
 * 创建人:涂清平
 * 创建时间:2014-11-28下午3:34:15
 * 修改人:
 * 修改时间:
 * 修改备注:
 * @version
 */
public enum CachePolicy {
	
	
	NO_CACHE("no-cache"),
	
	
	NO_STORE("no-store"),
	
	MUST_REVALIDATE("must-revalidate"),
	
	
	PROXY_REVALIDATE("proxy-revalidate"),
	
	
	PRIVATE("private"),
	
	
	PUBLIC("public");
	
	private final String policy;
	
	
	CachePolicy() {
		this.policy = null;
	}
	
	
	CachePolicy(final String policy) {
		this.policy = policy;
	}
	
	public String policy() {
		return this.policy;
	}
}
