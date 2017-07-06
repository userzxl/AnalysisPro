/**
 * 
 */
package com.tbox.time;

import java.util.Date;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.tbox.service.Functions;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author ZhaoXiaolong
 * 2017-6-23上午10:40:12
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	public Functions functions = new Functions();
	@Override
	public void channelActive(final ChannelHandlerContext ctx) { // (1)
		System.out.println("已连接。。。");
		//登入数据      30 30 30 30 30 30 36 34 36 31 30 39 39 32 31 30 33                                                                                                                                                               38 39 38 36 30 36 31 36 30 31 30 30 35 33 38 30 37 39 31 39 01 00 E6                                                                               
		byte[] infochar = {0x23,0x23,0x01,(byte) 0xFE,0x30,0x30,0x30,0x30,0x30,0x30,0x36,0x34,0x36,0x31,0x30,0x39,0x39,0x32,0x31,0x30,0x33,0x01,0x00,0x1E, /*数据单元长度0x1E*/ 0x11,0x06,0x1B,0x0B,0x01,0x36,0x00,0x05,/*ICCID-STart*/0x38,0x39,0x38,0x36,0x30,0x36,0x31,0x36,0x30,0x31,0x30,0x30,0x35,0x33,0x38,0x30,0x37,0x39,0x31,0x39,/*end*/0x01,0x00,(byte) 0xE6};
		
        final ByteBuf data = ctx.alloc().buffer(infochar.length); // (2)
        data.writeBytes(infochar);
        ctx.write(data);
        ctx.flush();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	System.out.println(1900);
        ByteBuf reply =  (ByteBuf)msg;
        try {
            byte[] result = new byte[reply.readableBytes()];
            reply.readBytes(result);  
            StringBuffer hex = new StringBuffer();
            for(int i = 0; i < result.length; i++){
        	    hex.append(Integer.toHexString((int)result[i]&0xFF).toUpperCase());
        	 }
            String resultStr = new String(result);
            System.out.println("Server said:" + resultStr);  
            System.out.println("Server said by hex:" + hex); 
            byte replyCode = functions.hexAnalysis(result[3]);
            byte commandCode = functions.hexAnalysis(result[2]);
            if(replyCode==0x01&&commandCode==0x01){
            	System.out.println("接收登录应答");//0x00 0x01 0x11 0x06 0x1B 0x0B 0x01 0x37 0x00 0x00 0x00 0x11 0x00 0x00 0x00 0x01 0x02 0x9C 0x23 0x91 0x07 0x79 0xC4 0xBA 0x00 0xFA 0x00 0x00 0x00 0x00 0x00 0x07 0x00 0x12 0x00 0x01 0x1E 0x3F 0x32 0x5D 0x9F 0x6B 0xA6 0x7A 0xD8 0x
            	//23 23 D2 FE 30 30 30 30 30 30 36 34 36 31 30 39 39 32 31 30 33 sim/ 01/不加密   00 34/数据单元长度   00 01 11 06 1B 0B 01 37 00 00 00 11 00 00 00 01 02 9C 23 91 07 79 C4 BA 00 FA 00 00 00 00 00 07 00 12 00 01 1E 3F 32 5D 9F 6B A6 7A D8 DF 3E EA 6D 09 60 ED 92 , rootKey=19 C1 4D AA B0 3C 77 43 68 5E 8B F3 49 E4 9D 2A
            	byte[] infochar = {0x23,0x23,(byte) 0xD2,(byte) 0xFE,0x30,0x30,0x30,0x30,0x30,0x30,0x36,0x34,0x36,0x31,0x30,0x39,0x39,0x32,0x31,0x30,0x33,0x01,0x00,0x34,0x00,0x01,0x11,0x06,0x1B,0x0B,0x01,0x37,0x00,0x00,0x00,0x11,0x00,0x00,0x00,0x01,0x02,(byte) 0x9C,0x23,(byte) 0x91,0x07,0x79,(byte) 0xC4,(byte) 0xBA,0x00,(byte) 0xFA,0x00,0x00,0x00,0x00,0x00,0x07,0x00,0x12,0x00,0x01,0x1E,0x3F,0x32,0x5D,(byte) 0x9F,0x6B,(byte) 0xA6,0x7A,(byte) 0xD8,(byte) 0xDF,0x3E,(byte) 0xEA,0x6D,0x09,0x60,(byte) 0xED,(byte) 0x92};
        		
                final ByteBuf data = ctx.alloc().buffer(infochar.length); // (2)
                data.writeBytes(infochar);
                ctx.write(data);
                ctx.flush();
                System.out.println("事件上报（安全验证上报）");
            }else if(replyCode==(byte)0xFE&&commandCode==(byte)0xE1){
            	System.out.println("接收数据下发（临时会话密钥）");
            	byte[] sessionKey = new byte[16];
            	System.arraycopy(result, 38, sessionKey, 0, 16);
            	sessionKey = functions.getDecCode(sessionKey,functions.rootKey);
            	System.out.println("会话密钥是："+functions.getHex(sessionKey));
            	
            	byte[] infochar = {0x23,0x23,(byte) 0xD0,(byte) 0xFE,0x30,0x30,0x30,0x30,0x30,0x30,0x36,0x34,0x36,0x31,0x30,0x39,0x39,0x32,0x32,0x30,0x39,//唯一识别码-end
            			0x03,0x00,(byte) 0xD0,//数据单元长度-end
            			0x64,(byte) 0xE9,
            			0x62,0x58,(byte) 0xF7,0x16,(byte) 0x92,0x24,(byte) 0xFF,0x69,(byte) 0xDB,0x1E,0x54,0x61,0x40,0x6F,0x52,0x04,(byte) 0xE9,0x67,0x0F,0x5B,(byte) 0xB6,(byte) 0xD1,
            			(byte) 0x92,(byte) 0xCB,0x6D,0x28,(byte) 0xDD,0x69,0x52,0x78,(byte) 0x98,(byte) 0xDD,(byte) 0x83,(byte) 0xA6,(byte) 0xBC,0x6B,0x09,0x7D,(byte) 0xF7,(byte) 0xB1,0x55,
            			(byte) 0xD1,(byte) 0xB5,0x77,0x30,0x13,(byte) 0xD4,(byte) 0xF6,0x39,(byte) 0xAC,(byte) 0xAC,0x67,0x29,0x12,(byte) 0xC2,0x53,(byte) 0x9B,(byte) 0x8B,(byte) 0xCF,(byte) 0xCD,
            			0x4F,0x01,0x0C,(byte) 0xD8,(byte) 0xA4,(byte) 0xA5,(byte) 0xD1,(byte) 0x9A,0x20,0x57,(byte) 0x81,0x0C,0x45,(byte) 0xF5,0x46,(byte) 0xA0,(byte) 0xD2,0x10,(byte) 0xA4,0x1C,
            			0x7C,(byte) 0xE4,0x31,0x46,0x48,0x4C,(byte) 0xA2,0x1D,(byte) 0xC8,(byte) 0xF6,0x4B,(byte) 0xCA,(byte) 0xA1,0x27,(byte) 0xF7,(byte) 0x83,(byte) 0x99,0x3A,(byte) 0x80,0x7A,
            			(byte) 0xDB,0x56,(byte) 0xA7,(byte) 0xD4,0x47,0x19,(byte) 0xC4,(byte) 0xE3,0x43,0x1C,(byte) 0xE0,(byte) 0x88,0x48,(byte) 0xF8,0x73,0x0E,(byte) 0xA3,(byte) 0xDF,0x21,0x7A,
            			(byte) 0x92,0x06,(byte) 0xA7,(byte) 0xC2,(byte) 0xBF,0x28,(byte) 0x8F,0x4E,0x5E,0x5A,(byte) 0x8A,(byte) 0xD3,0x44,0x40,0x32,(byte) 0x95,(byte) 0xAE,(byte) 0xCA,(byte) 0xBB,
            			0x19,(byte) 0xD8,0x3B,(byte) 0xCD,(byte) 0xD7,(byte) 0xC2,(byte) 0xB3,0x32,0x6B,0x2E,0x0A,(byte) 0xEB,(byte) 0x84,(byte) 0x84,(byte) 0xC1,(byte) 0x9F,(byte) 0x9A,0x6E,0x30,
            			0x5A,0x27,0x56,0x59,0x56,0x2B,0x39,(byte) 0xE6,(byte) 0x86,0x2D,(byte) 0xC7,(byte) 0xAA,0x5F,0x78,(byte) 0xCB,(byte) 0x82,(byte) 0xEE,0x69,0x47,(byte) 0xE8,0x34,0x16,0x5C,
            			0x61,(byte) 0xF0,(byte) 0x80,(byte) 0x9F,0x2E,0x7D,(byte) 0xF3,(byte) 0xDB,0x28,(byte) 0xCC,(byte) 0x85,(byte) 0xD8,(byte) 0x80,0x5F,(byte) 0xBA,0x79,0x41,(byte) 0x9C,0x1F,
            			(byte) 0x8F,0x78,0x2E,(byte) 0xB3,(byte) 0x9E,(byte) 0x97,(byte) 0xFD};
        		
                final ByteBuf data = ctx.alloc().buffer(infochar.length); // (2)
                data.writeBytes(infochar);
                ctx.write(data);
                ctx.flush();
            }
        } finally {
        	/*ctx.close();*/
        	// 释放资源，这行很关键  
        	reply.release();
        }
    } 
    //总线上报数据包
    //0x23,0x23,0xD0,0xFE,0x30,0x30,0x30,0x30,0x30,0x30,0x36,0x34,0x36,0x31,0x30,0x39,0x39,0x32,0x32,0x30,0x39,0x03,0x00,0xD0,0x64,0xE9,0x62,0x58,0xF7,0x16,0x92,0x24,0xFF,0x69,0xDB,0x1E,0x54,0x61,0x40,0x6F,0x52,0x04,0xE9,0x67,0x0F,0x5B,0xB6,0xD1,0x92,0xCB,0x6D,0x28,0xDD,0x69,0x52,0x78,0x98,0xDD,0x83,0xA6,0xBC,0x6B,0x09,0x7D,0xF7,0xB1,0x55,0xD1,0xB5,0x77,0x30,0x13,0xD4,0xF6,0x39,0xAC,0xAC,0x67,0x29,0x12,0xC2,0x53,0x9B,0x8B,0xCF,0xCD,0x4F,0x01,0x0C,0xD8,0xA4,0xA5,0xD1,0x9A,0x20,0x57,0x81,0x0C,0x45,0xF5,0x46,0xA0,0xD2,0x10,0xA4,0x1C,0x7C,0xE4,0x31,0x46,0x48,0x4C,0xA2,0x1D,0xC8,0xF6,0x4B,0xCA,0xA1,0x27,0xF7,0x83,0x99,0x3A,0x80,0x7A,0xDB,0x56,0xA7,0xD4,0x47,0x19,0xC4,0xE3,0x43,0x1C,0xE0,0x88,0x48,0xF8,0x73,0x0E,0xA3,0xDF,0x21,0x7A,0x92,0x06,0xA7,0xC2,0xBF,0x28,0x8F,0x4E,0x5E,0x5A,0x8A,0xD3,0x44,0x40,0x32,0x95,0xAE,0xCA,0xBB,0x19,0xD8,0x3B,0xCD,0xD7,0xC2,0xB3,0x32,0x6B,0x2E,0x0A,0xEB,0x84,0x84,0xC1,0x9F,0x9A,0x6E,0x30,0x5A,0x27,0x56,0x59,0x56,0x2B,0x39,0xE6,0x86,0x2D,0xC7,0xAA,0x5F,0x78,0xCB,0x82,0xEE,0x69,0x47,0xE8,0x34,0x16,0x5C,0x61,0xF0,0x80,0x9F,0x2E,0x7D,0xF3,0xDB,0x28,0xCC,0x85,0xD8,0x80,0x5F,0xBA,0x79,0x41,0x9C,0x1F,0x8F,0x78,0x2E,0xB3,0x9E,0x97,0xFD
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
