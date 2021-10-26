package com.qf.sso.core.common;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/4/29 10:49
 */
@Slf4j
public class HttpRequestUtil {
    /**
     * 发送post请求
     *
     * @param url
     * @param contentType
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> postMessage(String url, ContentType contentType) {
        return postMessage(url, "", null, contentType);
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param param
     * @param contentType
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> postMessage(String url, String param, ContentType contentType) {
        return postMessage(url, param, null, contentType);
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param param
     * @param host
     * @param contentType
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> postMessage(String url, String param, HttpHost host, ContentType contentType) {
        HttpPost post = new HttpPost(url);
        if (!StringUtils.isEmpty(param)) {
            StringEntity entity = new StringEntity(param, contentType);
            post.setEntity(entity);
        }
        return executeRequest(post, HttpType.getHttpType(url), host);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> getMessage(String url) {
        return getMessage(url, "");
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param param
     * @param host
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> getMessage(String url, String param, HttpHost host) {
        if (!StringUtils.isEmpty(param)) {
            if (url.indexOf('?') >= 0) {
                url += "&" + param;
            } else {
                url += "?" + param;
            }
        }
        HttpType httpType = HttpType.getHttpType(url);
        if (httpType == HttpType.unknown) {
            return new CheckWithResult<T>().setSuccess(false).setMsg("错误的请求链接");
        }
        HttpGet get = new HttpGet(url);
        return executeRequest(get, httpType, host);
    }

    /**
     * get请求
     *
     * @param url
     * @param host
     * @return
     */
    public static <T> CheckWithResult<T> getMessage(String url, HttpHost host) {
        return getMessage(url, "", host);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param param
     * @param <T>
     * @return
     */
    public static <T> CheckWithResult<T> getMessage(String url, String param) {
        return getMessage(url, param, null);
    }

    /**
     * 处理请求结果
     *
     * @param request
     * @param httpType
     * @return
     */
    private static <T> CheckWithResult<T> executeRequest(HttpRequestBase request, HttpType httpType, HttpHost host) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            switch (httpType) {
                case https:
                    client = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).build();
                    break;
                case http:
                    client = HttpClients.createDefault();
                    break;
                default:
                    return new CheckWithResult<T>().setSuccess(false).setMsg("错误:未知的请求类型");
            }
            RequestConfig requestConfig;
            RequestConfig.Builder builder = RequestConfig.custom()
                    .setConnectTimeout(60000).setConnectionRequestTimeout(60000)
                    .setSocketTimeout(60000);
            if (host == null) {
                requestConfig = builder.build();
            } else {
                requestConfig = builder.setProxy(host).build();
            }
            request.setConfig(requestConfig);
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String returnValue = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //encode防止json注入
                byte[] bytes = JsonStringEncoder.getInstance().encodeAsUTF8(returnValue);
                ObjectMapper objectMapper = new ObjectMapper();
                return new CheckWithResult<T>().setResult(objectMapper.readValue(bytes, new TypeReference<T>() {
                }));
            } else {
                String error = "错误:请求出错!错误代码:" + response.getStatusLine().getStatusCode();
                log.error(error);
                return new CheckWithResult<T>().setMsg(error).setSuccess(false);
            }
        } catch (IOException ex) {
            log.error("异常:请求异常", ex);
            return new CheckWithResult<T>().setMsg(ex.getMessage()).setSuccess(false);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("错误:请求关闭异常", e);
            }
        }
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslSf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null
                    , (TrustStrategy) (chain, authType) -> true).build();
            sslSf = new SSLConnectionSocketFactory(sslContext);
        } catch (GeneralSecurityException e) {
            log.error("创建ssl安全连接异常!", e);
            throw new RuntimeException(e);
        }
        return sslSf;
    }


    /**
     * http类型枚举
     */
    public enum HttpType {
        http(0),
        https(1),
        unknown(2);

        private int value;

        HttpType(int value) {
            this.setValue(value);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.name();
        }

        /**
         * 根据url获取请求类型
         *
         * @param url
         * @return
         */
        public static HttpType getHttpType(String url) {
            if (url.startsWith(HttpType.https.toString())) {
                return HttpType.https;
            } else if (url.startsWith(HttpType.http.toString())) {
                return HttpType.http;
            }
            return HttpType.unknown;
        }
    }
}