package org.sirius.server.boot;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.sirius.server.dispatch.DispatchService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author gaoliandi
 * @time 2023/6/28
 */

@Service
public class BootService implements DisposableBean {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    @Qualifier("boss")
    private NioEventLoopGroup bossNioEventLoopGroup;
    @Autowired
    @Qualifier("worker")
    private NioEventLoopGroup workerNioEventLoopGroup;
    @Value("${tcp.port}")
    private int port;
    private Channel serverChannel;

    @Async
    @EventListener({ApplicationReadyEvent.class})
    public void onReady(ApplicationReadyEvent event) throws InterruptedException {
        serverChannel = listen(port);
    }

    public Channel listen(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossNioEventLoopGroup, workerNioEventLoopGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4));
                pipeline.addLast("encoder", new LengthFieldPrepender(4, false));
                pipeline.addLast("handler", configurableListableBeanFactory.createBean(DispatchService.class));
            }
        });
        return bootstrap.bind(port).sync().channel();
    }

    public Channel connect(String host, int port, ChannelHandlerContext proxyCtx) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerNioEventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("encoder", new LengthFieldPrepender(4, false));
                pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4));
                pipeline.addLast("proxy", new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        proxyCtx.writeAndFlush(byteBuf);
                    }
                });
            }
        });
        return bootstrap.connect(host, port).sync().channel();
    }

    @Override
    public void destroy() throws Exception {
        bossNioEventLoopGroup.shutdownGracefully();
        workerNioEventLoopGroup.shutdownGracefully();
    }
}