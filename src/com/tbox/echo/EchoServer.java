/**
 * 
 */
package com.tbox.echo;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author ZhaoXiaolong
 * 2017-6-23上午9:31:27
 */
public class EchoServer {
	 private int port;
	    
	    public EchoServer(int port) {
	        this.port = port;
	    }
	    
	    public void run() throws Exception {
	        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap(); // (2)
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class) // (3)
	             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                	 ch.pipeline().addLast(new IdleStateHandler(0, 0, 190,TimeUnit.SECONDS));
	                	 ch.pipeline().addLast(new EchoIdleStateHandler());
	                     ch.pipeline().addLast(new EchoServerHandler());
	                     
	                 }
	             })
	             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
	             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
	    
	            // Bind and start to accept incoming connections.
	            System.out.println("服务器准备启动");
	            ChannelFuture f = b.bind(port).sync(); // (7)
	    
	            // Wait until the server socket is closed.
	            // In this example, this does not happen, but you can do that to gracefully
	            // shut down your server.
	            f.channel().closeFuture().sync();
	            System.out.println("关闭服务");
	        } finally {
	            workerGroup.shutdownGracefully();
	            bossGroup.shutdownGracefully();
	        }
	    }
	    
	    public static void main(String[] args) throws Exception {
	        int port;
	        if (args.length > 0) {
	            port = Integer.parseInt(args[0]);
	        } else {
	            port = 5000;
	        }
	        new EchoServer(port).run();
	    }
	}
