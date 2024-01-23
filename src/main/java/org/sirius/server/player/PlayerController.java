package org.sirius.server.player;

import org.sirius.server.bean.AutoBean;
import org.sirius.server.dispatch.MsgId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gaoliandi
 * @time 2023/6/29
 */
@Controller
@Scope("prototype")
public class PlayerController {
    @AutoBean
    private PlayerService playerService;

    @Transactional(rollbackFor = Exception.class)
    @MsgId(id = {1500, 1501})
    public void fun() {
        playerService.fun();
    }
}
