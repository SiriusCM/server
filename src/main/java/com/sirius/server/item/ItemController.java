package com.sirius.server.item;

import com.sirius.server.ioc.IRoleBean;
import com.sirius.server.ioc.AutoBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class ItemController implements IRoleBean {
    @AutoBean
    private ItemService itemService;
    @AutoBean
    private ItemService itemService1;

    @Override
    public void init() {

    }
}
