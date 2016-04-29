package com.nd.gaea.client.support;

import org.springframework.util.StringUtils;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.auth.WafBearerTokenServiceImpl;

/**
 * 封装UC信息的静态类
 * @author Administrator
 * @since 0.9.5
 * @deprecated 此类已不推荐使用，建议使用waf.properties配置文件对参数进行配置或通过调用waf提供的接口进行编码设置
 */
@Deprecated
public class WafUcServerConfig {
	
	private static WafUcServerConfig defaultWafUcServerConfig = new WafUcServerConfig();
		
	public static WafUcServerConfig getInstance(){
		return defaultWafUcServerConfig;
	}

	/**
	 * 
	 * 用户自定义UC账号相关信息
	 */
	public void setWafUcServerConfig(){
		defaultWafUcServerConfig = this;
		initUCParams();
	}
	
	// UC账户中心测试发布域地址
	private String UC_API_DOMAIN = "https://aqapi.101.com/";
	// UC账户中心，API接口的版本号
	private String UC_API_VERSION = "v0.93";
	// UC账户中心，验证token有效性的接口地址 %s[0] --token值${access_token}
	private String UC_API_VALID = "/tokens/{access_token}/actions/valid";
	// UC账户中心，根据用户id和业务领域获取用户信息的接口地址
	private String UC_API_GETUSER = "/users/{user_id}?realm={realm}";
	// UC账户中心，根据用户Id和业务领域，获取用户角色信息的接口地址
	private String UC_API_GETUSERROLES = "/users/{user_id}/roles?realm={realm}";
	// 服务器端登录UC中心的请求url
	private String UC_API_SERVER_LOGIN = "/bearer_tokens";
	// 服务器端bearer_token验证地址
	private String UC_API_BEARERTOKEN_VALID = "/bearer_tokens/{bearer_token}/actions/valid";
	// 服务器端令牌续约地址
	private String UC_API_SERVER_REFRESHTOKEN = "/tokens/{refresh_token}/actions/refresh";
	// 访问UC服务的用户名
	private String UC_API_ACCESS_USERNAME = "waf_loginer";
	// 访问UC服务的密码
	private String UC_API_ACCESS_PASSWORD = "80fba977d063a6f7262a8a9c95f61140";
	
	public WafUcServerConfig(){}
	
	public WafUcServerConfig(String domain) {
		this.UC_API_DOMAIN = domain;
	}

	public String getUC_API_DOMAIN() {
		return UC_API_DOMAIN;
	}

	public void setUC_API_DOMAIN(String uC_API_DOMAIN) {
		UC_API_DOMAIN = uC_API_DOMAIN;
	}

	public String getUC_API_VERSION() {
		return UC_API_VERSION;
	}

	public void setUC_API_VERSION(String uC_API_VERSION) {
		UC_API_VERSION = uC_API_VERSION;
	}

	public String getUC_API_VALID() {
		return UC_API_VALID;
	}

	public void setUC_API_VALID(String uC_API_VALID) {
		UC_API_VALID = uC_API_VALID;
	}

	public String getUC_API_GETUSER() {
		return UC_API_GETUSER;
	}

	public void setUC_API_GETUSER(String uC_API_GETUSER) {
		UC_API_GETUSER = uC_API_GETUSER;
	}

	public String getUC_API_GETUSERROLES() {
		return UC_API_GETUSERROLES;
	}

	public void setUC_API_GETUSERROLES(String uC_API_GETUSERROLES) {
		UC_API_GETUSERROLES = uC_API_GETUSERROLES;
	}

	public String getUC_API_ACCESS_USERNAME() {
		return UC_API_ACCESS_USERNAME;
	}

	public void setUC_API_ACCESS_USERNAME(String uC_API_ACCESS_USERNAME) {
		UC_API_ACCESS_USERNAME = uC_API_ACCESS_USERNAME;
	}

	public String getUC_API_ACCESS_PASSWORD() {
		return UC_API_ACCESS_PASSWORD;
	}

	public void setUC_API_ACCESS_PASSWORD(String uC_API_ACCESS_PASSWORD) {
		UC_API_ACCESS_PASSWORD = uC_API_ACCESS_PASSWORD;
	}

	public String getUC_API_SERVER_LOGIN() {
		return UC_API_SERVER_LOGIN;
	}

	public void setUC_API_SERVER_LOGIN(String uC_API_SERVER_LOGIN) {
		UC_API_SERVER_LOGIN = uC_API_SERVER_LOGIN;
	}

	public String getUC_API_SERVER_REFRESHTOKEN() {
		return UC_API_SERVER_REFRESHTOKEN;
	}

	public void setUC_API_SERVER_REFRESHTOKEN(String uC_API_SERVER_REFRESHTOKEN) {
		UC_API_SERVER_REFRESHTOKEN = uC_API_SERVER_REFRESHTOKEN;
	}

	public String getUC_API_BEARERTOKEN_VALID() {
		return UC_API_BEARERTOKEN_VALID;
	}

	public void setUC_API_BEARERTOKEN_VALID(String uC_API_BEARERTOKEN_VALID) {
		UC_API_BEARERTOKEN_VALID = uC_API_BEARERTOKEN_VALID;
	}
	
	/**
	 * 初始化各个配置参数
	 */
	private void initUCParams(){
		if (!StringUtils.isEmpty(this.getUC_API_DOMAIN())) {
			if (!StringUtils.isEmpty(this.getUC_API_VERSION())) {
				WafProperties.getProperties().setProperty(WafContext.WAF_UC_URI, this.getUC_API_DOMAIN()+this.getUC_API_VERSION()+"/");
			}else {
				WafProperties.getProperties().setProperty(WafContext.WAF_UC_URI, this.getUC_API_DOMAIN()+ UC_API_VERSION +"/");
			}
		}
		if (!StringUtils.isEmpty(this.getUC_API_ACCESS_USERNAME())) {
			WafProperties.getProperties().setProperty(WafBearerTokenServiceImpl.WAF_CLIENT_BEARER_TOKEN_ACCOUNT_USERNAME, this.getUC_API_ACCESS_USERNAME());
		}
		if (!StringUtils.isEmpty(this.getUC_API_ACCESS_PASSWORD())) {
			WafProperties.getProperties().setProperty(WafBearerTokenServiceImpl.WAF_CLIENT_BEARER_TOKEN_ACCOUNT_PASSWORD, this.getUC_API_ACCESS_PASSWORD());
		}
	}
}
