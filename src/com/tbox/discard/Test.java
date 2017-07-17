/**
 * 
 */
package com.tbox.discard;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.io.IOException;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.tbox.echo.EchoIdleStateHandler;
import com.tbox.entity.CommandMean;
import com.tbox.entity.NettyChannel;
import com.tbox.service.Functions;
import com.tbox.sql.DBConn;
import com.tbox.sql.SqlFunctions;

/**
 * @author ZhaoXiaolong
 * 2017-6-28下午4:06:08
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Functions functions = new Functions();
		SqlFunctions sqlFunctions = new SqlFunctions();
		/*Test3 test = new Test3();
		
		try {
			test.outputExcelData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
		/*byte[] init = functions.getSessionKey();
		byte[] enc = functions.getEncCode(init);
		byte[] dec = functions.getDecCode(enc);
		System.out.println(functions.getHex(init)+"随机session_key");
		System.out.println(functions.getHex(enc)+"加密");
		System.out.println(functions.getHex(dec)+"解密");*/
		/*byte[] timeByte = functions.getLocalDate(functions.getLocalDate());*/
	/*	System.out.println(functions.getHex(timeByte));*/
		
		/*String asc = "171727111137";
		 int len = asc.length();  
	        int mod = len % 2;  
	        if (mod != 0) {  
	            asc = "0" + asc;  
	            len = asc.length();  
	        }  
	        byte abt[] = new byte[len];  
	        if (len >= 2) {  
	            len = len / 2;  
	        }  
	        byte bbt[] = new byte[len];  
	        abt = asc.getBytes();  
	        int j, k;  
	        for (int p = 0; p < asc.length() / 2; p++) {  
	            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
	                j = abt[2 * p] - '0';  
	            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
	                j = abt[2 * p] - 'a' + 0x0a;  
	            } else {  
	                j = abt[2 * p] - 'A' + 0x0a;  
	            }  
	            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
	                k = abt[2 * p + 1] - '0';  
	            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
	                k = abt[2 * p + 1] - 'a' + 0x0a;  
	            } else {  
	                k = abt[2 * p + 1] - 'A' + 0x0a;  
	            }  
	            int a = (j << 4) + k;  
	            byte b = (byte) a;  
	            bbt[p] = b;  
	        }
	       System.out.println(functions.getHex(bbt)); */
		
		/*
		StringBuffer temp = new StringBuffer(bytes.length * 2);  
        for (int i = 0; i < bytes.length; i++) {  
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));  
            temp.append((byte) (bytes[i] & 0x0f));  
        }  
        System.out.println(temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp  
                .toString().substring(1) : temp.toString());*/
		
		/*Test2 encryp = new Test2();
        String str ="functions";
        byte[] de = encryp.encryption(str);
        byte[] str1 = encryp.decryption(de);
		System.out.println(functions.getHex(str1));
		System.out.println(new String(str1));*/
		/*byte[] enc = {(byte) 0xC2,(byte) 0x80,(byte) 0xF2,(byte) 0xA4,0x7B,0x6E,0x1A,0x1B,0x43,(byte) 0xB2,0x48,(byte) 0xFE,(byte) 0x9C,0x2D,0x2C,(byte) 0xC6};
		System.out.println("会话密钥"+functions.getHex(functions.getDecCode(enc,functions.rootKey)));*/
		
		// TODO Auto-generated method stub
		//0xC2,0x80,0xF2,0xA4,0x7B,0x6E,0x1A,0x1B,0x43,0xB2,0x48,0xFE,0x9C,0x2D,0x2C,0xC6
		/*String data = "./cmake-3.1.1/mysql-5.6.15.tar.gz";*/
		/*System.out.println("0x"+data.replaceAll(" ", ",0x"));*/
		/*Functions functions = new Functions();*/
		
		/*byte[] a ={0x1E,0x3F,0x32,0x5D,(byte) 0x9F,0x6B,(byte) 0xA6,0x7A,(byte) 0xD8,(byte) 0xDF,0x3E,(byte) 0xEA,0x6D,0x09,0x60,(byte) 0xED};
		byte[] b = functions.getDecCode(a);
		String asd = null;
		for (int i = 0; i < b.length; i++) {
			asd+=b[i]+",";
		}
		System.out.println(asd);*/
		/*Functions functions = new Functions();
		functions.validateSim("23421");*/
		/*byte[] a ={(byte) 0x101};
		System.out.println(a.length);*///11061B0B0137
		
		/*byte[] hehe = {0x0A};
		
		System.out.println(functions.sixteenToTen("11061B0B0137"));*/
		
		
		/**
		 * 测试整形转byte[]
		 */
		/*System.out.println(functions.getHex(functions.intToByte2(256))+"");*/
		
		/*DBConn dbConn = new DBConn(
				"select * from bms_terminal");
		ResultSet result = null;
		String rootKey = "";
		try {
			result = dbConn.pst.executeQuery();
			int flag = 0;
			while (result.next()) {
				flag+=1;
				rootKey = result.getString("root_key");

			}
			System.out.println("flag:"+flag);
			System.out.println("rootKey:"+rootKey);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}*/
		/*sqlFunctions.getAlarmSet("64610992103");*/
			
		/*byte[] abc = {0x00,0x00,0x00,(byte) 0x11};
		System.out.println(functions.sixteenToTen(functions.sixteenToString(abc)));
*/
		/*System.out.println(functions.getUUID());*/
		
		int timeStamep = 1;
		DBConn dbConn = new DBConn(
				"update bms_monitor a set a.low = '"+timeStamep+"' where a.Id ='11111111111111111111111111111111'");
		int result1 = 0;
		try {
			result1 = dbConn.pst.executeUpdate();
			if (result1 == 1) {
				System.out.println("修改成功");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			dbConn.close();
			System.out.println("关闭数据库连接");
		}
		/*Attribute<NettyChannel> attr = ctx.attr(CommandMean.finalValue.NETTY_CHANNEL_KEY);  
        NettyChannel nChannel = null;
		try {
			nChannel = attr.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        if (nChannel == null) {  
            NettyChannel newNChannel = new NettyChannel("HelloWorld0Client", new Date());  
            nChannel = attr.setIfAbsent(newNChannel);  
        } else {  
            System.out.println("attributeMap 中是有值的");  
            System.out.println(nChannel.getName() + "=======" + nChannel.getCreateDate());  
        }  
        System.out.println("HelloWorldC0ientHandler Active");  */
		
	}

}
