package com.qf.sso.core.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author qiufeng
 * @date 2020/2/12 13:47
 */
@Slf4j
public class Utils {
    /**
     * 从请求中获取token值
     * token通过access_token=****直接赋值
     * 或者token放到head中 以Authorization=Bearer******方式传入
     *
     * @param request
     * @return
     */
    public static String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter(OAuth.OAUTH_ACCESS_TOKEN);
        // 请求参数中包含access_token参数
        if (StringUtils.isEmpty(accessToken)) {
            // 头部的Authorization值以Bearer开头
            String auth = request.getHeader(OAuth.HeaderType.AUTHORIZATION);
            if (auth != null && auth.startsWith(OAuth.OAUTH_HEADER_NAME)) {
                accessToken = auth.replace(OAuth.OAUTH_HEADER_NAME, "").trim();
            }
        }
        return accessToken;
    }

    /**
     * 获取请求用户IP
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inetAddress.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            log.info(ip);
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
