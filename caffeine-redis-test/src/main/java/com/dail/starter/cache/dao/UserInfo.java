package com.dail.starter.cache.dao;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description 用户表DAO
 *
 * @author Dail 2023/01/06 13:35
 */
@Data
@Accessors(chain = true)
public class UserInfo {
    private Integer userId;
    private String userName;
}
