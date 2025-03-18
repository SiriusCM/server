package com.sirius.server.aop;

import com.sirius.server.Msg;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MsgId {
    Msg.Message.MsgIdCase id();
}
