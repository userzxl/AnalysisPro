/**
 * 
 */
package com.tbox.echo;


import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.tbox.entity.CommandMean;
import com.tbox.entity.NettyChannel;
import com.tbox.service.Functions;
import com.tbox.sql.DBConn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;


/**
 * @author ZhaoXiaolong
 *
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	public Functions functions = new Functions();
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg){
		
		// msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中  
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()]; 
        result.readBytes(result1);  
        //转换成16进制存入字符串变量hex
        StringBuffer hex = functions.getHex(result1);
        System.out.println("会话信息："+hex);
       //解析命令标识
        byte commandCode = functions.hexAnalysis(result1[2]);
        //BCC校验
        boolean BCCValue = functions.booleanBCC(result1,functions.getBCC(result1));
        System.out.println("BCC验证码"+BCCValue);
        if(!BCCValue){
        	System.out.println("BCC校验错误");
        	functions.cxtResponse(result1, ctx, result, (byte)0x02);
        	return;
        }
        System.out.println("BCC校验通过");
        
        //解析VIN卡号（000000+sim号)
        byte[] simByte = new byte[11];
        System.arraycopy(result1, 10, simByte, 0, 11);
        String simCode = new String(simByte);
        int validateSim =  functions.validateSim(simCode);
        
        //如果不存在sim时，应答标识为0x02
        if(validateSim==0){
        	functions.cxtResponse(result1, ctx, result, (byte)0x02);
            System.out.println("不存在sim:"+simCode+",发送应答标识为0x02。。。");
        }else{
        	switch(commandCode) { 
	    	case CommandMean.finalValue.VEHICLE_LOGIN: 
	    		
	    		//validateSim == 1 应答0x01 否则 应答0x03
	    		if(validateSim==1){
	    			functions.cxtResponse(result1, ctx, result, (byte)0x01);
	                System.out.println("发送应答标识为0x01。。。");
	    			System.out.println("汽车登入");  
	    		}else{
	    			functions.cxtResponse(result1, ctx, result, (byte)0x03);
	                System.out.println("发送应答标识为0x03。。。");
	                System.out.println("汽车登入失败，该sim已存在。。。");
	    		}	    		  		
	    		return; 
	    	case CommandMean.finalValue.EVENT_REPORT: 
	    		System.out.println("事件数据上报");
	    		
	    		//事件发生时间
	    		byte[] eventTimeByte = new byte[6];
	            System.arraycopy(result1, 26, eventTimeByte, 0, 6);
	            StringBuffer eventTimeHex = functions.getHex(eventTimeByte);
	            String eventTimeStr = functions.sixteenToString(eventTimeByte);
	    		System.out.println("事件发生时间："+eventTimeStr);
	    		
	    		//事件属性：下发事件ID 0x0007 安全信息认证上报 0x0002 车辆通用报警(烟雾，振动，温度)
	    		byte[] eventIDByte = new byte[2];
	            System.arraycopy(result1, 54, eventIDByte, 0, 2);
	            int eventIDSixteenBit = functions.toSixteenBit(eventIDByte);	
	            System.out.println("事件ID："+eventIDSixteenBit);
	            
	            //事件数据体长度
	            byte[] eventDataL = new byte[2];
	            System.arraycopy(result1, 56, eventDataL, 0, 2);
	            int eventDataLSixteenBit = functions.toSixteenBit(eventDataL);	
	            System.out.println("事件数据体长度："+eventDataLSixteenBit);
	            if(eventIDSixteenBit==CommandMean.finalValue.SAFE_INFO_VALIDATE){
	            	System.out.println("接收到安全信息验证");
	            	byte encryptType = functions.hexAnalysis(result1[59]);
	            	if(encryptType == 0x01){
	            		//获取加密信息
	            		byte[] encryptData = new byte[16];
	    	            System.arraycopy(result1, 60, encryptData, 0, 16);
	    	            byte[] decryptData = functions.getDecCode(encryptData,functions.getRootKey(simCode));
	    	            String decryptDataHex = functions.getHex(decryptData).toString();	         	    		 
	    	    		System.out.println("加密信息解密后："+decryptDataHex);
	    	    		String timeSim = (eventTimeStr+simCode).replaceAll("0", "");
	    	    		System.out.println("加密信息解密后："+timeSim);
	    	    		if(timeSim.equals(decryptDataHex.toString().replaceAll("0", ""))){
	    	    			System.out.println("验证通过");	    	    				    	    				    	    	        	    	    	   	    	    			
	    	    			//下发会话密钥 23 23 E1 FE 30 30 30 30 30 30 36 34 36 31 30 39 39 32 31 30 33 01 00 1E 00 00 11 06 1B 0A 37 21 00 02 00 12 01 01 C2 80 F2 A4 7B 6E 1A 1B 43 B2 48 FE 9C 2D 2C C6 29
	    	    			/*byte[] issuedDataBody = */
	    	    			//绑定simCode
	    	    			ctx.channel().attr(AttributeKey.valueOf("simCode")).set(simCode);
	    	    			functions.updateOffline(simCode, "1");
	    	    			System.out.println(simCode+"上线");
	    	    			
	    	    			byte[] issuedData = functions.getIssuedData(result1,simCode);
	    	    			final ByteBuf data = ctx.alloc().buffer(issuedData.length); // (2)
	    	    	        data.writeBytes(issuedData);
	    	    	        ctx.write(data);
	    	    	        ctx.flush();
	    	    	        System.out.println("数据下发完成 to hex:"+functions.getHex(issuedData));
	    	    	        
	    	    	        functions.getAlarmSet(simCode, ctx);   	 //自定义参数设置   	        	    	    	        
	    	    	        
	    	    	        
	    	    		}else{
	    	    			try {
								functions.cxtResponse(result1, ctx, result, (byte)0x02);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    	                System.out.println("安全验证失败。。发送应答标识为0x02。。。");
	    	    		}
	            	}
	            }else if(eventIDSixteenBit==CommandMean.finalValue.VEHICLE_ALARM){
	            	System.out.println("这是报警事件!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	            	functions.cxtResponse(result1, ctx, result, (byte)0x01);
	            }else{
	            	eventIDByte = functions.getDecCode(eventIDByte, functions.getSessionKey(simCode));
	            	if(functions.toSixteenBit(eventIDByte) == CommandMean.finalValue.VEHICLE_ALARM){
	            		System.out.println("这是报警事件!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		            	functions.cxtResponse(result1, ctx, result, (byte)0x01);
		            	
		            	/*functions.getAlarmSet(simCode, ctx);   	 //自定义参数设置 
*/	            	}else{
	            		System.out.println("未知事件");
		            	functions.cxtResponse(result1, ctx, result, (byte)0x02);
	            	}
	            	
	            }
	    		
	            return;  
	    	case CommandMean.finalValue.BUS_DATA_REPORT:
	    		//总线数据单元长度
	            byte[] dataCellL = new byte[2];
	            System.arraycopy(result1, 22, dataCellL, 0, 2);
	            int dataCellLSixteenBit = functions.toSixteenBit(dataCellL);	
	            System.out.println("数据单元长度："+dataCellLSixteenBit);
	           /* byte[] enc = {(byte) 0xC2,(byte) 0x80,(byte) 0xF2,(byte) 0xA4,0x7B,0x6E,0x1A,0x1B,0x43,(byte) 0xB2,0x48,(byte) 0xFE,(byte) 0x9C,0x2D,0x2C,(byte) 0xC6};*///加密后的会话密钥
	            byte[] dec = functions.getSessionKey(simCode);//从数据库获取会话密钥
	            byte[] dataCell = new byte[dataCellLSixteenBit];
	            System.arraycopy(result1, 24, dataCell, 0, dataCell.length);
	            	            
	            dataCell = functions.getDecCode(dataCell,dec);  //解密后的数据单元
	            System.out.println("解密后的数据单元："+functions.getHex(dataCell));
	            
	    		//数据上报时间
	    		byte[] dataTimeByte = new byte[6];
	            System.arraycopy(dataCell, 2, dataTimeByte, 0, 6);
	            StringBuffer dataTimeHex = functions.getHex(dataTimeByte);
	            String dataTimeStr = functions.sixteenToString(dataTimeByte);
	    		System.out.println("事件发生时间："+dataTimeStr);
	            
	            //位置信息数据
	    		byte[] dataAddrByte = new byte[22];
	    		System.arraycopy(dataCell, 8, dataAddrByte, 0, 22);
	    		StringBuffer dataAddrHex = functions.getHex(dataAddrByte);
	    		System.out.println("位置信息数据："+dataAddrHex);
	    		
	    		//总线数据属性
	    		byte[] dataPropByte = new byte[14];
	    		System.arraycopy(dataCell, 30, dataPropByte, 0, 14);
	    		StringBuffer dataPropHex = functions.getHex(dataPropByte);
	    		System.out.println("总线数据属性："+dataPropHex);
	    		
	    		/**
	    		 * 总线数据体 
	    		 * 湿度	4	DWORD	
	    		 * 温度	4	DWORD	
		    		SOC	2	WORD	
		    		SOH	2	WORD	
		    		报警状态标识	2	BYTE	bit0：振动报警  0无     1有
		    		bit1：烟雾报警  0无     1有
		    		bit2：温度报警  0无     1有
	    		 */	    		
	    		byte[] dataBodyByte = new byte[16];
	    		System.arraycopy(dataCell, 44, dataBodyByte, 0, 16);
	    		StringBuffer dataBodyHex = functions.getHex(dataBodyByte);
	    		System.out.println("总线数据体："+dataBodyHex);//0000000BB0330770000  总线数据体
	    		
	    		try {
					functions.saveUpdateInfo(dataBodyByte, simCode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//总线数据存储
	    		
	    		functions.cxtResponse(result1, ctx, result, (byte)0x01);
	    		System.out.println("总线数据上报"); 
	    		functions.getAlarmSet(simCode, ctx);   	 //自定义参数设置   	  
	    		return; 
	    	case CommandMean.finalValue.VEHICLE_LOGOUT: 
	    		functions.cxtResponse(result1, ctx, result, (byte)0x01);
	    		System.out.println("车辆退出"); 
	    		return; 
	    	case CommandMean.finalValue.HEART_BEAT: 
	    		result.release();
	    		System.out.println("心跳"); 
	    		return;
	    	default: 
	    		result.release();
	    		System.out.println("啥也不是"); 
	    		return; 
	
			}         
	      
        }
      //获取 1.数据库采集频率 	2.温度报警阀值
        
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		//Close the connection when an exception is raised
		cause.printStackTrace();
		ctx.close();
	}
	/**
	 * 通信结束操作
	 */
	 public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	     //获取数据simCode
		 String simCode = (String) ctx.channel().attr(AttributeKey.valueOf("simCode")).get(); 
		
		 functions.updateOffline(simCode, "0");
		 System.out.println(simCode+"离线");
	 }
	 /**
	 * 通信建立时操作
	 * 建立定时器，定时扫描
	 */
	 public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		
		 /*		 final Timer timer = new HashedWheelTimer();
	        timer.newTimeout(new TimerTask() {
	            public void run(Timeout timeout) throws Exception {
	                System.out.println("timeout 5");
	            }
	        }, 5, TimeUnit.SECONDS);*/
	 }
}

