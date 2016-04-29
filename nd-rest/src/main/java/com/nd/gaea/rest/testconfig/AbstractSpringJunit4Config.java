package com.nd.gaea.rest.testconfig;

/**
 * 
* @ClassName: AbstractSpringJunit4Config 
* @Description: 用户id和realm自定义抽象类
* @author Eric
* @date 2015年2月15日 下午1:40:50 
*
 */
public abstract class AbstractSpringJunit4Config extends BaseSpringJunit4Config {

	@Override
	public void setUp() {
		this.initRealm();
		this.initUserId();
		super.setUp();
	}
	
	/**
	 * 
	* @Title: initRealm 
	* @Description: 初始化用户领域
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	 */
	protected abstract void initRealm();
	
	/**
	 * 
	* @Title: initUserId 
	* @Description: 初始化用户id
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	 */
	protected abstract void initUserId();
	
}
