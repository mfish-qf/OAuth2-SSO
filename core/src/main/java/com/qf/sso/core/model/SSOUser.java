package com.qf.sso.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author qiufeng
 * @date 2020/2/13 17:29
 */
@ApiModel("用户基础信息")
@Data
@EqualsAndHashCode(callSuper=true)
public class SSOUser extends BaseEntity<String>{
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("旧密码")
    private String oldPassword;
    @ApiModelProperty("昵称--用于显示")
    private String nickname;
    @ApiModelProperty("头像")
    private String headImgUrl;
    @ApiModelProperty("电话")
    private String telephone;
    @ApiModelProperty("生日")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date birthday;
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("状态 0删除 1正常 2禁用")
    private Integer status;
    @ApiModelProperty("密码加密盐")
    private String salt;
}
