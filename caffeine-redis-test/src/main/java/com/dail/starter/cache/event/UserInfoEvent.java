package com.dail.starter.cache.event;

import com.dail.starter.cache.dao.UserInfo;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * description 用户信息事件
 *
 * @author Dail 2023/01/06 14:21
 */
public class UserInfoEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserInfoEvent(List<UserInfo> source) {
        super(source);
    }
}
