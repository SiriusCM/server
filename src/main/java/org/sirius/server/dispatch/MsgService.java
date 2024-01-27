package org.sirius.server.dispatch;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.sirius.server.cache.CachingService;
import org.sirius.server.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    @Autowired
    private Map<Long, DataService> dataServiceMap;
    private DataService dataService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        long playerId = 18L;
        if (dataServiceMap.containsKey(playerId)) {
            dataService = dataServiceMap.get(playerId);
        } else {
            dataService = configurableListableBeanFactory.createBean(DataService.class);
            dataService.getBeanPool().put(DataService.class, dataService);
            dataService.getBeanPool().put(MsgService.class, this);
            dataService.getBeanPool().put(ChannelHandlerContext.class, ctx);
            dataService.getBeanPool().put(long.class, playerId);
            dataService.autowireBean(dataService);
            dataServiceMap.put(playerId, dataService);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        int msgId = byteBuf.readInt();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        dispatchService.dispatchMsg(dataService, msgId, data);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dataService.setInactiveTimeStamp(System.currentTimeMillis());
    }
}