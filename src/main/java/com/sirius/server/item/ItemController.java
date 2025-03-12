package com.sirius.server.item;

import com.sirius.server.ioc.AutoBean;
import com.sirius.server.ioc.IRoleBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class ItemController implements IRoleBean {
    @AutoBean
    private ItemService itemService;

    @Override
    public void init() {

    }
}
