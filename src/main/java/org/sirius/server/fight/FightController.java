package org.sirius.server.fight;


import org.sirius.server.bean.AutoBean;
import org.sirius.server.dispatch.MsgId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * @author gaoliandi
 * @time 2023/8/5
 */
@Controller
@Scope("prototype")
public class FightController {
    @AutoBean
    private FightService fightService;

    @MsgId(id = 1101)
    public void match(int msgId, byte[] data) throws InterruptedException {
        fightService.match(msgId, data);
    }

    @MsgId(id = 1103)
    public void enter(int msgId, byte[] data) {
        fightService.enter(msgId, data);
    }

    @MsgId(id = 1105)
    public void fight(int msgId, byte[] data) throws InterruptedException {
        fightService.fight(msgId, data);
    }
}
