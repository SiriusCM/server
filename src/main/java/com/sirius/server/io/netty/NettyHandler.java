package com.sirius.server.io.netty;

import com.sirius.server.Msg;
import com.sirius.server.object.RoleObject;
import com.sirius.server.object.RoleState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class NettyHandler extends SimpleChannelInboundHandler<Msg.Message> {
    @Autowired
    private RoleObject roleObject;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        roleObject.setChannelHandlerContext(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Msg.Message message) throws Exception {
        roleObject.dispatchMsg(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        long now = System.currentTimeMillis();
        roleObject.setLogoutTime(now);
        roleObject.setRoleState(RoleState.OFFLINE);
    }
}
