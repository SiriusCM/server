package com.sirius.server.io.netty;

import com.sirius.server.Config;
import com.sirius.server.MainThread;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NettyService implements CommandLineRunner {
    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;
    @Value("${server.gameport}")
    private int gameport;

    @Override
    public void run(String... args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        for (EventExecutor eventExecutor : workerGroup) {
            EventLoop eventLoop = (EventLoop) eventExecutor;
            MainThread mainThread = new MainThread("worker-" + eventLoop.hashCode());
            mainThread.start();
            Config.eventLoopMainThreadMap.put(eventLoop, mainThread);
        }
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    // 双向
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChunkedWriteHandler());

                    // 输入流
                    pipeline.addLast(new HttpObjectAggregator(64 * 1024));
//                    pipeline.addLast(new WebSocketServerCompressionHandler());
                    pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                    pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                            ByteBuf buf = frame.content();
                            objs.add(buf);
                            buf.retain();
                        }
                    });
//                    pipeline.addLast(new ProtobufVarint32FrameDecoder());
//                    pipeline.addLast(new ProtobufDecoder(Msg.Message.getDefaultInstance()));
//                    pipeline.addLast(new NettyHandler());
                    pipeline.addLast(configurableApplicationContext.getBean(NettyHandler.class));

//                    //输出流
//                    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
//                    pipeline.addLast(new ProtobufEncoder());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(gameport).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
