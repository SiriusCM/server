package com.sirius.server.account;

import com.sirius.server.ioc.IRoleBean;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Data
@Scope("prototype")
@Service
public class AccountService implements IRoleBean {
    @Override
    public void init() {

    }
}
