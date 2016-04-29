package com.nd.gaea.rest.security.services.visitor;

import java.util.List;

/**
 * 游客模式下的组织接口黑名单映射
 * @author 110825
 * @since 0.9.6
 */
public interface VistorsService {
	public List<String> getWhiteRequestMappings(String key);
}
