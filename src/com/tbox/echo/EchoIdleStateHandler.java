/**
 * 
 */
package com.tbox.echo;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author ZhaoXiaolong
 * 2017-6-29下午1:39:06
 */
public class EchoIdleStateHandler extends ChannelInboundHandlerAdapter  {

	/**
	 * @param readerIdleTime
	 * @param writerIdleTime
	 * @param allIdleTime
	 * @param unit
	 */
	
	
	@Override  
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  
            throws Exception {  
        // TODO Auto-generated method stub  
        super.userEventTriggered(ctx, evt);  
        if (evt instanceof IdleStateEvent) {  
  
            IdleStateEvent event = (IdleStateEvent) evt;  
              
            if (event.state().equals(IdleState.READER_IDLE)) {  
                //未进行读操作  
                System.out.println("没有进行读写操作，关闭通信");  
                // 超时关闭 
                 ctx.close();  
  
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {  
                  
  
            } else if (event.state().equals(IdleState.ALL_IDLE)) {  
                //未进行读写  
            	System.out.println("没有进行读写操作，关闭通信");  
                // 超时关闭 
                 ctx.close(); 
             
                  
            }  
  
        }  
    }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}  

}
