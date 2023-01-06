package com.dail.starter.cache.mapper;

import com.dail.starter.cache.dao.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description mapper类
 *
 * @author Dail 2023/01/06 13:36
 */
@Mapper
public interface UserInfoMapper {
    int addUser(UserInfo userInfo);
    int updateUser(UserInfo userInfo);
    List<UserInfo> queryByUserInfo(@Param("userInfo") UserInfo userInfo);
}
