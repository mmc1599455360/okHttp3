package com.example.http.okhttp;

import com.alibaba.fastjson.JSON;
import com.example.http.utils.JsonUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class AbstractApiJsonClient {
	protected static final MediaType MEDIA_TYPE_JSON = MediaType
			.parse("application/json; charset=utf-8");
	protected static OkHttpClient client = null;
	private int readTimeout = 3000;
	private int writeTimeout = 3000;
	private int connectTimeout = 3000;

	@SneakyThrows
	protected <T, S> S call(@NonNull String url, @NonNull T body,
                            Class<S> clazz) {

		String postBody = "";

		// todo body处理逻辑
		if (StringUtils.isBlank(postBody)) {
			postBody = JSON.toJSONString(body);
		}
		Request request = new Request.Builder().url(url)
				.post(RequestBody.create(MEDIA_TYPE_JSON, postBody)).build();
		log.debug("postBody: {}", postBody);
		log.debug("connectTimeoutMillis: {}", client.connectTimeoutMillis());
		log.debug("readTimeoutMillis: {}", client.readTimeoutMillis());
		log.debug("writeTimeoutMillis: {}", client.writeTimeoutMillis());
		val response = client.newCall(request).execute();
		val respBody = response.body();
		try {
			if (response.isSuccessful()) {
				return JSON.parseObject(respBody.bytes(), clazz);
			}
			else {
				log.error("error, {}", response.message());
				return null;
			}
		}
		catch (Exception e) {
			log.error(response.message() + "||" + e.toString());
			return null;
		}
		finally {
			if (respBody != null) {
				respBody.close();
			}
		}
	}

	@SneakyThrows
	public Response getResponse(String url, String methodType,
                                Map<String, String> headerMap, Map<String, Object> queryParams,
                                Object body) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("未指定url");
		}
		String postBody = "";
		if (StringUtils.isBlank(postBody) && body != null) {
			postBody = JsonUtils.toJson(body);
		}
		log.debug("接口请求参数: {}", postBody);
		Headers headers;
		if (headerMap == null) {
			headers = new Headers.Builder().build();
		}
		else {
			headers = Headers.of(headerMap);
		}

		// url添加 QUERY PARAMETERS
		HttpUrl httpUrl = HttpUrl.parse(url);
		if (httpUrl == null) {
			throw new IllegalArgumentException("url错误");
		}
		HttpUrl.Builder builder = httpUrl.newBuilder();
		/**根据请求头添加参数信息 **/
		if (MapUtils.isNotEmpty(queryParams)) {
			queryParams.forEach((o, o2) -> builder.addQueryParameter(o,
					Optional.ofNullable(o2).orElse("").toString()));
		}

		Request request;

		if ("get".equalsIgnoreCase(methodType)) {
			httpUrl = builder.build();
			request = new Request.Builder().url(httpUrl).headers(headers).get()
					.build();
		} else if ("post".equalsIgnoreCase(methodType)) {
			httpUrl = builder.build();
			request = new Request.Builder().url(httpUrl).headers(headers)
					.post(RequestBody.create(MEDIA_TYPE_JSON, postBody))
					.build();
		}
		else {
			return null;
		}
		if (client == null) {
			init();
		}
		log.debug("-X {} -H {} -d {} {}", methodType, request.headers(),
				postBody, request.url());
		return client.newCall(request).execute();
	}

	@SneakyThrows
	public Response getResponse(Request request) {
		if (client == null) {
			init();
		}
		return client.newCall(request).execute();
	}

	@SuppressWarnings("deprecation")
	@PostConstruct
	public void init() {
		client = new OkHttpClient.Builder()
				/**读取写入 链接超时时间 3 秒**/
				.readTimeout(readTimeout, TimeUnit.SECONDS)
				.writeTimeout(writeTimeout, TimeUnit.SECONDS)
				.connectTimeout(connectTimeout, TimeUnit.SECONDS)
				/**验证正式factory 信任的证书**/
				.sslSocketFactory(SSLSocketClientUtils.getSSLSocketFactory())
				.hostnameVerifier(SSLSocketClientUtils.getHostnameVerifier())
				.build();
	}
}
