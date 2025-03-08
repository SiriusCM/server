package com.sirius.server.account;

import com.sirius.server.IRoleBean;
import com.sirius.server.aop.MsgId;
import com.sirius.server.msg.Msg;
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

    @MsgId(id = Msg.Message.MsgIdCase.LOGIN_REQUEST)
    public void login(Msg.LoginRequest loginRequest) {

    }
}
