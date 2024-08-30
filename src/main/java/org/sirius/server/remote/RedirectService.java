package org.sirius.server.remote;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Aspect
@Scope("prototype")
public class RedirectService implements DisposableBean {
    private Channel proxyChannel;

    @AfterReturning(value = "@annotation(org.sirius.server.remote.Connect)", returning = "proxyChannel")
    public void connect(Channel proxyChannel) {
        this.proxyChannel = proxyChannel;
    }

    @Around("@annotation(org.sirius.server.remote.Redirect) && args(msgId, data)")
    public void redirect(ProceedingJoinPoint joinPoint, int msgId, byte[] data) throws Throwable {
        if (proxyChannel == null) {
            joinPoint.proceed();
        } else {
            ByteBuf byteBuf = Unpooled.directBuffer();
            byteBuf.writeInt(msgId);
            byteBuf.writeBytes(data);
            proxyChannel.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void destroy() throws Exception {
        proxyChannel.close();
    }
}
