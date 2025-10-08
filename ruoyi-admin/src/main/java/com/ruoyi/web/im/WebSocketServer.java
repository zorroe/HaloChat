package com.ruoyi.web.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebSocketServer implements SmartLifecycle {

    @Value("${im.netty.port:18080}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile boolean running = false;

    @Override
    public void start() {
        if (running) return;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new WebSocketServerProtocolHandler("/chat", null, true))
                                    .addLast(new WebSocketIdleStateHandler(30, 0, 0, TimeUnit.MINUTES))
                                    .addLast(new WebSocketServerHandler());
                        }
                    });
            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("WebSocket Server started at port " + port);
                } else {
                    System.out.println("WebSocket Server start failed");
                }
            });
            running = true;
            // do not block here; lifecycle will manage shutdown
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        if (!running) return;
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