/* System.out.println("to hex:"+hex);       


System.out.println("Client said sim:" + simCode);       											
DBConn dbConn = new DBConn("INSERT INTO testxl (name, age) VALUES ('"+simCode+"', '18')");
try {
	dbConn.pst.executeUpdate();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}finally{
	dbConn.close();
	System.out.println("关闭数据库连接");
}
// 释放资源，这行很关键   
String response = "I am ok!!!!!!";  
// 在当前场景下，发送的数据必须转换成ByteBuf数组  
ByteBuf encoded = ctx.alloc().buffer(4 * response.length());  
encoded.writeBytes(response.getBytes());  
ctx.write(encoded);  
ctx.flush(); 
System.out.println("发送。。。");*/

/*System.out.println("Client said by obj:" + result1);*///10410110810811132115101114118101114
//ascii
/*String byteString = new String();
for (int i = 0; i < result1.length; i++) {
	byteString+=result1[i];
}
System.out.println("Client said by byte:" + byteString); */
/*int bytelength = result1.length; 数据长度
System.out.println("Client said length:" + bytelength);*/
/*try {
	analysis.commandAnalysis((int)result1[2]&0xFF);
} catch (Exception e1) {
	// TODO Auto-generated catch block
	System.out.println("error");
}*/

//转换成16进制字符串
/*if(Integer.toHexString((int)result1[0]&0xFF).equals("23")){
	System.out.println("这是登录操作");
}else{
	System.out.println(Integer.toHexString((int)result1[0]&0xFF));
}*/
//转换字符串
/*String str = new String(result1);
System.out.println("Client said str:" + str);*/

