package com.sirius.server.item;

import com.sirius.server.ioc.IRoleBean;
import com.sirius.server.aop.MsgId;
import com.sirius.server.ioc.AutoCache;
import com.sirius.server.Msg;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Scope("prototype")
@Service
public class ItemService implements IRoleBean {

    @AutoCache
    private List<Item> itemList;

    @Override
    public void init() {

    }

    @MsgId(id = Msg.Message.MsgIdCase.CHAT_MESSAGE)
    public void delItem(Msg.ChatMessage chatMessage) {

    }
}
