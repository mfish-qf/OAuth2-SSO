package com.qf.sso.core.common;

import com.alibaba.fastjson.JSON;
import com.qf.sso.core.exception.MyRuntimeException;
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
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;


/**
 * @author qiufeng
 * @date 2020/2/28 12:56
 */
@Slf4j
public class HttpRequestUtil {
    /**
     * 发送post请求
     * @param url
     * @param contentType
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> postMessage(String url, ContentType contentType) throws IOException {
        return postMessage(url, "", null, contentType);
    }

    /**
     * 发送post请求
     * @param url
     * @param param
     * @param contentType
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> postMessage(String url, String param, ContentType contentType) throws IOException {
        return postMessage(url, param, null, contentType);
    }

    /**
     * 发送post请求
     * @param url
     * @param param
     * @param host
     * @param contentType
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> postMessage(String url, String param, HttpHost host, ContentType contentType) throws IOException {
        HttpPost post = new HttpPost(url);
        if (!StringUtils.isEmpty(param)) {
            StringEntity entity = new StringEntity(param, contentType);
            post.setEntity(entity);
        }
        return executeRequest(post, HttpType.getHttpType(url), host);
    }

    /**
     * 发送get请求
     * @param url
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> getMessage(String url) throws IOException {
        return getMessage(url, "");
    }

    /**
     * 发送get请求
     * @param url
     * @param param
     * @param host
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> getMessage(String url, String param, HttpHost host) throws IOException {
        if (!StringUtils.isEmpty(param)) {
            if (url.indexOf('?') >= 0) {
                url += "&" + param;
            } else {
                url += "?" + param;
            }
        }
        HttpGet get = new HttpGet(url);
        return executeRequest(get, HttpType.getHttpType(url), host);
    }

    /**
     * get请求
     *
     * @param url
     * @param host
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> getMessage(String url, HttpHost host) throws Exception {
        return getMessage(url, "", host);
    }

    /**
     * 发送get请求
     * @param url
     * @param param
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> CheckWithResult<T> getMessage(String url, String param) throws IOException {
        return getMessage(url, param, null);
    }

    /**
     * 处理请求结果
     *
     * @param request
     * @param httpType
     * @return
     * @throws Exception
     */
    private static <T> CheckWithResult<T> executeRequest(HttpRequestBase request, HttpType httpType, HttpHost host) throws IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            switch (httpType) {
                case https:
                    client = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).build();
                    break;
                default:
                    client = HttpClients.createDefault();
                    break;
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
                return new CheckWithResult().setResult(JSON.parseObject(returnValue));
            } else {
                String error = "错误:请求出错!错误代码:" + response.getStatusLine().getStatusCode();
                log.error(error);
                return new CheckWithResult<T>().setMsg(error).setSuccess(false);
            }
        } catch (IOException ex) {
            log.error("异常:请求异常", ex);
            return new CheckWithResult<T>().setMsg(ex.getMessage()).setSuccess(false);
        } finally {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
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
            throw new MyRuntimeException(e);
        }
        return sslSf;
    }



    /**
     * http类型枚举
     */
    public enum HttpType {
        http(0),
        https(1);
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
            }
            return HttpType.http;
        }
    }
}
