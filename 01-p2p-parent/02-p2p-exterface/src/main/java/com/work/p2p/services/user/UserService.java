package com.work.p2p.services.user;

import com.work.p2p.beans.user.User;

public interface UserService {

    //查询平台的用户数量
   Integer qurryAllUserCount();
    //根据手机号返回user对象
    User queryUserByPhone(String phone);
    //注册用户
    //因为注册用户使得用户表和账户表都要修改，所以不使用addxxxx作为方法名而是根据业务给方法命名
    User register(String phone, String loginPassword) throws Exception;
    //实名认证后，修改用户信息
    int modifyUser(User user);
    //用户登陆验证
    User queryUserByPhoneAndPwd(String phone, String loginPassword);
}
