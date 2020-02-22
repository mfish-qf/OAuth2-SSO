package com.qf.sso.webOAuth2;

import com.qf.sso.core.dao.SSOUserDao;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qiufeng
 * @date 2020/2/14 10:46
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {
    @Autowired
    SSOUserDao userDao;
    @Autowired
    UserService userService;

    @Test
    public void testUserDao() {
        SSOUser user = userDao.getUserByAccount("18952006692");
        user = userDao.getUserByAccount("qiufeng9862@qq.com");
        System.out.println(user);
    }

    @Test
    public void changePwd(){
        userService.changePassword("6870e0fe-fa79-468c-86d9-e963e9b5c43f","!QAZ2wsx");
    }

}
