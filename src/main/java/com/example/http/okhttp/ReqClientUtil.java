package com.example.http.okhttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.http.model.ResultModel;
import com.example.http.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReqClientUtil {
    private static AbstractApiJsonClient client = new AbstractApiJsonClient();

    /**get请求返回json格式**/
    public static JSONObject callGet(String url) {
        ResponseBody respBody = null;
        try {
            Response response = client.getResponse(url, "get", null, null,
                    null);
            respBody = response.body();
            if (response.isSuccessful()) {
                return JSON.parseObject(respBody.bytes(), JSONObject.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (respBody != null) {
                respBody.close();
            }
        }
    }


   /***不包含请求头的post*/
    public static JSONObject callPost(String url,
                                      Map<String, Object> queryParams, Object body) {
        Response response = client.getResponse(url, "post", null, queryParams,
                body);
        return getJsonObject(response);
    }

    /***包含请求头的post*/
    public static JSONObject callPostContainHeader(String url, Map<String, String> headerMap, Map<String, Object> queryParams, Object body) {
        Response response = client.getResponse(url, "post", headerMap, queryParams,
                body);
        return getJsonObject(response);
    }


    /**
     * 封装获取响应结果
     **/
    private static JSONObject getJsonObject(Response response) {
        ResponseBody respBody = response.body();
        try {
            if (response.isSuccessful()) {
                return JSON.parseObject(respBody.bytes(), JSONObject.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (respBody != null) {
                respBody.close();
            }
        }
    }



    /**
     * 指定返回类的post
     */
    public static <S> S post(String url, Map<String, String> headerMap, Map<String, Object> queryParams, Object body,
                             Class<S> clazz) {
        Response response = null;
        try {
            response = client.getResponse(url, "post", headerMap, queryParams, body);
        } catch (Exception e) {
            log.error("文件服务器异常." + e.getMessage(), e);
            return null;
        }
        return convertBody(clazz, response);
    }

    private static <S> S convertBody(Class<S> clazz, Response response) {
        ResponseBody respBody = null;
        try {
            respBody = response.body();
            if (respBody == null) {
                return null;
            }
            String body = respBody.string();
            log.info("body: {}", body);
            if (response.isSuccessful()) {
                return JsonUtils.parse(body, clazz);
            } else {
                log.error(response.message());
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error(response.message() + "||" + e.toString());
            return null;
        } finally {
            if (respBody != null) {
                respBody.close();
            }
        }
    }


    /**get请求返回字符串格式**/
    public static String callGetForString(String url) {
        ResponseBody respBody = null;
        try {
            Response response = client.getResponse(url, "get", null, null,
                    null);
            respBody = response.body();
            return getStringResponse(respBody, response);
        } catch (Exception e) {
            return null;
        } finally {
            if (respBody != null) {
                respBody.close();
            }
        }
    }

    private static String getStringResponse(ResponseBody respBody, Response response) throws IOException {
        if (response.isSuccessful()) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(respBody.byteStream(), "UTF-8"));
            String buffer = null;
            StringBuffer str = new StringBuffer();
            while ((buffer = br.readLine()) != null) {
                str.append(buffer);
            }
            return str.toString();
        } else {
            return null;
        }
    }

    /**指定类型的post请求 **/
    public static String callByType(String url, String body, MediaType type) {
        ResponseBody respBody = null;
        try {
            Request request = new Request.Builder().url(url)
                    .post(RequestBody.create(type, body)).build();
            Response response = client.getResponse(request);
            respBody = response.body();
            return getStringResponse(respBody, response);
        } catch (Exception e) {
            return null;
        } finally {
            if (respBody != null) {
                respBody.close();
            }
        }
    }

    /**  返回数组集合模式
     * 用于查询请求
     * url ： 请求地址
     * ata ：请求查询参数
     * clazz ：返回查询结果模板
     */
    public static <T> List<T> search(String url, Object data, Class<T> clazz) {
        ResultModel model = ReqResultModel(url, data);
        String str = JSONObject.toJSONString(model.getData());
        return JSONArray.parseArray(str, clazz);
    }

    /** 返回类型模式
     * 业务操作请求 url ： 请求地址 data ：请求参数
     */
    public static String callPost(String url, Object data) {
        try {
            log.info("访问路径：{}", url);
            log.info("请求参数：{}", JSONObject.toJSONString(data));
            ResultModel model = ReqResultModel(url, data);
            return model.getMessage();
        } catch (Exception e) {
            log.error("访问路径：{}", url);
            log.error("请求参数：{}", JSONObject.toJSONString(data));
            throw e;
        }
    }
    private static ResultModel ReqResultModel(String url, Object data) {
        JSONObject body = callPost(url, null, data);
        if (body == null) {
            throw new IllegalArgumentException("请求返回为空，请检查网络！");
        }
        ResultModel model = body.toJavaObject(ResultModel.class);
        if (!model.isResult()) {
            throw new IllegalArgumentException(model.getMessage());
        }
        return model;
    }

}
