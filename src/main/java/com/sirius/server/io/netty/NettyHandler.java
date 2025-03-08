package com.sirius.server.io.netty;

import com.sirius.server.Config;
import com.sirius.server.MainThread;
import com.sirius.server.msg.Msg;
import com.sirius.server.object.RoleObject;
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

    private MainThread mainThread;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.mainThread = Config.eventLoopMainThreadMap.get(ctx.channel().eventLoop());
        roleObject.setChannelHandlerContext(ctx);
        roleObject.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg.Message message) throws Exception {
        mainThread.getConsumerQueue().add(t -> roleObject.dispatchMsg(message));
    }
}
