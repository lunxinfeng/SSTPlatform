package com.fintech.sst.other.netty;

import android.content.Context;
import android.util.Log;

import com.fintech.sst.other.netty.handler.ClientHeartBeatHandler;
import com.fintech.sst.other.netty.handler.MessageChannelHandler;
import com.fintech.sst.other.netty.handler.MessageConvert;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyConnectionFactory implements ChannelFutureListener {
    private int alreadyRetry = 0;
    private Bootstrap bootstrap;
    private Channel channel;
    private TcpConnection connection;
    private NioEventLoopGroup eventLoopGroup;
    private int retryConnect = 5;
    private int retryTime = 3;
    private ServerMessageHandler serverMessageHandler;
    private Context context;

    public NettyConnectionFactory(TcpConnection var1, ServerMessageHandler var2, Context context) {
        this.connection = var1;
        this.serverMessageHandler = var2;
        this.context = context;
        this.init();
    }

    private void createChannel() {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close();
            this.channel.disconnect();
        }
        try {
            this.bootstrap.connect(this.connection.getHost(), this.connection.getPort()).sync().addListener(this);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }
    }

    private void init() {
        this.bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap.group(this.eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                String token = NettyConnectionFactory.this.connection.getAuthToken();
                Log.i("XposedData-->终端编号::",token);
                if(!StringUtils.isNotBlank(token)){
                    token = AbSharedUtil.getString(context, "userIdStr");
                    NettyConnectionFactory.this.connection.setAuthToken(token);
                    Log.i("XposedData-->重新设置终端编号",token);
                }
                ClientHeartBeatHandler clientHeartBeatHandler =  new ClientHeartBeatHandler(token);
                clientHeartBeatHandler.setAuthToken(token);

                channel.pipeline().addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder(Charset.forName("UTF-8")))
                        .addLast(new IdleStateHandler(0L, 5L, 0L, TimeUnit.SECONDS))
                        .addLast(clientHeartBeatHandler)
                        .addLast(new MessageChannelHandler(NettyConnectionFactory.this.serverMessageHandler,context));
            }
        });
        this.createChannel();
    }

    public void closeChannel() {
        if (this.channel != null) {
            this.channel.close();
            this.channel.disconnect();
        }
    }

    public void closeCurrentChannel() {
        if (this.channel != null) {
            this.channel.attr(Attributes.NORMAL_CLOSE).set(true);
            this.channel.close();
        }
    }

    public String getAuthToken() {
        return this.connection.getAuthToken();
    }

    public Channel getChannel() {
        if (this.channel != null && this.channel.isActive()) {
            return this.channel;
        } else {
            try {
                this.createChannel();
                Thread.sleep(500L);
                if (this.channel != null && this.channel.isActive()) {
                    Channel var1 = this.channel;
                    return var1;
                }
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            return null;
        }
    }

    public boolean isActive() {
        return this.channel.isActive();
    }

    public void operationComplete(ChannelFuture var1) throws Exception {
        if (var1.isSuccess()) {
            this.channel = var1.channel();
            System.out.println("云闪付:11");
            this.channel.writeAndFlush(Unpooled.copiedBuffer(MessageConvert.convertAuthMsg(this.connection.getAuthToken())));
            this.alreadyRetry = 0;
            this.serverMessageHandler.afterConnectionEstablished(ConnectionStatus.CONNECTION_SUCCESS);
        } else if (this.alreadyRetry < this.retryConnect) {
            var1.channel().eventLoop().schedule(new Runnable() {
                public void run() {
                    NettyConnectionFactory.this.createChannel();
                }
            }, (long) this.retryTime, TimeUnit.SECONDS);
        } else {
            this.serverMessageHandler.afterConnectionEstablished(ConnectionStatus.CONNECTION_ERROR);
        }
    }
}