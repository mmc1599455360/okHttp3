package com.example.http.model;

import com.alibaba.fastjson.JSONObject;
import com.example.http.utils.JsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 接口返回信息
 */
@Data
@SuppressWarnings("rawtypes")
public class ResultModel {

	private boolean result = true;

	/*** 返回消息编码，0：成功，其他失败 */
	private String code;

	/*** 返回消息描述 */
	private String message;

	/*** 数据内容 */
	private String data;

	public ResultModel() {
		super();
	}

	public ResultModel(String code, String message) {
		super();
		this.code = code;
		this.message = message;
		if ("0".equals(code)) {
			this.result = true;
		}
		else {
			this.result = false;
		}
	}

	public ResultModel(String code, String message, String data) {
		super();
		this.code = code;
		this.message = message;
		if ("0".equals(code)) {
			this.result = true;
		}
		else {
			this.result = false;
		}
		if (StringUtils.isNotBlank(data)) {
			this.data = data;
		}
		else {
			this.data = "";
		}
	}

	public ResultModel(String code, String message, List data) {
		super();
		this.code = code;
		this.message = message;
		if ("0".equals(code)) {
			this.result = true;
		}
		else {
			this.result = false;
		}
		this.data = JsonUtils.toJson(data).toString();
	}

	public ResultModel(String code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		if ("0".equals(code)) {
			this.result = true;
		}
		else {
			this.result = false;
		}
		if(data != null){
			this.data = JSONObject.toJSONString(data);
		}
	}
}
