package org.sirius.server.dispatch;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.sirius.server.bean.BeanService;
import org.sirius.server.cache.CachingService;
import org.sirius.server.event.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */
@Service
@Scope("prototype")
public class MsgService extends SimpleChannelInboundHandler<ByteBuf> {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private CachingService cachingService;
    @Autowired
    private DispatchService dispatchService;
    private BeanService beanService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (cachingService.getBeanServiceMap().containsKey(18L)) {
            beanService = cachingService.getBeanServiceMap().get(18L);
        } else {
            beanService = configurableListableBeanFactory.createBean(BeanService.class);
            beanService.getBeanPool().put(BeanService.class, beanService);
            beanService.getBeanPool().put(MsgService.class, this);
            beanService.getBeanPool().put(ChannelHandlerContext.class, ctx);
            beanService.getBeanPool().put(long.class, 18L);
            beanService.autowireBean(beanService);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        int msgId = byteBuf.readInt();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        dispatchService.dispatchMsg(beanService, msgId, data);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        beanService.setInactiveTimeStamp(System.currentTimeMillis());
    }
}