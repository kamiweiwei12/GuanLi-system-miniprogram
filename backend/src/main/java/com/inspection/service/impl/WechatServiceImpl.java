package com.inspection.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspection.exception.BusinessException;
import com.inspection.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 微信服务实现
 * 封装微信小程序 jscode2session API 调用
 */
@Service
public class WechatServiceImpl implements WechatService {

    private static final Logger log = LoggerFactory.getLogger(WechatServiceImpl.class);

    private static final String WX_API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WechatServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getOpenid(String code) {
        if (appId == null || appId.isBlank()) {
            throw new BusinessException(500, "微信 AppID 未配置，请设置环境变量 WX_APP_ID");
        }
        if (appSecret == null || appSecret.isBlank()) {
            throw new BusinessException(500, "微信 AppSecret 未配置，请设置环境变量 WX_APP_SECRET");
        }

        String url = WX_API_URL
                + "?appid=" + appId
                + "&secret=" + appSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();

            if (body == null) {
                throw new BusinessException(500, "微信 API 返回空响应");
            }

            JsonNode json = objectMapper.readTree(body);

            // 错误处理
            if (json.has("errcode")) {
                int errcode = json.get("errcode").asInt();
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "未知错误";
                log.warn("微信 API 返回错误: errcode={}, errmsg={}", errcode, errmsg);
                throw new BusinessException(401, "微信登录失败: " + errmsg + " (errcode=" + errcode + ")");
            }

            // 成功获取 openid
            if (json.has("openid")) {
                String openid = json.get("openid").asText();
                log.info("微信登录成功: openid={}", openid.substring(0, Math.min(8, openid.length())) + "***");
                return openid;
            }

            throw new BusinessException(500, "微信 API 返回数据中缺少 openid");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信 API 异常", e);
            throw new BusinessException(500, "调用微信接口失败，请稍后重试");
        }
    }
}