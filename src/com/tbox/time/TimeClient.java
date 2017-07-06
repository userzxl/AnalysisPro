/**
 * 
 */
package com.tbox.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ZhaoXiaolong
 * 2017-6-23上午10:39:00
 */
public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = "192.168.2.104";
        int port = 5000;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println(24);
        try {
        	System.out.println(26);
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            
            // Start the client.
            System.out.println(39);
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            System.out.println(41);
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
