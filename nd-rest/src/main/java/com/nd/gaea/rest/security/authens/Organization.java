package com.nd.gaea.rest.security.authens;

import java.io.Serializable;

/**
 * 组织pojo对象
 * @author 110825
 * @since 0.9.5
 */
public class Organization implements Serializable{
	
	private static final long serialVersionUID = -4379403109327060318L;
	
	private String orgId;
	private String orgName;
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
