package org.sirius.server.dispatch;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.sirius.server.bean.BeanService;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */
@Service
@Scope("prototype")
public class DispatchService extends SimpleChannelInboundHandler<ByteBuf> {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private CachingService cachingService;
    private BeanService beanService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        beanService = configurableListableBeanFactory.createBean(BeanService.class);
        beanService.getBeanPool().put(BeanService.class, beanService);
        beanService.getBeanPool().put(ChannelHandlerContext.class, ctx);
        beanService.getBeanPool().put(long.class, 18L);
        beanService.autowireBean(beanService);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        int msgId = byteBuf.readInt();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        dispatch(beanService, msgId, data);
    }

    public void dispatch(BeanService beanService, int msgId, byte[] data) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        //CodedInputStream codedInputStream = CodedInputStream.newInstance(data, 4, data.length - 4);
        beanService.getBeanPool().put(int.class, msgId);
        beanService.getBeanPool().put(byte[].class, data);
        for (Method method : cachingService.getDispatchMethods(msgId)) {
            method.invoke(beanService.getBean(method.getDeclaringClass()), beanService.getParameters(method));
        }
    }
}