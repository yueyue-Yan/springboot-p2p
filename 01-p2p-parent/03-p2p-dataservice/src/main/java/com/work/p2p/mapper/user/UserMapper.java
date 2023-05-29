package com.work.p2p.mapper.user;

import com.work.p2p.beans.user.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //查询平台的用户数量
    Integer selectAllUserCount();
    //根据手机号返回user对象
    User selectUserByPhone(@Param("phone") String phone);
    //根据手机号和密码查询user对象
    User selectUserByPhoneAndPwd(@Param("phone") String phone, @Param("loginPassword")String loginPassword);
}