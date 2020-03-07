package com.qf.sso.webOAuth2.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.MyRuntimeException;
import com.qf.sso.core.model.QRCode;
import com.qf.sso.core.model.QRCodeImg;
import com.qf.sso.core.model.RedisQrCode;
import com.qf.sso.core.service.QRCodeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Hashtable;
import java.util.UUID;

/**
 * @author qiufeng
 * @date 2020/3/5 14:54
 */
@RestController
@Slf4j
@RequestMapping("/qrCodeLogin")
public class QRCodeController {
    @Resource
    QRCodeService qrCodeService;

    @ApiOperation("生成二维码")
    @GetMapping("/build")
    public QRCodeImg buildQRCode() {
        String error = "错误:生成二维码异常!";
        try {
            Hashtable<EncodeHintType, Comparable> hints = new Hashtable<>();
            //指定二维码字符集编码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置二维码纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            //设置图片边距
            hints.put(EncodeHintType.MARGIN, 2);
            String code = UUID.randomUUID().toString();
            BitMatrix matrix = new MultiFormatWriter().encode(code,
                    BarcodeFormat.QR_CODE, 250, 250, hints);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            if (ImageIO.write(bufferedImage, "png", byteArrayOutputStream)) {
                RedisQrCode qrCode = saveQRCode(code);
                return buildResponseCode(qrCode, byteArrayOutputStream);
            }
            throw new MyRuntimeException(error);
        } catch (WriterException | IOException e) {
            log.error(error, e);
            throw new MyRuntimeException(error);
        }
    }

    /**
     * 保存二维码
     *
     * @param code
     * @return
     */
    private RedisQrCode saveQRCode(String code) {
        RedisQrCode qrCode = new RedisQrCode();
        qrCode.setCode(code);
        qrCode.setStatus(SerConstant.ScanStatus.未扫描.toString());
        qrCodeService.saveQRCode(qrCode);
        return qrCode;
    }

    /**
     * 构建返回code
     * @param qrCode
     * @param byteArrayOutputStream
     * @return
     */
    private QRCodeImg buildResponseCode(QRCode qrCode, ByteArrayOutputStream byteArrayOutputStream) {
        QRCodeImg qrCodeImg = new QRCodeImg();
        qrCodeImg.setImg("data:image/png;base64," + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        qrCodeImg.setCode(qrCode.getCode());
        qrCodeImg.setStatus(qrCode.getStatus());
        return qrCodeImg;
    }

    @ApiOperation("检测扫码登录状态")
    @GetMapping("/check")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true)
    })
    public QRCode qrCodeLoginCheck(String code) throws InvocationTargetException, IllegalAccessException {
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(code);
        if (redisQrCode == null) {
            return null;
        }
        QRCode qrCode = new QRCode();
        BeanUtils.copyProperties(qrCode, redisQrCode);
        return qrCode;
    }
}
