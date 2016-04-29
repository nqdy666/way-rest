package com.nd.gaea.rest.testconfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.UnsupportedEncodingException;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

public class MockUtil {
	
	public static final String APPLICATION_JSON = "application/json;charset=utf-8";

	/**
	 * 
	 * @Title: mockCreate
	 * @Description: 测试创建接口
	 * @param @param mvc
	 * @param @param uri
	 * @param @param json
	 * @param @return
	 * @param @throws UnsupportedEncodingException
	 * @param @throws Exception 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String mockCreate(MockMvc mvc, String uri, String json)
			throws UnsupportedEncodingException, Exception {
		return mockPost(mvc,uri,json);
	}
	
	/**
	 * 
	* @Title: mockPost 
	* @Description: 用于处理post请求
	* @param @param mvc
	* @param @param uri
	* @param @param json
	* @param @return
	* @param @throws UnsupportedEncodingException
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String mockPost(MockMvc mvc, String uri, String json)
			throws UnsupportedEncodingException, Exception {
		
		return mvc
				.perform(
						post(uri, "json").characterEncoding("UTF-8")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.valueOf(APPLICATION_JSON))
						.content(checkContent(json).getBytes())).andReturn()
				.getResponse().getContentAsString();
	}

	/**
	 * 
	 * @Title: mockGet
	 * @Description: 测试get接口
	 * @param @param mvc
	 * @param @param uri
	 * @param @param json
	 * @param @return
	 * @param @throws UnsupportedEncodingException
	 * @param @throws Exception 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String mockGet(MockMvc mvc, String uri, String json)
			throws UnsupportedEncodingException, Exception {
		return mvc
				.perform(
						get(uri, "json").characterEncoding("UTF-8")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.valueOf(APPLICATION_JSON))
						.content(checkContent(json).getBytes())).andReturn()
				.getResponse().getContentAsString();
	}

	/**
	 * 
	* @Title: mockPut 
	* @Description: 测试put接口
	* @param @param mvc
	* @param @param uri
	* @param @param json
	* @param @return
	* @param @throws UnsupportedEncodingException
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String mockPut(MockMvc mvc, String uri, String json)
			throws UnsupportedEncodingException, Exception {
		return mvc
				.perform(
						put(uri, "json").characterEncoding("UTF-8")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.valueOf(APPLICATION_JSON))
						.content(checkContent(json).getBytes())).andReturn()
				.getResponse().getContentAsString();
	}
	
	/**
	 * 
	* @Title: mockDelete 
	* @Description: 测试delete接口 
	* @param @param mvc
	* @param @param uri
	* @param @param json
	* @param @return
	* @param @throws UnsupportedEncodingException
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String mockDelete(MockMvc mvc, String uri, String json)
			throws UnsupportedEncodingException, Exception {
		return mvc
				.perform(
						delete(uri, "json").characterEncoding("UTF-8")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.valueOf(APPLICATION_JSON))
						.content(checkContent(json).getBytes())).andReturn()
				.getResponse().getContentAsString();
	}
	
	/**
	 * 
	* @Title: checkContent 
	* @Description: 参数验证
	* @param @param jsonContent
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	protected static String checkContent(String jsonContent){
		if (StringUtils.isEmpty(jsonContent)) {
			jsonContent = "";
		}
		return jsonContent;
	}

}
