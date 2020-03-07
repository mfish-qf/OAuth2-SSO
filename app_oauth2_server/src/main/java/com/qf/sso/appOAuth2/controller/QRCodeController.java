package com.qf.sso.appOAuth2.controller;

import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.appOAuth2.validator.WeChatTokenValidator;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.RedisQrCode;
import com.qf.sso.core.service.QRCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author qiufeng
 * @date 2020/3/5 17:07
 */
@Api(tags = "扫码登录接口")
@RestController
@RequestMapping("/qrCodeLogin")
public class QRCodeController {
    @Resource
    WeChatTokenValidator weChatTokenValidator;
    @Resource
    QRCodeService qrCodeService;

    @ApiOperation("扫描二维码登录")
    @PostMapping("/scan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true)
    })
    public CheckWithResult<String> scanQrCode(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.未扫描, SerConstant.ScanStatus.已扫描);
    }

    @ApiOperation("扫码确认登录")
    @PostMapping("/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.QR_SECRET, value = "前一次扫码返回的秘钥", paramType = "query", required = true)
    })
    public CheckWithResult<String> qrCodeLogin(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.已扫描, SerConstant.ScanStatus.已确认);
    }

    @ApiOperation("扫码取消登录")
    @PostMapping("/cancel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.QR_SECRET, value = "前一次扫码返回的秘钥", paramType = "query", required = true)
    })
    public CheckWithResult<String> qrCodeCancel(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.已扫描, SerConstant.ScanStatus.已取消);
    }

    /**
     * 扫码登录操作
     *
     * @param request
     * @param origStatus
     * @param destStatus
     * @return
     */
    private CheckWithResult<String> qrCodeOperator(HttpServletRequest request, SerConstant.ScanStatus origStatus, SerConstant.ScanStatus destStatus) {
        CheckWithResult<WeChatToken> result = weChatTokenValidator.validate(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        String code = request.getParameter(SerConstant.QR_CODE);
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(code);
        if (redisQrCode == null) {
            return new CheckWithResult<String>().setSuccess(false).setMsg("错误:code不正确");
        }
        if (!StringUtils.isEmpty(redisQrCode.getAccessToken())
                && !result.getResult().getAccess_token().equals(redisQrCode.getAccessToken())) {
            return new CheckWithResult<String>().setSuccess(false).setMsg("错误:两次请求token不相同");
        }
        if (!StringUtils.isEmpty(redisQrCode.getSecret())) {
            String secret = request.getParameter(SerConstant.QR_SECRET);
            if (!redisQrCode.getSecret().equals(secret)) {
                return new CheckWithResult<String>().setSuccess(false).setMsg("错误:传入秘钥不正确");
            }
        }
        if (origStatus.toString().equals(redisQrCode.getStatus())) {
            redisQrCode.setStatus(destStatus.toString());
            redisQrCode.setAccessToken(result.getResult().getAccess_token());
            redisQrCode.setAccount(result.getResult().getAccount());
            redisQrCode.setSecret(UUID.randomUUID().toString());
            qrCodeService.updateQRCode(redisQrCode);
            return new CheckWithResult<String>().setMsg("操作成功").setResult(redisQrCode.getSecret());
        }
        return new CheckWithResult<String>().setSuccess(false).setMsg("错误:二维码状态不正确");
    }
}
