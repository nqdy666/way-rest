package com.nd.gaea.rest.security.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.services.WafUserDetailsService;

/**
 * 角色对象代理类，提供延迟加载服务
 * @author 110825
 * @since 0.9.5
 * @param <E>
 */
class UserCenterRoleDetailsListProxy<E> implements List<E> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	List<UserCenterRoleDetails> userCenterRoleDetailsList;
	
	private boolean flag;
	private String userId;
	private String realm;
	private WafUserDetailsService wafUserDetailsService;
	
	public UserCenterRoleDetailsListProxy(String userId, String realm, WafUserDetailsService wafUserDetailsService){
		this.userId = userId;
		this.realm = realm;
		this.wafUserDetailsService = wafUserDetailsService;
	}
	
	/**
	 * 加载角色信息
	 */
	public void loadUserRoles(){
		//发送获取userInfo的http请求
		userCenterRoleDetailsList = wafUserDetailsService.getUserRoleList(userId, realm);
		if(userCenterRoleDetailsList==null){
			logger.error("userId:{}, realm:{} get user role from uc is return null", userId, realm);
			userCenterRoleDetailsList = new ArrayList<UserCenterRoleDetails>();
		}
		flag = true;
	}

	@Override
	public Iterator<E> iterator() {
		if(!flag){
			this.loadUserRoles();
		}
		return (Iterator<E>) userCenterRoleDetailsList.iterator();
	}
	
	@Override
	public boolean add(E e) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.add((UserCenterRoleDetails) e);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.addAll((Collection<? extends UserCenterRoleDetails>) c);
	}
	
	@Override
	public int size() {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.size();
	}

	@Override
	public boolean isEmpty() {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.contains(o);
	}

	@Override
	public Object[] toArray() {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.retainAll(c);
	}

	@Override
	public void clear() {
		if(!flag){
			this.loadUserRoles();
		}
		userCenterRoleDetailsList.clear();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.addAll(index, (Collection<? extends UserCenterRoleDetails>) c);
	}

	@Override
	public E get(int index) {
		if(!flag){
			this.loadUserRoles();
		}
		return (E) userCenterRoleDetailsList.get(index);
	}

	@Override
	public E set(int index, E element) {
		if(!flag){
			this.loadUserRoles();
		}
		return (E) userCenterRoleDetailsList.set(index, (UserCenterRoleDetails) element);
	}

	@Override
	public void add(int index, E element) {
		if(!flag){
			this.loadUserRoles();
		}
		userCenterRoleDetailsList.add(index, (UserCenterRoleDetails) element);
	}

	@Override
	public E remove(int index) {
		if(!flag){
			this.loadUserRoles();
		}
		return (E) userCenterRoleDetailsList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		if(!flag){
			this.loadUserRoles();
		}
		return userCenterRoleDetailsList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		if(!flag){
			this.loadUserRoles();
		}
		return (ListIterator<E>) userCenterRoleDetailsList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if(!flag){
			this.loadUserRoles();
		}
		return (ListIterator<E>) userCenterRoleDetailsList.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		if(!flag){
			this.loadUserRoles();
		}
		return (List<E>) userCenterRoleDetailsList.subList(fromIndex, toIndex);
	}
}
