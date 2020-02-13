package com.qf.sso.core.realm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/10 19:21
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MultipleRealm extends ModularRealmAuthenticator {
    private Map<String, Object> myRealms;
    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        this.assertRealmsConfigured();
        Realm realm = null;
        MyUsernamePasswordToken token = (MyUsernamePasswordToken) authenticationToken;
        switch (token.getLoginType()) {
            case 一次一密:
                realm = (Realm) this.myRealms.get("smsAuthRealm");
                break;
            case 扫码:
                realm = (Realm) this.myRealms.get("qrCodeRealm");
                break;
            default:
                realm = (Realm) this.myRealms.get("userRealm");
                break;
        }
        return super.doSingleRealmAuthentication(realm, authenticationToken);
    }
}
