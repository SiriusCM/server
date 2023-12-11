package org.sirius.server.dispatch;

import java.lang.annotation.*;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MsgId {
    int[] id();
}
