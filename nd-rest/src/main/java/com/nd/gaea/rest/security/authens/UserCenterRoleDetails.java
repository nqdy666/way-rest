/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.authens;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
/**
 * 扩展自GrantAuthority对象，处了获取角色外，封装UC返回的角色相关的信息内容
 * @author johnny
 *
 */
public class UserCenterRoleDetails implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7243022331854420825L;
	/**
	 * 角色信息id
	 */
	private String roleId;
	/**
	 * 角色名称,UC中，角色的名称是小写的英文单词，而且是唯一的，所以在鉴权过程中主要以name为主
	 */
	private String roleName;
	/**
	 * 权限所属业务领域
	 */
	private String realm;
	/**
	 * 角色修改时间
	 */
	private Date updatedAt;
	/**
	 * 1,--权限扩展 0-NONE,1-READ,2-ADD,3-WRITE,4-DELETE
	 */
	private int authExtra=0;
	
	/**
	 * 返回系统的角色编号
	 */
	@Override
	public String getAuthority() {
		return this.roleName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getAuthExtra() {
		return authExtra;
	}

	public void setAuthExtra(int authExtra) {
		this.authExtra = authExtra;
	}


	
}
